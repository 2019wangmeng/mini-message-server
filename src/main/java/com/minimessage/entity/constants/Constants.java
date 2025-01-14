package com.minimessage.entity.constants;

import com.minimessage.entity.enums.UserContactTypeEnum;

public class Constants {

    public static final String REDIS_KEY_CHECK_CODE = "minimessage:checkcode:";
    public static final Integer ONE = 1;
    public static final Integer ZERO = 0;
    public static final Integer LENGTH_11 = 11;

    public static final String REDIS_KEY_SYS_SETTING = "minimessage:syssetting:";
    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix() + "robot";
}
