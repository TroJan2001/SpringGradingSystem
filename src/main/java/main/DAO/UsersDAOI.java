package main.DAO;

import main.Model.User;

import java.util.List;

public interface UsersDAOI {
    boolean createUser(User user);

    boolean deleteUser(int id);

    String getUserRole(String name);

    User getUserByName(String name);

    User getUserById(int id);

    List<User> getAllUsers();

    boolean userExists(String name);
}