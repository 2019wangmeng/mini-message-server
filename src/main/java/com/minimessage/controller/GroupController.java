package com.minimessage.controller;

import com.minimessage.annotation.GlobalInterceptor;
import com.minimessage.entity.dto.TokenUserInfoDto;
import com.minimessage.entity.enums.GroupStatusEnum;
import com.minimessage.entity.enums.UserContactStatusEnum;
import com.minimessage.entity.enums.UserContactTypeEnum;
import com.minimessage.entity.po.GroupInfo;
import com.minimessage.entity.po.UserContact;
import com.minimessage.entity.query.GroupInfoQuery;
import com.minimessage.entity.query.UserContactQuery;
import com.minimessage.entity.vo.GroupInfoVO;
import com.minimessage.entity.vo.ResponseVO;
import com.minimessage.exception.BusinessException;
import com.minimessage.service.GroupInfoService;
import com.minimessage.service.UserContactService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController extends ABaseController {

    @Resource
    private GroupInfoService groupInfoService;
    @Resource
    private UserContactService userContactService;

    /**
     * 创建或修改群组
     *
     * @param request
     * @param groupId
     * @param groupName
     * @param groupNotice
     * @param joinType
     * @param avatarFile
     * @param avatarCover
     * @return
     */
    @RequestMapping("/saveGroup")
    @GlobalInterceptor
    public ResponseVO saveGroup(HttpServletRequest request,
                                String groupId,
                                @NotEmpty String groupName,
                                String groupNotice,
                                @NotNull Integer joinType,
                                MultipartFile avatarFile,
                                MultipartFile avatarCover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroupOwnerId(tokenUserInfoDto.getUserId());
        groupInfo.setGroupId(groupId);
        groupInfo.setGroupName(groupName);
        groupInfo.setGroupNotice(groupNotice);
        groupInfo.setJoinType(joinType);
        groupInfoService.saveGroup(groupInfo, avatarFile, avatarCover);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取我创建的群组
     *
     * @param request
     * @return
     */
    @RequestMapping("/loadMyGroup")
    @GlobalInterceptor
    public ResponseVO loadMyGroup(HttpServletRequest request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
        groupInfoQuery.setGroupOwnerId(tokenUserInfoDto.getUserId());
        groupInfoQuery.setOrderBy("create_time desc");
        List<GroupInfo> groupInfoList = groupInfoService.findListByParam(groupInfoQuery);
        return getSuccessResponseVO(groupInfoList);
    }

    /**
     * 获取群聊详情
     *
     * @param request
     * @param groupId
     * @return
     */
    @RequestMapping("/getGroupInfo")
    @GlobalInterceptor
    public ResponseVO getGroupInfo(HttpServletRequest request,
                                   @NotEmpty String groupId){
        GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setContactId(groupId);
        Integer memberCount = userContactService.findCountByParam(userContactQuery);
        groupInfo.setMemberCount(memberCount);
        return getSuccessResponseVO(groupInfo);
    }

    private GroupInfo getGroupDetailCommon(HttpServletRequest request,String groupId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContact userContact = userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(),groupId);
        if (userContact == null || !UserContactStatusEnum.FRIEND.getStatus().equals(userContact.getStatus())){
            throw new BusinessException("你不在群聊或者群聊不存在或已经解散");
        }
        GroupInfo groupInfo = groupInfoService.getGroupInfoByGroupId(groupId);
        if (groupInfo == null || !GroupStatusEnum.NORMAL.getStatus().equals(groupInfo.getStatus())){
            throw new BusinessException("群聊不存在或已经解散");
        }
        return groupInfo;
    }

    /**
     * 获取聊天会话群聊详情
     *
     * @param request
     * @param groupId
     * @return
     */
    @RequestMapping("/getGroupInfo4Chat")
    @GlobalInterceptor
    public ResponseVO getGroupInfo4Chat(HttpServletRequest request, @NotEmpty String groupId){
        GroupInfo groupInfo = getGroupDetailCommon(request, groupId);
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setContactId(groupInfo.getGroupId());
        userContactQuery.setQueryUserInfo(true);
        userContactQuery.setOrderBy("create_time asc");
        userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        List<UserContact> userContactList = userContactService.findListByParam(userContactQuery);
        GroupInfoVO groupInfoVO = new GroupInfoVO();
        groupInfoVO.setGroupInfo(groupInfo);
        groupInfoVO.setUserContactList(userContactList);
        return getSuccessResponseVO(groupInfoVO);
    }
}
