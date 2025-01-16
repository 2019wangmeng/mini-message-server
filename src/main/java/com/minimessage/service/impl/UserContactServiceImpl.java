package com.minimessage.service.impl;

import com.minimessage.entity.dto.SysSettingDto;
import com.minimessage.entity.dto.UserContactSearchResultDto;
import com.minimessage.entity.enums.MessageStatusEnum;
import com.minimessage.entity.enums.MessageTypeEnum;
import com.minimessage.entity.enums.UserContactStatusEnum;
import com.minimessage.entity.enums.UserContactTypeEnum;
import com.minimessage.entity.po.*;
import com.minimessage.entity.query.*;
import com.minimessage.exception.BusinessException;
import com.minimessage.mappers.*;
import com.minimessage.redis.RedisComponent;
import com.minimessage.service.UserContactService;
import com.minimessage.utils.CopyTools;
import com.minimessage.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserContactServiceImpl implements UserContactService {

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;
    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addContact4Robot(String userId) {
        Date curDate = new Date();
        SysSettingDto sysSetting = redisComponent.getSysSetting();
        String contactId = sysSetting.getRobotUid();
        String contactName = sysSetting.getRobotNickName();
        String sendMessage = sysSetting.getRobotWelcome();
        sendMessage = StringTools.cleanHtmlTag(sendMessage);
        //添加机器人好友
        UserContact userContact = new UserContact();
        userContact.setUserId(userId);
        userContact.setContactId(contactId);
        userContact.setContactType(UserContactTypeEnum.USER.getType());
        userContact.setCreateTime(curDate);
        userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        userContact.setLastUpdateTime(curDate);
        userContactMapper.insert(userContact);

        //增加会话信息
        ChatSession chatSession = new ChatSession();
        String sessionId = StringTools.getChatSessionId4User(new String[]{userId, contactId});
        chatSession.setLastMessage(sendMessage);
        chatSession.setSessionId(sessionId);
        chatSession.setLastReceiveTime(curDate.getTime());
        chatSessionMapper.insert(chatSession);
        ChatSessionUser chatSessionUser = new ChatSessionUser();
        chatSessionUser.setUserId(userId);
        chatSessionUser.setContactId(contactId);
        chatSessionUser.setContactName(contactName);
        chatSessionUser.setSessionId(sessionId);
        chatSessionUserMapper.insert(chatSessionUser);
        //增加聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setMessageType(MessageTypeEnum.CHAT.getType());
        chatMessage.setMessageContent(sendMessage);
        chatMessage.setSendUserId(contactId);
        chatMessage.setSendUserNickName(contactName);
        chatMessage.setSendTime(curDate.getTime());
        chatMessage.setContactId(userId);
        chatMessage.setContactType(UserContactTypeEnum.USER.getType());
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
        chatMessageMapper.insert(chatMessage);
    }

    @Override
    public UserContact getUserContactByUserIdAndContactId(String userId, String groupId) {
        return userContactMapper.selectByUserIdAndContactId(userId, groupId);
    }

    @Override
    public Integer findCountByParam(UserContactQuery userContactQuery) {
        return userContactMapper.selectCount(userContactQuery);
    }

    @Override
    public List<UserContact> findListByParam(UserContactQuery userContactQuery) {
        return userContactMapper.selectList(userContactQuery);
    }

    @Override
    public UserContactSearchResultDto searchContact(String userId, String contactId) {
        UserContactTypeEnum byPrefix = UserContactTypeEnum.getByPrefix(contactId);
        if (byPrefix == null) {
            return null;
        }
        UserContactSearchResultDto resultDto = new UserContactSearchResultDto();
        switch (byPrefix) {
            case USER:
                UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
                if (userInfo == null) {
                    return null;
                }
                resultDto = CopyTools.copy(userInfo, UserContactSearchResultDto.class);
                break;
            case GROUP:
                GroupInfo groupInfo = groupInfoMapper.selectByGroupId(contactId);
                if (groupInfo == null) {
                    return null;
                }
                resultDto.setNickName(groupInfo.getGroupName());
                break;
        }
        resultDto.setContactType(byPrefix.toString());
        resultDto.setContactId(contactId);
        if (userId.equals(contactId)){
            resultDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            return resultDto;
        }
        //查询是否是好友关系
        UserContact userContact = userContactMapper.selectByUserIdAndContactId(userId, contactId);
        resultDto.setStatus(userContact == null ? null : userContact.getStatus());
        return resultDto;
    }

    @Override
    public void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo) {
        if (UserContactTypeEnum.GROUP.getType().equals(contactType)){//如果是申请加群，需要判断当前群是否达到人数上限
            UserContactQuery userContactQuery = new UserContactQuery();
            userContactQuery.setContactId(contactId);
            userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            Integer count = userContactMapper.selectCount(userContactQuery);
            SysSettingDto sysSetting = redisComponent.getSysSetting();
            if (count >= sysSetting.getMaxGroupMemberCount()){
                throw new BusinessException("成员已满，无法加入");
            }
            Date curDate = new Date();
        }
    }
}
