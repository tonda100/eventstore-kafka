import net.osomahe.esk.eventstore.entity.EventGroupKey;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class EventB extends EventA {

    private Long age;

    private String name;

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    @EventGroupKey
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EventB{" +
                "age=" + age +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }
}
