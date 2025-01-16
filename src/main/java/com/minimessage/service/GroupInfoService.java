package com.minimessage.service;

import com.minimessage.entity.po.GroupInfo;
import com.minimessage.entity.query.GroupInfoQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupInfoService {
    void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover);

    List<GroupInfo> findListByParam(GroupInfoQuery groupInfoQuery);

    GroupInfo getGroupInfoByGroupId(String groupId);
}
