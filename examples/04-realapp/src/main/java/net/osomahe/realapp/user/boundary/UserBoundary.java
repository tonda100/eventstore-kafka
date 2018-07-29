package net.osomahe.realapp.user.boundary;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.osomahe.realapp.user.control.UserStorage;
import net.osomahe.realapp.user.entity.User;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class UserBoundary {

    @Inject
    private UserStorage storage;

    public void saveUser(User user) {
        storage.saveUser(user);
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public void deleteUser(String id) {
        storage.deleteUser(id);
    }

}
