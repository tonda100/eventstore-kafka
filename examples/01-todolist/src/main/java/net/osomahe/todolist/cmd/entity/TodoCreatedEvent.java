package net.osomahe.todolist.cmd.entity;

import net.osomahe.esk.entity.AbstractEvent;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class TodoCreatedEvent extends AbstractEvent {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TodoCreatedEvent{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
