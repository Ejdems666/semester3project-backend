package org.cba.model.facades;

import io.ebean.Ebean;
import org.cba.model.entities.Role;
import org.cba.model.entities.User;
import org.cba.model.exceptions.UsernameNotUniqueException;
import org.cba.model.util.Hasher;

/**
 * Created by adam on 11/21/2017.
 */
public class RegisterFacade {

    public User register(String username, String password) throws UsernameNotUniqueException {
        if (!isUsernameUnique(username)) {
            throw new UsernameNotUniqueException();
        }
        Hasher hasher = new Hasher();
        String salt = hasher.generateSalt();
        String hashedPassword = hasher.hashPassword(password, salt);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        addBasicUserRole(user);
        Ebean.save(user);
        return user;
    }

    private void addBasicUserRole(User user) {
        Role role = new Role();
        role.setId(1);
        user.getRoles().add(role);
    }

    private boolean isUsernameUnique(String username) {
        return User.find.where().username.eq(username).findCount() == 0;
    }
}
