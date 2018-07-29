package net.osomahe.realapp.user.control;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;

import net.osomahe.realapp.user.entity.User;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Singleton
public class UserStorage {

    private Map<String, User> users = new ConcurrentHashMap<>();


    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public void deleteUser(String id) {
        users.remove(id);
    }
}
