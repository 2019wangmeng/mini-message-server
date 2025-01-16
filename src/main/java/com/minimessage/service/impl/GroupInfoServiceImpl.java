package com.minimessage.service.impl;

import com.minimessage.entity.config.AppConfig;
import com.minimessage.entity.constants.Constants;
import com.minimessage.entity.dto.SysSettingDto;
import com.minimessage.entity.enums.*;
import com.minimessage.entity.po.*;
import com.minimessage.entity.query.*;
import com.minimessage.exception.BusinessException;
import com.minimessage.mappers.*;
import com.minimessage.redis.RedisComponent;
import com.minimessage.service.ChatSessionUserService;
import com.minimessage.service.GroupInfoService;
import com.minimessage.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class GroupInfoServiceImpl implements GroupInfoService {
    private static final Logger logger = LoggerFactory.getLogger(GroupInfoService.class);

    @Resource
    private GroupInfoMapper<GroupInfo,GroupInfoQuery> groupInfoMapper;
    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;
    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private ChatSessionUserService chatSessionUserService;
    @Resource
    private AppConfig appConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) {
        Date curDate = new Date();
        //新增群
        if (StringTools.isEmpty(groupInfo.getGroupId())){
            GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
            groupInfoQuery.setGroupOwnerId(groupInfo.getGroupOwnerId());
            Integer count = groupInfoMapper.selectCount(groupInfoQuery);
            SysSettingDto sysSetting = redisComponent.getSysSetting();
            if (count >= sysSetting.getMaxGroupCount()){
                throw new BusinessException("最多只能创建" + sysSetting.getMaxGroupCount() + "个群组");
            }
            if (avatarFile == null){
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            groupInfo.setCreateTime(curDate);
            groupInfo.setGroupId(StringTools.getGroupId());
            groupInfoMapper.insert(groupInfo);
            //将群组添加为联系人
            UserContact userContact = new UserContact();
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            userContact.setContactType(UserContactTypeEnum.GROUP.getType());
            userContact.setContactId(groupInfo.getGroupId());
            userContact.setUserId(groupInfo.getGroupOwnerId());
            userContact.setCreateTime(curDate);
            userContact.setLastUpdateTime(curDate);
            userContactMapper.insert(userContact);
            //创建会话
            String sessionId = StringTools.getChatSessionId4Group(groupInfo.getGroupId());
            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setLastMessage(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatSession.setLastReceiveTime(curDate.getTime());
            chatSessionMapper.insert(chatSession);
            //创建群主会话
            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(groupInfo.getGroupOwnerId());
            chatSessionUser.setContactId(groupInfo.getGroupId());
            chatSessionUser.setContactName(groupInfo.getGroupName());
            chatSessionUser.setSessionId(sessionId);
            chatSessionUserMapper.insert(chatSessionUser);
            //将该群组添加为群主的联系人(缓存中)
            redisComponent.addUserContact(groupInfo.getGroupOwnerId(),groupInfo.getGroupId());
            //todo 发送通道消息
            //创建会话消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(MessageTypeEnum.GROUP_CREATE.getType());
            chatMessage.setMessageContent(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatMessage.setSendUserId(null);
            chatMessage.setSendUserNickName(null);
            chatMessage.setSendTime(curDate.getTime());
            chatMessage.setContactId(groupInfo.getGroupId());
            chatMessage.setContactType(UserContactTypeEnum.GROUP.getType());
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
            chatMessageMapper.insert(chatMessage);
            //todo 发送ws消息
        }else {//更新群
            GroupInfo gInfo = groupInfoMapper.selectByGroupId(groupInfo.getGroupId());
            if (!gInfo.getGroupOwnerId().equals(groupInfo.getGroupOwnerId())){
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            groupInfoMapper.updateByGroupId(groupInfo,groupInfo.getGroupId());
            //更新冗余的群信息
            String contactNameUpdate = null;
            if (!gInfo.getGroupName().equals(groupInfo.getGroupName())){
                contactNameUpdate = groupInfo.getGroupName();
            }
            chatSessionUserService.updateRedundanceInfo(contactNameUpdate,groupInfo.getGroupId());
        }
        if (avatarFile == null){
            return;
        }
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
        if (!targetFileFolder.exists()){
            targetFileFolder.mkdirs();
        }
        String filePath = targetFileFolder.getPath() + "/" + groupInfo.getGroupId() + Constants.IMAGE_SUFFIX;
        try {
            avatarFile.transferTo(new File(filePath));
            avatarCover.transferTo(new File(filePath+Constants.COVER_IMAGE_SUFFIX));
        } catch (IOException e) {
            logger.error("群头像上传失败",e);
            throw new BusinessException("头像上传失败");
        }
    }

    @Override
    public List<GroupInfo> findListByParam(GroupInfoQuery groupInfoQuery) {
        return groupInfoMapper.selectList(groupInfoQuery);
    }

    @Override
    public GroupInfo getGroupInfoByGroupId(String groupId) {
        return groupInfoMapper.selectByGroupId(groupId);
    }
}
