package com.mulan.fengwo_backend.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类
 * @author mulan
 */
@Component
@Slf4j
public class RedisCacheUtil {
    private static final Gson gson = new Gson();
    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 写缓存
     * @param key
     * @param value
     * @param timeOut
     * @param unit
     */
    public void setCache(String key, Object value, Long timeOut, TimeUnit unit){
        String sValue = gson.toJson(value);
        try {
            stringRedisTemplate.opsForValue().set(key,sValue,timeOut,unit);
        } catch (Exception e){
            log.error("写缓存失败",e);
        }
    }

    /**
     * 读缓存
     * @param key
     * @param type
     * @return
     * @param <T>
     */
    public <T> T getCache(String key,Class<T> type){
        String sValue = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(sValue)){
            return gson.fromJson(sValue, type);
        }
        return null;
    }
    public <T> T getCache(String key, Type type){
        String sValue = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(sValue)){
            return gson.fromJson(sValue, type);
        }
        return null;
    }
}
