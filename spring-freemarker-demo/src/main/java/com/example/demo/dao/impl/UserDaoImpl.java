package com.example.demo.dao.impl;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(User user) {
        String sql = "INSERT INTO t_user(username, email, age, create_time, update_time) " +
                "VALUES(?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getAge());
            return ps;
        }, keyHolder);

        if (result > 0 && keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
        }

        return result;
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE t_user SET username=?, email=?, age=?, update_time=NOW() WHERE id=?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getAge(),
                user.getId());
    }

    @Override
    public int delete(Long id) {
        String sql = "DELETE FROM t_user WHERE id=?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT id, username, email, age, create_time, update_time FROM t_user WHERE id=?";

        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        List<User> users = jdbcTemplate.query(sql, rowMapper, id);

        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username, email, age, create_time, update_time FROM t_user ORDER BY id DESC";

        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<User> findByUsername(String username) {
        String sql = "SELECT id, username, email, age, create_time, update_time " +
                "FROM t_user WHERE username LIKE ? ORDER BY id DESC";

        RowMapper<User> rowMapper = new BeanPropertyRowMapper<>(User.class);
        return jdbcTemplate.query(sql, rowMapper, "%" + username + "%");
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM t_user";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}