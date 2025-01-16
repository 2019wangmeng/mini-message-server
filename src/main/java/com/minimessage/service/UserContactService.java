package com.minimessage.service;

import com.minimessage.entity.dto.UserContactSearchResultDto;
import com.minimessage.entity.po.UserContact;
import com.minimessage.entity.query.UserContactQuery;

import java.util.List;

public interface UserContactService {
    void addContact4Robot(String userId);

    UserContact getUserContactByUserIdAndContactId(String userId, String groupId);

    Integer findCountByParam(UserContactQuery userContactQuery);

    List<UserContact> findListByParam(UserContactQuery userContactQuery);

    UserContactSearchResultDto searchContact(String userId, String contactId);

    void addContact(String applyUserId, String receiveUserId, String contactId, Integer type, String applyInfo);
}
