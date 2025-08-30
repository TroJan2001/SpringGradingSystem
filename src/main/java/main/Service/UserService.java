package main.Service;

import main.DAO.UsersDAO;
import main.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UsersDAO usersDAO;

    @Autowired
    public UserService(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    public boolean createUser(User user) {
        try {
            return usersDAO.createUser(user);
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean deleteUser(int id) {
        try {
            return usersDAO.deleteUser(id);
        } catch (DataAccessException e) {
            return false;
        }
    }

    public User getUserById(int id) {
        try {
            return usersDAO.getUserById(id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<User> getAllUsers() {

        try {
            return usersDAO.getAllUsers();
        } catch (DataAccessException e) {
            return null;
        }
    }

    public String getUserRole(String name) {
        try {
            return usersDAO.getUserRole(name);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByName(String name) {
        try {
            return usersDAO.getUserByName(name);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public boolean userExists(String name) {
        try {
            return usersDAO.userExists(name);
        } catch (DataAccessException e) {
            return false;
        }
    }
}
