package net.osomahe.realapp.user.entity;

import net.osomahe.esk.eventstore.entity.EventGroupKey;
import net.osomahe.esk.eventstore.entity.EventStoreEvent;
import net.osomahe.esk.eventstore.entity.TopicName;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@TopicName("user")
public class UserEvent implements EventStoreEvent {

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
        return "UserEvent{" +
                "id='" + id + '\'' +
                '}';
    }
}
