package com.minimessage.service.impl;

import com.minimessage.entity.constants.Constants;
import com.minimessage.entity.dto.TokenUserInfoDto;
import com.minimessage.entity.enums.*;
import com.minimessage.entity.po.GroupInfo;
import com.minimessage.entity.po.UserContact;
import com.minimessage.entity.po.UserInfo;
import com.minimessage.entity.query.GroupInfoQuery;
import com.minimessage.entity.query.UserContactQuery;
import com.minimessage.entity.query.UserInfoQuery;
import com.minimessage.exception.BusinessException;
import com.minimessage.mappers.GroupInfoMapper;
import com.minimessage.mappers.UserContactMapper;
import com.minimessage.mappers.UserInfoMapper;
import com.minimessage.service.UserContactApplyService;
import com.minimessage.service.UserContactService;
import com.minimessage.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserContactApplyServiceImpl implements UserContactApplyService {

    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private UserContactService userContactService;

    @Override
    public Integer applyAdd(TokenUserInfoDto tokenUserInfoDto, String contactId, String contactType, String applyInfo) {
        UserContactTypeEnum typeEnum = UserContactTypeEnum.getByName(contactType);
        if (typeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        //申请人
        String applyUserId = tokenUserInfoDto.getUserId();
        applyInfo = StringTools.isEmpty(applyInfo) ? String.format(Constants.APPLY_INFO_TEMPLATE, tokenUserInfoDto.getNickName()) : applyInfo;
        Long curDate = System.currentTimeMillis();
        Integer joinType = null;
        String receiveUserId = contactId;
        UserContact userContact = userContactMapper.selectByUserIdAndContactId(applyUserId, contactId);
        if (userContact != null && ArrayUtils.contains(new Integer[]{UserContactStatusEnum.BLACKLIST_BE.getStatus(), UserContactStatusEnum.BLACKLIST_BE_FIRST.getStatus()}, userContact.getStatus())) {
            throw new BusinessException("对方已经把你拉黑，无法添加");
        }
        if (UserContactTypeEnum.GROUP == typeEnum) {
            GroupInfo groupInfo = groupInfoMapper.selectByGroupId(contactId);
            if (groupInfo == null || GroupStatusEnum.DISSOLUTION.getStatus().equals(groupInfo.getStatus())) {
                throw new BusinessException("群聊不存在或已解散");
            }
            receiveUserId = groupInfo.getGroupOwnerId();
            joinType = groupInfo.getJoinType();
        } else {
            UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
            if (userInfo == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            joinType = userInfo.getJoinType();
        }
        if (JoinTypeEnum.JOIN.getType().equals(joinType)) {//直接加入，不用审核
            userContactService.addContact(applyUserId, receiveUserId, contactId, typeEnum.getType(), applyInfo);
            return joinType;
        }
        return joinType;
    }
}
