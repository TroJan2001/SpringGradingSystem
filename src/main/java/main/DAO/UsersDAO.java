package main.DAO;

import main.Model.User;
import main.Utility.PasswordUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UsersDAO implements UsersDAOI {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("password"),
            rs.getString("role")
    );

    public UsersDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean createUser(User user) throws DataAccessException{
        byte[] salt = PasswordUtil.generateSalt();
        String hashedPasswordWithSalt = PasswordUtil.hashPassword(user.getPassword(), salt);
        String query = "INSERT INTO users (name, password, role) VALUES (?, ?, ?)";
        int affectedRows = jdbcTemplate.update(query, user.getName(), hashedPasswordWithSalt, user.getRole());
        return affectedRows > 0;
    }

    @Override
    public boolean deleteUser(int id) throws DataAccessException{
        String query = "DELETE FROM users WHERE id = ?";
        int affectedRows = jdbcTemplate.update(query, id);
        return affectedRows > 0;
    }

    @Override
    public User getUserById(int id) throws DataAccessException {
        String query = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(query, USER_ROW_MAPPER, id);
    }

    @Override
    public List<User> getAllUsers() throws DataAccessException{
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, USER_ROW_MAPPER);
    }

    public String getUserRole(String name) {
        User user = getUserByName(name);
        return user != null ? user.getRole() : null;
    }

    public User getUserByName(String name) throws DataAccessException{
        String query = "SELECT * FROM users WHERE name = ?";
        return jdbcTemplate.queryForObject(query, USER_ROW_MAPPER, name);
    }

    public boolean userExists(String name) throws DataAccessException{
        String query = "SELECT COUNT(*) FROM users WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, name);
        return count != null && count > 0;
    }
}
