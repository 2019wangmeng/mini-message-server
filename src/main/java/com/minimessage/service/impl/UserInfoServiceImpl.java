package com.minimessage.service.impl;

import com.minimessage.entity.po.UserInfo;
import com.minimessage.entity.query.UserInfoQuery;
import com.minimessage.exception.BusinessException;
import com.minimessage.mappers.UserInfoMapper;
import com.minimessage.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Override
    public void register(String email, String nickName, String password) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("邮箱账号已经存在");
        }
        Date curDate = new Date();

    }
}
