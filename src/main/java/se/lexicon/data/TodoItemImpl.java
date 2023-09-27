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
import java.util.Objects;

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

                    printout(todo, Objects.requireNonNull(assignee));
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

                    printout(todo, Objects.requireNonNull(assignee)); // So we don't get nullpointererror if null
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (tasksByStatus.isEmpty()) {
            System.out.println("No tasks found with done status: " + done);
        }
        return tasksByStatus;
    }

    @Override
    public Collection<Todo> findByAssignee(int id) {
        List<Todo> tasksByAssignee = new ArrayList<>();

        String query = "SELECT * FROM todo_item WHERE assignee_id = ?";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int taskId = resultSet.getInt("todo_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate deadline = resultSet.getDate("deadline").toLocalDate();
                    boolean done = resultSet.getBoolean("done");

                    // Retrieve the assignee information from the Person table
                    Person assignee = getPersonById(id);

                    Todo todo = new Todo(taskId, title, description, deadline, done, assignee);
                    tasksByAssignee.add(todo);

                    // Use the printout method to display the task
                    printout(todo, assignee);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Print out the found tasks
        if (tasksByAssignee.isEmpty()) {
            System.out.println("No tasks found for assignee with ID: " + id);
        }
        return tasksByAssignee;
    }



    @Override
    public Collection<Todo> findByAssignee(Person person) {
        List<Todo> tasksByAssignee = new ArrayList<>();

        // Retrieve the ID of the person from the database based on first and last name
        int assigneeId = getPersonIdByName(person.getFirstName(), person.getLastName());

        String query = "SELECT * FROM todo_item WHERE assignee_id = ?";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, assigneeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int taskId = resultSet.getInt("todo_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate deadline = resultSet.getDate("deadline").toLocalDate();
                    boolean done = resultSet.getBoolean("done");

                    Todo todo = new Todo(taskId, title, description, deadline, done, person);
                    tasksByAssignee.add(todo);

                    // Use the printout method to display the task
                    printout(todo, person);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Print out the found tasks
        if (tasksByAssignee.isEmpty()) {
            System.out.println("No tasks found for assignee: " + person.getFirstName() + " " + person.getLastName());
        }
        return tasksByAssignee;
    }
    @Override
    public Collection<Todo> findByUnassignedTodoItems() {
        List<Todo> unassignedTasks = new ArrayList<>();

        String query = "SELECT * FROM todo_item WHERE assignee_id IS NULL OR assignee_id = 0";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int taskId = resultSet.getInt("todo_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate deadline = resultSet.getDate("deadline").toLocalDate();
                    boolean done = resultSet.getBoolean("done");

                    // Unassigned tasks have no specific assignee, so you can create a Todo object with a null assignee
                    Todo todo = new Todo(taskId, title, description, deadline, done, null);
                    unassignedTasks.add(todo);

                    // Use the printout method to display the task
                    try {
                        printout(todo, null);
                    } catch (NullPointerException ex) {
                        // Handle the NullPointerException here
                        System.err.println("Error: Assignee is null for task ID " + taskId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Print out the found tasks
        if (unassignedTasks.isEmpty()) {
            System.out.println("No unassigned tasks found.");
        }
        return unassignedTasks;
    }

    @Override
    public Todo findById(int id) {
        System.out.println("Attempting to find task with ID: " + id); // Debug statement

        String query = "SELECT * FROM todo_item WHERE todo_id = ?";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int taskId = resultSet.getInt("todo_id");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    LocalDate deadline = resultSet.getDate("deadline").toLocalDate();
                    boolean done = resultSet.getBoolean("done");
                    int assigneeId = resultSet.getInt("assignee_id");

                    // Retrieve the assignee information from the Person table
                    Person assignee = getPersonById(assigneeId);
                    Todo todo = new Todo(taskId, title, description, deadline, done, assignee);
                    printout(todo, Objects.requireNonNull(assignee));
                    return todo;
                } else {
                    System.out.println("Todo with ID " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while executing SQL query: " + e.getMessage());
        }

        return null;
    }



    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM todo_item WHERE todo_id = ?";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Todo with ID " + id + " deleted successfully!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Todo with ID " + id + " not found. Deletion failed.");
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
    private void printout(Todo todo, Person assignee){
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
    // Helper method to retrieve the ID of a person by first and last name
    private int getPersonIdByName(String firstName, String lastName) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT person_id FROM person WHERE first_name = ? AND last_name = ?")) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("person_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no matching person is found
    }
}
