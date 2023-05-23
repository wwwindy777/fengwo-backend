package com.mulan.fengwo_backend.job;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mulan.fengwo_backend.constant.RedisConstant;
import com.mulan.fengwo_backend.model.domain.User;
import com.mulan.fengwo_backend.service.UserService;
import com.mulan.fengwo_backend.util.RedisCacheUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存预热
 * @author mulan
 */
@Component
public class PreCacheJob {
    private static final List<Long> HOT_USER_LIST = new ArrayList<>();
    static {
        HOT_USER_LIST.add(1L);
        HOT_USER_LIST.add(2L);
    }
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserService userService;
    @Resource
    RedisCacheUtil redisCacheUtil;
    @Scheduled(cron = "0 0 0 * * ? ")
    public void doCache(){
        //默认缓存第一页
        int pageSize = 5;
        int pageNum = 1;
        //1 设置分布式锁，得到锁的服务写缓存，抢不到的直接返回
        long id = Thread.currentThread().getId();
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        Boolean isLock = stringStringValueOperations.setIfAbsent(RedisConstant.PRECACHE_SCHEDULED_LOCK,
                String.valueOf(id), 10000, TimeUnit.MILLISECONDS);
        if (Boolean.TRUE.equals(isLock)){
            //2 获取重点用户（统计活跃度较高的用户，先写死）
            List<User> hotUsers = HOT_USER_LIST.stream().map(userId -> userService.searchUserById(userId))
                    .collect(Collectors.toList());
            Gson gson = new Gson();
            //3 查出每个重点用户的首页推荐，写入缓存
            for (User user : hotUsers) {
                Long userId = user.getId();
                String userPageKey = "" + userId + "&" + pageNum + "&" + pageSize;
                String userTagString = user.getTag();
                Type userListType = new TypeToken<List<String>>() {
                }.getType();
                List<String> userTagList = gson.fromJson(userTagString, userListType);
                List<User> recommendUsers = userService.searchUsersByMatchingTags(pageNum, pageSize, userTagList);
                //列表中排除自己
                recommendUsers = recommendUsers.stream().filter(resUser -> !Objects.equals(resUser.getId(),userId ))
                        .collect(Collectors.toList());
                redisCacheUtil.setCache(RedisConstant.RECOMMEND_USERS + userPageKey,
                        recommendUsers, 600L, TimeUnit.SECONDS);
            }
        }
        //释放锁?
    }
}
