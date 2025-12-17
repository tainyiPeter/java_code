package com.example.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 操作工具类
 */
public class RedisUtil {

    private JedisPool jedisPool;
    private int defaultExpire = 3600; // 默认过期时间（秒）

    // 构造方法
    public RedisUtil() {}

    public RedisUtil(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 获取 Jedis 连接
     */
    private Jedis getJedis() {
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            }
            return null;
        } catch (Exception e) {
            throw new JedisException("获取Redis连接失败", e);
        }
    }

    /**
     * 释放 Jedis 连接
     */
    private void returnJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    // ==================== String 操作 ====================

    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.set(key, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.setex(key, seconds, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } finally {
            returnJedis(jedis);
        }
    }

    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(key);
        } finally {
            returnJedis(jedis);
        }
    }

    public Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.exists(key);
        } finally {
            returnJedis(jedis);
        }
    }

    public Long expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire(key, seconds);
        } finally {
            returnJedis(jedis);
        }
    }

    public Long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.ttl(key);
        } finally {
            returnJedis(jedis);
        }
    }

    // ==================== Hash 操作 ====================

    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hset(key, field, value);
        } finally {
            returnJedis(jedis);
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } finally {
            returnJedis(jedis);
        }
    }

    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        } finally {
            returnJedis(jedis);
        }
    }

    public Long hdel(String key, String... fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hdel(key, fields);
        } finally {
            returnJedis(jedis);
        }
    }

    // ==================== List 操作 ====================

    public Long lpush(String key, String... values) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key, values);
        } finally {
            returnJedis(jedis);
        }
    }

    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrange(key, start, end);
        } finally {
            returnJedis(jedis);
        }
    }

    // ==================== Set 操作 ====================

    public Long sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, members);
        } finally {
            returnJedis(jedis);
        }
    }

    public Set<String> smembers(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.smembers(key);
        } finally {
            returnJedis(jedis);
        }
    }

    // ==================== 原子操作 ====================

    public Long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incr(key);
        } finally {
            returnJedis(jedis);
        }
    }

    public Long incrBy(String key, long increment) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incrBy(key, increment);
        } finally {
            returnJedis(jedis);
        }
    }

    public Long decr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.decr(key);
        } finally {
            returnJedis(jedis);
        }
    }

    // ==================== Key 操作 ====================

    public Set<String> keys(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.keys(pattern);
        } finally {
            returnJedis(jedis);
        }
    }

    // ==================== 通用方法 ====================

    /**
     * 设置带默认过期时间的缓存
     */
    public String setWithDefaultExpire(String key, String value) {
        return setex(key, defaultExpire, value);
    }

    /**
     * 批量删除指定模式的key
     */
    public Long deletePattern(String pattern) {
        Set<String> keys = keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }

        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(keys.toArray(new String[0]));
        } finally {
            returnJedis(jedis);
        }
    }

    // ==================== getter 和 setter ====================

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public int getDefaultExpire() {
        return defaultExpire;
    }

    public void setDefaultExpire(int defaultExpire) {
        this.defaultExpire = defaultExpire;
    }
}