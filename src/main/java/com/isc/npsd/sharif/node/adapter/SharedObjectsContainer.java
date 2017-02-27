package com.isc.npsd.sharif.node.adapter;

import com.isc.npsd.common.util.redis.RedisUtil;

import java.util.*;

/**
 * Created by A_Ahmady on 06/28/2016.
 **/
public class SharedObjectsContainer {
    public static Properties properties = new Properties();
    private static RedisUtil redisUtil;

    public static void setRedisUtil(RedisUtil redisUtil) {
        SharedObjectsContainer.redisUtil = redisUtil;
    }

    public static RedisUtil getRedisUtil() {
        if(redisUtil == null)
            redisUtil = RedisUtil.getInstance();
        return redisUtil;
    }
}
