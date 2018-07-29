package net.osomahe.todolist.cmd.entity;

import net.osomahe.esk.eventstore.entity.EventStoreEvent;
import net.osomahe.esk.eventstore.entity.EventGroupKey;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class TodoCreatedEvent implements EventStoreEvent {

    @EventGroupKey
    private String id;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TodoCreatedEvent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }
}
