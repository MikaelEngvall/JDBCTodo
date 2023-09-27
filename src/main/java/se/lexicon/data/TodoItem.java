package se.lexicon.data;

import se.lexicon.model.Person;
import se.lexicon.model.Todo;

import java.util.Collection;

public interface TodoItem {
    Todo create(Todo todo);
    Todo update(Todo todo);
    Collection<Todo> findAll();
    Collection<Todo> findByDoneStatus(boolean done);
    Collection<Todo> findByAssignee(int id);
    Collection<Todo> findByAssignee(Person person);
    Collection<Todo> findByUnassignedTodoItems();
    Todo findById(int id);
    boolean delete(int id);
}