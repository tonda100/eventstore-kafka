package net.osomahe.todolist.cmd.entity;

import net.osomahe.esk.eventstore.entity.EventStoreEvent;
import net.osomahe.esk.eventstore.entity.EventGroupKey;


/**
 * @author Antonin Stoklasek
 */
public class TodoDeletedEvent implements EventStoreEvent {

    @EventGroupKey
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TodoDeletedEvent{" +
                "id='" + id + '\'' +
                "} " + super.toString();
    }
}
