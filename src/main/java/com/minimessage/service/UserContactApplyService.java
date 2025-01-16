package com.minimessage.service;

import com.minimessage.entity.dto.TokenUserInfoDto;

public interface UserContactApplyService {
    Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String contactType, String applyInfo);
}
