package se.lexicon;


import se.lexicon.data.PeopleImpl;
import se.lexicon.data.TodoItemImpl;

import java.sql.*;


public class JDBCTodo {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todoit", "root", "1234");
            Statement statement = connection.createStatement();
            ResultSet resultSetPerson = statement.executeQuery("select person_id, first_name, last_name from person");
            ResultSet resultSetTodo = statement.executeQuery("select todo_id, title, description, deadline, done, assignee_id from todo_item");
            // executeQuery used for SELECT Queries.
            // executeUpdate used for INSERT, DELETE, UPDATE
//            Person newPerson = new Person(2, "John", "Blur");
//            Person updatePerson = new Person(2, "John", "Blond");
//            Person anders = new Person("Anders", "Loren");
//            Person mikael = new Person("Mikael", "Engvall");
//            Todo task1 = new Todo("Change tires", "Switch summer tires to winter tires",
//                    LocalDate.of(2023,10, 1), false, new Person(3));
//            Todo updateTask = new Todo(1, "Change tires", "Switch winter tires to summer tires",
//                    LocalDate.of(2023,10, 1), false, new Person(3));
//            Todo task2 = new Todo("Attend to scrum meeting", "Weekly meeting to set the path",
//                    LocalDate.of(2023,10, 1), false, new Person(2));
            PeopleImpl obj = new PeopleImpl();  // We had to add this to be able to call create!!! Creating an instance?
            TodoItemImpl task = new TodoItemImpl();
//            task.create(task1);
//            task.create(task2);
//            task.findAll();
//            task.update(updateTask);
//            task.findByDoneStatus(true);
//            obj.create(mikael);
//            obj.create(anders);
//            obj.findAll();
//            obj.findByName("John");
//            obj.findById(3);
//            obj.update(updatePerson);
//            obj.delete(7);
        } catch (SQLException e) {
            System.out.println("SQL Exception: ");
            e.printStackTrace();
        }
    }
}
