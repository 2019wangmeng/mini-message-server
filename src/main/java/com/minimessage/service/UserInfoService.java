package com.minimessage.service;

import com.minimessage.entity.vo.UserInfoVO;

public interface UserInfoService {
    void register(String email, String nickName, String password);

    UserInfoVO login(String email, String password);
}
