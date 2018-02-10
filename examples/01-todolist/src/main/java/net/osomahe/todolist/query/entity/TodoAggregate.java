package net.osomahe.todolist.query.entity;

/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class TodoAggregate {

    private String id;

    private String name;

    private Boolean completed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "TodoAggregate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", completed=" + completed +
                '}';
    }
}
