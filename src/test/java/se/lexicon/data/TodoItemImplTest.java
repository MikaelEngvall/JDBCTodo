package se.lexicon.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.lexicon.model.Person;
import se.lexicon.model.Todo;

import java.time.LocalDate;

class TodoItemImplTest {
    private TodoItemImpl testObject;


    @BeforeEach
    void setUp() {
        testObject = new TodoItemImpl();
    }

    @AfterEach
    void tearDown() {
    }
//    String title, String description, LocalDate deadline, boolean done
    @Test
    void create() {
        Todo todo1 = new Todo("Do this", "Has to be done somehow", LocalDate.of(2023, 10,1), false);
        Todo createdTodo = testObject.create(todo1);
        Assertions.assertEquals(todo1, createdTodo);
    }

    @Test
    void update() {
        Todo taskToBeUpdated = testObject.findById(7);
        Todo updatedTask = new Todo(taskToBeUpdated.getId(), "Updated", taskToBeUpdated.getDescription(),
                taskToBeUpdated.getDeadline(), taskToBeUpdated.isDone(), taskToBeUpdated.getAssignee_id());
        taskToBeUpdated = testObject.update(updatedTask);
        Assertions.assertEquals(updatedTask, taskToBeUpdated);
    }

    @Test
    void findAll() {
        for (Todo todo : testObject.findAll()) {System.out.println(todo);}
    }

    @Test
    void findByDoneStatus() {
        for (Todo todo : testObject.findByDoneStatus(true)) {System.out.println(todo);}
    }

    @Test
    void findByAssignee() {
        for (Todo todo : testObject.findByAssignee(3)) {System.out.println(todo);}
    }

    @Test
    void testFindByAssignee() {
        Person anders = new Person("Anders", "Loren");
        for (Todo todo : testObject.findByAssignee(anders)) {System.out.println(todo);}
    }

    @Test
    void findByUnassignedTodoItems() {
        for (Todo todo : testObject.findByUnassignedTodoItems()) {System.out.println(todo);}
    }

    @Test
    void findById() {
        Todo todo1 = new Todo("Do this", "Has to be done somehow", LocalDate.of(2023, 10,1), false);
        Todo createdTodo = testObject.create(todo1); //Create an object
        int createdTodosId = createdTodo.getId();//Get its ID
        Assertions.assertNotNull(testObject.findById(createdTodosId)); //Assert its ID not null
        testObject.delete(createdTodosId); // Deleting the object so we don't overpopulate the db

    }

    @Test
    void delete() {
        Todo todo1 = new Todo("Do this", "Has to be done somehow", LocalDate.of(2023, 10,1), false);
        Todo createdTodo = testObject.create(todo1); //Create an object
        testObject.delete(createdTodo.getId()); //Delete it right away
        Assertions.assertNull(testObject.findById(createdTodo.getId())); // Assert teh object doesn't exist anymore
    }
}