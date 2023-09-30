package se.lexicon.model;

import java.time.LocalDate;

public class Todo {
    private int id;
    private String title;
    private String description;
    private LocalDate deadline;
    private boolean done;
    private Person assignee_id;

    public Todo() {
    }

    public Todo(String title, String description, LocalDate deadline, boolean done) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.done = done;
    }

    public Todo(int id) {
        this.id = id;
    }

    public Todo(boolean done) {
        this.done = done;
    }

    public Todo(Person assignee_id) {
        this.assignee_id = assignee_id;
    }

    public Todo(String title, String description, LocalDate deadline, boolean done, Person assignee_id) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.done = done;
        this.assignee_id = assignee_id;
    }

    public Todo(int id, String title, String description, LocalDate deadline, boolean done, Person assignee_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.done = done;
        this.assignee_id = assignee_id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public boolean isDone() {
        return done;
    }

    public Person getAssignee_id() {
        return assignee_id;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", done=" + done +
                ", assignee_id=" + assignee_id +
                '}';
    }
}