package net.osomahe.realapp.user.entity;

/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class UserRenameEvent extends UserEvent {


    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserRenameEvent{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
