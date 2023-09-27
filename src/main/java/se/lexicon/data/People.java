package se.lexicon.data;

import se.lexicon.model.Person;

import java.util.Collection;

public interface People {
    void create(Person person);
    Person update(Person person);
    Collection<Person> findAll();
    Collection<Person> findByName(String name);
    Person findById(int id);
    boolean delete(int id);

}
