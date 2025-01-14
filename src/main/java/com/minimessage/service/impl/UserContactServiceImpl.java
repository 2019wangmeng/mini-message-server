package com.minimessage.service.impl;

import com.minimessage.entity.dto.SysSettingDto;
import com.minimessage.entity.enums.MessageStatusEnum;
import com.minimessage.entity.enums.MessageTypeEnum;
import com.minimessage.entity.enums.UserContactStatusEnum;
import com.minimessage.entity.enums.UserContactTypeEnum;
import com.minimessage.entity.po.ChatMessage;
import com.minimessage.entity.po.ChatSession;
import com.minimessage.entity.po.ChatSessionUser;
import com.minimessage.entity.po.UserContact;
import com.minimessage.entity.query.ChatMessageQuery;
import com.minimessage.entity.query.ChatSessionQuery;
import com.minimessage.entity.query.ChatSessionUserQuery;
import com.minimessage.entity.query.UserContactQuery;
import com.minimessage.mappers.ChatMessageMapper;
import com.minimessage.mappers.ChatSessionMapper;
import com.minimessage.mappers.ChatSessionUserMapper;
import com.minimessage.mappers.UserContactMapper;
import com.minimessage.redis.RedisComponent;
import com.minimessage.service.UserContactService;
import com.minimessage.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

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
}
