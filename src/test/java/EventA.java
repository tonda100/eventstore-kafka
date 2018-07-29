import net.osomahe.esk.eventstore.entity.EventStoreEvent;
import net.osomahe.esk.eventstore.entity.EventGroupKey;
import net.osomahe.esk.eventstore.entity.TopicName;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@TopicName("test-topis")
public class EventA implements EventStoreEvent {

    @EventGroupKey
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "EventA{" +
                "userId='" + userId + '\'' +
                "} " + super.toString();
    }
}
