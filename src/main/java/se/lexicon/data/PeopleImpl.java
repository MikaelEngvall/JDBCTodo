package se.lexicon.data;

import se.lexicon.db.MySQLConnection;
import se.lexicon.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PeopleImpl implements People {

    @Override
    public Person create(Person person) {

        String query = "INSERT INTO person(first_name, last_name) VALUES(?, ?)";
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {

            preparedStatement.setString(1, person.getFirstName());
            preparedStatement.setString(2, person.getLastName());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                throw new SQLException("Person created unsuccessfully!");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    person.setId(generatedKeys.getInt(1)); // Setting the ID to the person object
//                    System.out.println("generatedPersonId = " + person.getId());
                } else {
                    throw new SQLException("Creation of person id failed!");
                }
            }
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Person update(Person person) {
        // Check if the person with the provided ID exists in the database
        if (personIdExists(person.getId())) {
            try (Connection connection = MySQLConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE person SET first_name = ?, last_name = ? WHERE person_id = ?")) {

                preparedStatement.setString(1, person.getFirstName());
                preparedStatement.setString(2, person.getLastName());
                preparedStatement.setInt(3, person.getId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Person with ID " + person.getId() + " updated successfully in the database.");
                } else {
                    System.out.println("Person with ID " + person.getId() + " not found in the database.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Person with ID " + person.getId() + " not found in the database.");
        }
        return person;
    }

    @Override
    public Collection<Person> findAll() {
        List<Person> matchingPersons = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM person")) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int personId = resultSet.getInt("person_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    // Create a Person object with the retrieved data
                    Person person = new Person(personId, firstName, lastName);
                    matchingPersons.add(person);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Print out the found persons
        if (!matchingPersons.isEmpty()) {
            System.out.println(matchingPersons.toString());
//            for (Person person : matchingPersons) {
//                System.out.println("Found Person: " + person.getFirstName() + " " + person.getLastName() + " with ID: " + person.getId());
//            }
        } else {
            System.out.println("No persons found");
        }
        return matchingPersons;
    }

    @Override
    public Collection<Person> findByName(String name) {
        List<Person> matchingPersons = new ArrayList<>();

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM person WHERE CONCAT(first_name, ' ', last_name) = ? OR first_name = ?")) {

            preparedStatement.setString(1, name); // I am looking for name in either firstName
            preparedStatement.setString(2, name); // or lastName

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int personId = resultSet.getInt("person_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    // Create a Person object with the retrieved data
                    Person person = new Person(personId, firstName, lastName);
                    matchingPersons.add(person);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Print out the found persons
        if (!matchingPersons.isEmpty()) {
            System.out.println(matchingPersons.toString());
//            for (Person person : matchingPersons) {
//                System.out.println("Found Person: " + person.getFirstName() + " " + person.getLastName() + " with ID: " + person.getId());
//            }
        } else {
            System.out.println("No persons found with the name '" + name + "'.");
        }
        return matchingPersons;
    }

    @Override
    public Person findById(Integer id) {
        try (
                Connection connection = MySQLConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM person WHERE person_id = ? ")
        ) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Person person = null;
            if (resultSet.next()) {
                int personId = resultSet.getInt("person_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                person = new Person(personId, firstName, lastName);
            }
            return person;  // We cant return new Person(); here !!!!

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } // it will close -> first resultSet, preparedStatement, connection
    }

    @Override
    public boolean delete(int id) {
        Person person = new Person(id);
        if (personIdExists(person.getId())) {
            try (Connection connection = MySQLConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM person WHERE person_id = ?")) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
//                System.out.println("Person with ID " + id + " deleted from the database.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Person with ID " + id + " not found in the database.");
            return false;
        }
        return true;
    }

    private boolean personIdExists(int id) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM person WHERE person_id = ?")) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a matching person exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if there's an error or no match found
    }

    private boolean personExists(int id, String firstName, String lastName) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM person WHERE id = ? OR (first_name = ? AND last_name = ?")) {

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a matching person exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if there's an error or no match found
    }
}
