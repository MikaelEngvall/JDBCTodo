package se.lexicon;


import se.lexicon.data.PeopleImpl;
import se.lexicon.data.TodoItemImpl;
import se.lexicon.db.MySQLConnection;
import se.lexicon.model.Person;
import se.lexicon.model.Todo;

import java.sql.Connection;
import java.time.LocalDate;


public class JDBCTodo {
    public static void main(String[] args) {
        try {
            PeopleImpl obj = new PeopleImpl();  // We had to add this to be able to call the methods!!!
//                                                 Creating an instance?
            TodoItemImpl task = new TodoItemImpl();

            Connection connection = MySQLConnection.getConnection();
            Person newPerson = new Person(2, "John", "Blur");
            Person updatePerson = new Person(2, "John", "Blond");
            Person anders = new Person("Anders", "Loren");
            Person mikael = new Person("Mikael", "Engvall");
            Todo task1 = new Todo("Change tires", "Switch summer tires to winter tires",
                    LocalDate.of(2023,10, 1), false, new Person(3));
            Todo updateTask = new Todo(task.findById(5).getId(), "Change tires", "Switch winter tires to summer tires",
                    LocalDate.of(2023,10, 1), false, new Person(3));
            Todo task2 = new Todo("Attend to scrum meeting", "Weekly meeting to set the path",
                    LocalDate.of(2023,10, 1), false, new Person(2));
            Todo task3 = new Todo("Task3", "task3", LocalDate.of(2023,11, 1), true, new Person(3));
            Todo task8 = new Todo("Task8", "task8", LocalDate.of(2023,9, 1), false);
            Todo task5 = new Todo("Task5", "task5", LocalDate.of(2023,9, 1), false, null);

//            obj.create(newPerson);
//            obj.create(mikael);
//            obj.create(anders);
//            obj.findAll();
//            obj.findByName("John");
//            obj.findById(7);
//            obj.update(updatePerson);
//            obj.delete(9);
//            task.create(task1);
//            task.create(task2);
//            task.create(task3);
//            task.create(task8);
//            task.create(task5); // Cannot create a task without assignee
//            for (Todo todo : task.findAll()) {System.out.println(todo);}
//            task.update(updateTask);
//            System.out.println(task.findById(6));
//            task.findByDoneStatus(false);
//            for (Todo todo : task.findByAssignee(3)) {System.out.println(todo);}
//            for (Todo todo : task.findByAssignee(anders)) {System.out.println(todo);}
//            for (Todo todo : task.findByUnassignedTodoItems()) {System.out.println(todo);}
//            task.delete(8);
        } catch (Throwable e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
