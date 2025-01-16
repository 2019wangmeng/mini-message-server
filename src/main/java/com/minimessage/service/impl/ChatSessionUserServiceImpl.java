package com.minimessage.service.impl;

import com.minimessage.entity.po.ChatSessionUser;
import com.minimessage.entity.query.ChatSessionUserQuery;
import com.minimessage.mappers.ChatSessionUserMapper;
import com.minimessage.service.ChatSessionUserService;
import com.minimessage.utils.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChatSessionUserServiceImpl implements ChatSessionUserService {

    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;

    @Override
    public void updateRedundanceInfo(String contactName, String contactId) {
        if (StringTools.isEmpty(contactName)){
            return;
        }
        ChatSessionUser updateInfo = new ChatSessionUser();
        updateInfo.setContactName(contactName);
        ChatSessionUserQuery chatSessionUserQuery = new ChatSessionUserQuery();
        chatSessionUserQuery.setContactId(contactId);
        chatSessionUserMapper.updateByParam(updateInfo,chatSessionUserQuery);

        //todo 发送联系人昵称修改消息
    }
}
