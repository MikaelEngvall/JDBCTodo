package se.lexicon.data;

import org.junit.jupiter.api.*;
import se.lexicon.model.Person;

import java.util.Collection;

class PeopleImplTest {

PeopleImpl testObject = new PeopleImpl();

    @Test
    @DisplayName("create() Instantiate an object, create another one stored in createdPerson. Delete createdPerson. Then assert the object and the createdPerson are equal. Delete it from db")
    void create() {
        Person person1 = new Person(null, "Mikael", "Engvall");
        Person createdPerson = testObject.create(person1);
        Assertions.assertEquals(person1, createdPerson);
        testObject.delete(createdPerson.getId());
    }

    @Test
    @DisplayName("delete() Instantiate an object, create another one stored in createdPerson. Delete createdPerson. Then assert null as return from findById with id from created person ")
    void delete() {
        Person person1 = new Person("Mikael", "Engvall");
        Person createdPerson = testObject.create(person1);
        testObject.delete(createdPerson.getId());
        Assertions.assertNull(testObject.findById(createdPerson.getId()));
    }
    @Test
    @DisplayName("findById() Instantiate an object, then another one with return value from the create method then compare both' first names. Then delete the object from the db.")
    void findById() {
        Person expectedPerson = new Person("Test", "Testsson");
        Person testPerson = testObject.create(expectedPerson); // Put a new person in the db
        Assertions.assertEquals(expectedPerson.getFirstName(), testObject.findById(testPerson.getId()).getFirstName());
        testObject.delete(testPerson.getId());
    }

    @Test
    @DisplayName("create() Instantiate an object with same values as an existing person from db, except we change the last name called updateToBeDoneToPerson. Pass that object to the method update. Assert the last name of updateToBeDoneToPerson and the updatedPerson are equals.")
    void update(){
        Person updateToBeDoneToPerson = new Person(1,"John", "Blur"); //Person id to be updated, changing lastName
        Person updatedPerson = testObject.update(updateToBeDoneToPerson);
        Assertions.assertEquals(updateToBeDoneToPerson.getLastName(), testObject.findById(updatedPerson.getId()).getLastName());
    }
    @Test
    void findByName() {
        Person searchedPerson = new Person("John", "Whatever");
        Collection<Person> expectedPerson = testObject.findByName("John");
        Collection<Person> actualPerson = testObject.findByName(searchedPerson.getFirstName());
        // Cannot compare two identical Collections, had to toString them first
        Assertions.assertEquals(expectedPerson.toString(), actualPerson.toString());
    }

}