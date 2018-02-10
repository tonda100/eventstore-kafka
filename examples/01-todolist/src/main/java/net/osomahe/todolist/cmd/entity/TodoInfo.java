package net.osomahe.todolist.cmd.entity;

/**
 * @author Antonin Stoklasek
 */
public class TodoInfo {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TodoInfo{" +
                "name='" + name + '\'' +
                '}';
    }
}
