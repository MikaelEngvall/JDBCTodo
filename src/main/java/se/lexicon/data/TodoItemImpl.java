package se.lexicon.data;

import se.lexicon.db.MySQLConnection;
import se.lexicon.model.Person;
import se.lexicon.model.Todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TodoItemImpl implements TodoItem{

    @Override
    public Todo create(Todo todo) {
        // Check if a task with the same title, description, and assignee ID already exists
        if (!taskExists(todo.getTitle(), todo.getDescription(), todo.getAssignee_id().getId())) {
            String query = "INSERT INTO todo_item(title, description, deadline, done, assignee_id) VALUES(?, ?, ?, ?, ?)";
            try (
                    Connection connection = MySQLConnection.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
            ) {
                preparedStatement.setString(1, todo.getTitle());
                preparedStatement.setString(2, todo.getDescription());
                preparedStatement.setString(3, String.valueOf(todo.getDeadline()));
                preparedStatement.setInt(4, todo.isDone() ? 1 : 0); // Use 1 for true, 0 for false
                preparedStatement.setInt(5, todo.getAssignee_id().getId());

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Todo created successfully!");
                }

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedTodoId = generatedKeys.getInt(1);
                        System.out.println("generatedTodoId = " + generatedTodoId);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("A task with the same title, description, and assignee ID already exists.");
        }
        return todo;
    }


    @Override
    public Todo update(Todo todo) {
        if (todoExists(todo.getId())) {
            String query = "UPDATE todo_item SET title = ?, description = ?, deadline = ?, done = ?, assignee_id = ? WHERE todo_id = ?";
            try (
                    Connection connection = MySQLConnection.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(query)
            ) {
                preparedStatement.setString(1, todo.getTitle());
                preparedStatement.setString(2, todo.getDescription());
                preparedStatement.setString(3, String.valueOf(todo.getDeadline()));
                preparedStatement.setInt(4, todo.isDone() ? 1 : 0); // Use 1 for true, 0 for false
                preparedStatement.setInt(5, todo.getAssignee_id().getId());
                preparedStatement.setInt(6, todo.getId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Todo with ID " + todo.getId() + " updated successfully!");
                    return todo;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Todo with ID " + todo.getId() + " not found. Update failed.");
        }
        return null;
    }


    @Override
    public Collection<Todo> findAll() {
        List<Todo> allTasks = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM todo_item")) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("todo_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate deadline = resultSet.getDate("deadline").toLocalDate();
                    boolean done = resultSet.getBoolean("done");
                    int assigneeId = resultSet.getInt("assignee_id");

                    // Retrieve the assignee information from the Person table
                    Person assignee = getPersonById(assigneeId);

                    Todo todo = new Todo(id, title, description, deadline, done, assignee);
                    allTasks.add(todo);

                    // Print out all the fields for the retrieved task
                    System.out.println("Found Task: ");
                    System.out.println("ID: " + todo.getId());
                    System.out.println("Title: " + todo.getTitle());
                    System.out.println("Description: " + todo.getDescription());
                    System.out.println("Deadline: " + todo.getDeadline());
                    System.out.println("Done: " + todo.isDone());
                    System.out.println("Assignee: " + assignee.getFirstName() + " " + assignee.getLastName());
                    System.out.println("-----------------------------------------");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Print out the found tasks
        if (allTasks.isEmpty()) {
            System.out.println("No tasks found");
        }
        return allTasks;
    }


    @Override
    public Collection<Todo> findByDoneStatus(boolean done) {
        List<Todo> tasksByStatus = new ArrayList<>();

        String query = "SELECT * FROM todo_item WHERE done = ?";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, done ? 1 : 0); // Use 1 for true, 0 for false

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("todo_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate deadline = resultSet.getDate("deadline").toLocalDate();
                    int assigneeId = resultSet.getInt("assignee_id");

                    // Retrieve the assignee information from the Person table
                    Person assignee = getPersonById(assigneeId);

                    Todo todo = new Todo(id, title, description, deadline, done, assignee);
                    tasksByStatus.add(todo);

                    // Print out all the fields for the retrieved task
                    System.out.println("Found Task: ");
                    System.out.println("ID: " + todo.getId());
                    System.out.println("Title: " + todo.getTitle());
                    System.out.println("Description: " + todo.getDescription());
                    System.out.println("Deadline: " + todo.getDeadline());
                    System.out.println("Done: " + todo.isDone());
                    System.out.println("Assignee: " + assignee.getFirstName() + " " + assignee.getLastName());
                    System.out.println("-----------------------------------------");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Print out the found tasks
        if (tasksByStatus.isEmpty()) {
            System.out.println("No tasks found with done status: " + done);
        }
        return tasksByStatus;
    }


    @Override
    public Collection<Todo> findByAssignee(int id) {
        return null;
    }


    @Override
    public Collection<Todo> findByAssignee(Person person) {
        return null;
    }

    @Override
    public Collection<Todo> findByUnassignedTodoItems() {
        return null;
    }

    @Override
    public Todo findById(int id) {
        return null;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    // Helper method to check if a task with the given ID exists
    private boolean todoExists(int id) {
        String query = "SELECT COUNT(*) FROM todo_item WHERE todo_id = ?";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to check if a task with the same title, description, and assignee ID exists
    private boolean taskExists(String title, String description, int assigneeId) {
        String query = "SELECT COUNT(*) FROM todo_item WHERE title = ? AND description = ? AND assignee_id = ?";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, assigneeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private Person getPersonById(int id) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM person WHERE person_id = ?")) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int personId = resultSet.getInt("person_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    return new Person(personId, firstName, lastName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no matching person is found
    }

}
