package com.cad.web.dao;

import com.cad.web.domain.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 接口的简单实现
 */
@Service("RedisDBHelper")
public class RedisDBHelperImpl<HK, T> implements RedisDBHelper<HK, T>{

    // 在构造器中获取redisTemplate实例, key(not hashKey) 默认使用String类型
    private RedisTemplate<String, T> redisTemplate;

    // 在构造器中通过redisTemplate的工厂方法实例化操作对象
    private HashOperations<String, HK, T> hashOperations;

    private ListOperations<String, T> listOperations;

    private ZSetOperations<String, T> zSetOperations;

    private SetOperations<String, T> setOperations;

    private ValueOperations<String, T> valueOperations;

    // IDEA虽然报错,但是依然可以注入成功, 实例化操作对象后就可以直接调用方法操作Redis数据库
    @Autowired
    public RedisDBHelperImpl (RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.listOperations = redisTemplate.opsForList();
        this.zSetOperations = redisTemplate.opsForZSet();
        this.setOperations = redisTemplate.opsForSet();
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void hashPut(String key, HK hashKey, T domain) {
        hashOperations.put(key, hashKey, domain);
    }

    @Override
    public Map<HK, T> hashFindAll(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public List< T> hashFindMutiValue(String key, Collection collection) {
        return hashOperations.multiGet(key,collection);
    }
    @Override
    public T hashGet(String key, HK hashKey) {
        return hashOperations.get(key, hashKey);
    }


    @Override
    public void hashRemove(String key, HK hashKey) {
        hashOperations.delete(key, hashKey);
    }


    @Override
    public List<T> listRange(String key, Long start,Long end) {
        return listOperations.range(key,start,end);
    }

    @Override
    public Long listPush(String key, T domain) {
        return listOperations.rightPush(key, domain);
    }

    @Override
    public Long listUnshift(String key, T domain) {
        return listOperations.leftPush(key, domain);
    }

    @Override
    public List<T> listFindAll(String key) {
        if (! redisTemplate.hasKey(key)){
            return null;
        }
        return listOperations.range(key,0, listOperations.size(key));
    }

    @Override
    public T listLPop(String key) {
        return listOperations.leftPop(key);
    }

    @Override
    public Long setPush(String key, T domain) {
        return setOperations.add(key,domain);
    }

    @Override
    public Set<T> setFindAll(String key) {
//        System.out.println(key);
        return setOperations.members(key);
    }

    @Override
    public boolean setIsMember(String key, T domain) {
        return setOperations.isMember(key,domain);
    }

    @Override
    public void setRemove(String key, T domain) {
        setOperations.remove(key,domain);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    @Override
    public boolean haskey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public boolean hashhaskey(String key,HK hashkey) {
        return hashOperations.hasKey(key,hashkey);
    }
}
