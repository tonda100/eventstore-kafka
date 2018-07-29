package net.osomahe.realapp.user.control;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.osomahe.realapp.user.boundary.UserBoundary;
import net.osomahe.realapp.user.entity.User;
import net.osomahe.realapp.user.entity.UserCreatedEvent;
import net.osomahe.realapp.user.entity.UserDeleteEvent;
import net.osomahe.realapp.user.entity.UserRenameEvent;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class UserEventHandler {

    @Inject
    private UserBoundary userBoundary;

    public void createUserEvent(@Observes UserCreatedEvent event){
        User user = new User();
        user.setId(event.getId());
        user.setName(event.getName());
        userBoundary.saveUser(user);
    }



    public void renameUserEvent(@Observes UserRenameEvent event){
        User user = new User();
        user.setId(event.getId());
        user.setName(event.getName());
        userBoundary.saveUser(user);
    }


    public void deleteUserEvent(@Observes UserDeleteEvent event){
        userBoundary.deleteUser(event.getId());
    }

}
