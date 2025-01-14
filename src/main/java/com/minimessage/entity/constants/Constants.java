package com.minimessage.entity.constants;

import com.minimessage.entity.enums.UserContactTypeEnum;

public class Constants {

    public static final String REDIS_KEY_CHECK_CODE = "minimessage:checkcode:";
    public static final Integer ONE = 1;
    public static final Integer ZERO = 0;
    public static final Integer LENGTH_11 = 11;

    public static final String REDIS_KEY_SYS_SETTING = "minimessage:syssetting:";
    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix() + "robot";
    public static final String REDIS_KEY_USER_CONTACT = "easychat:ws:user:contact:";

    /**
     * 过期时间 1分钟
     */
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;


    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT = 6;

    /**
     * 过期时间 1天
     */
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;


    public static final Integer REDIS_KEY_TOKEN_EXPIRES = REDIS_KEY_EXPIRES_DAY * 2;

    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "easychat:ws:user:heartbeat";
    public static final Integer LENGTH_20 = 20;
    public static final String REDIS_KEY_WS_TOKEN = "easychat:ws:token:";
    public static final String REDIS_KEY_WS_TOKEN_USERID = "easychat:ws:token:userid";
}
