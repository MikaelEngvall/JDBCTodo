package se.lexicon;


import se.lexicon.data.PeopleImpl;

import java.sql.*;


public class JDBCTodo {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todoit", "root", "1234");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select person_id, first_name, last_name from person");
            // executeQuery used for SELECT Queries.
            // executeUpdate used for INSERT, DELETE, UPDATE
//            Person newPerson = new Person(2, "John", "Blur");
//            Person updatePerson = new Person(2, "John", "Blond");
//            Person anders = new Person("Anders", "Loren");
//            Person mikael = new Person("Mikael", "Engvall");
            PeopleImpl obj = new PeopleImpl();  // We had to add this to be able to call create!!!
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
