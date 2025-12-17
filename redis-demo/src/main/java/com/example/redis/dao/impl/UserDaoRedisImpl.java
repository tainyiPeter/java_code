package com.example.redis.dao.impl;

import com.example.redis.dao.UserDao;
import com.example.redis.model.User;
import com.example.redis.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;

public class UserDaoRedisImpl implements UserDao {

    private static final Logger logger = Logger.getLogger(UserDaoRedisImpl.class);

    private RedisUtil redisUtil;
    private ObjectMapper objectMapper;
    private String userKeyPrefix = "user:";
    private int userExpire = 1800; // 用户缓存过期时间（秒）

    /**
     * 生成用户key
     */
    private String getUserKey(Long id) {
        return userKeyPrefix + id;
    }

    /**
     * 生成用户名key
     */
    private String getUsernameKey(String username) {
        return userKeyPrefix + "username:" + username;
    }

    /**
     * 对象转JSON
     */
    private String toJson(User user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            logger.error("User对象转JSON失败: " + user, e);
            return null;
        }
    }

    /**
     * JSON转对象
     */
    private User fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, User.class);
        } catch (IOException e) {
            logger.error("JSON转User对象失败: " + json, e);
            return null;
        }
    }

    @Override
    public boolean saveUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        String userKey = getUserKey(user.getId());
        String usernameKey = getUsernameKey(user.getUsername());
        String userJson = toJson(user);

        if (userJson == null) {
            return false;
        }

        try {
            // 保存用户数据
            redisUtil.setex(userKey, userExpire, userJson);

            // 建立用户名到ID的映射
            redisUtil.setex(usernameKey, userExpire, user.getId().toString());

            logger.info("保存用户成功: " + user);
            return true;
        } catch (Exception e) {
            logger.error("保存用户失败: " + user, e);
            return false;
        }
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        }

        String userKey = getUserKey(id);
        try {
            String userJson = redisUtil.get(userKey);
            User user = fromJson(userJson);

            if (user != null) {
                // 刷新过期时间
                redisUtil.expire(userKey, userExpire);
            }

            return user;
        } catch (Exception e) {
            logger.error("根据ID获取用户失败: " + id, e);
            return null;
        }
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        try {
            // 通过用户名获取用户ID
            String usernameKey = getUsernameKey(username);
            String userIdStr = redisUtil.get(usernameKey);

            if (userIdStr == null) {
                return null;
            }

            Long userId = Long.parseLong(userIdStr);
            return getUserById(userId);
        } catch (Exception e) {
            logger.error("根据用户名获取用户失败: " + username, e);
            return null;
        }
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        // 先获取旧用户信息
        User oldUser = getUserById(user.getId());
        if (oldUser == null) {
            return false;
        }

        // 如果用户名改变了，需要更新映射关系
        if (!oldUser.getUsername().equals(user.getUsername())) {
            String oldUsernameKey = getUsernameKey(oldUser.getUsername());
            redisUtil.del(oldUsernameKey);
        }

        return saveUser(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null) {
            return false;
        }

        try {
            // 先获取用户信息
            User user = getUserById(id);
            if (user == null) {
                return false;
            }

            // 删除用户数据
            String userKey = getUserKey(id);
            redisUtil.del(userKey);

            // 删除用户名映射
            String usernameKey = getUsernameKey(user.getUsername());
            redisUtil.del(usernameKey);

            logger.info("删除用户成功: " + id);
            return true;
        } catch (Exception e) {
            logger.error("删除用户失败: " + id, e);
            return false;
        }
    }

    @Override
    public boolean existsUser(Long id) {
        if (id == null) {
            return false;
        }

        String userKey = getUserKey(id);
        return redisUtil.exists(userKey);
    }

    @Override
    public boolean expireUser(Long id, int seconds) {
        if (id == null) {
            return false;
        }

        String userKey = getUserKey(id);
        Long result = redisUtil.expire(userKey, seconds);
        return result != null && result == 1;
    }

    @Override
    public Long getUserTTL(Long id) {
        if (id == null) {
            return null;
        }

        String userKey = getUserKey(id);
        return redisUtil.ttl(userKey);
    }

    // ==================== getter 和 setter ====================

    public RedisUtil getRedisUtil() {
        return redisUtil;
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getUserKeyPrefix() {
        return userKeyPrefix;
    }

    public void setUserKeyPrefix(String userKeyPrefix) {
        this.userKeyPrefix = userKeyPrefix;
    }

    public int getUserExpire() {
        return userExpire;
    }

    public void setUserExpire(int userExpire) {
        this.userExpire = userExpire;
    }
}