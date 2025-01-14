package com.minimessage.service.impl;

import com.minimessage.entity.enums.BeautyAccountStatusEnum;
import com.minimessage.entity.enums.UserContactStatusEnum;
import com.minimessage.entity.enums.UserContactTypeEnum;
import com.minimessage.entity.enums.UserStatusEnum;
import com.minimessage.entity.po.UserContact;
import com.minimessage.entity.po.UserInfo;
import com.minimessage.entity.po.UserInfoBeauty;
import com.minimessage.entity.query.UserContactQuery;
import com.minimessage.entity.query.UserInfoBeautyQuery;
import com.minimessage.entity.query.UserInfoQuery;
import com.minimessage.entity.vo.UserInfoVO;
import com.minimessage.exception.BusinessException;
import com.minimessage.mappers.UserContactMapper;
import com.minimessage.mappers.UserInfoBeautyMapper;
import com.minimessage.mappers.UserInfoMapper;
import com.minimessage.redis.RedisComponent;
import com.minimessage.service.UserContactService;
import com.minimessage.service.UserInfoService;
import com.minimessage.utils.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private UserInfoBeautyMapper<UserInfoBeauty, UserInfoBeautyQuery> userInfoBeautyMapper;
    @Resource
    private UserContactService userContactService;

    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private RedisComponent redisComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("邮箱账号已经存在");
        }
        Date curDate = new Date();
        String userId = StringTools.getUserId();
        //判断该用户是否指定靓号
        UserInfoBeauty userInfoBeauty = userInfoBeautyMapper.selectByEmail(email);
        Boolean userBeautyAccount = userInfoBeauty != null && userInfoBeauty.getStatus().equals(BeautyAccountStatusEnum.NO_USE.getStatus());
        if (userBeautyAccount){
            userId = UserContactTypeEnum.USER.getPrefix() + userInfoBeauty.getUserId();
        }
        //新增用户信息
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(nickName);
        userInfo.setEmail(email);
        userInfo.setPassword(StringTools.encodeByMD5(password));
        userInfo.setCreateTime(curDate);
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        userInfo.setLastOffTime(curDate.getTime());
        userInfoMapper.insert(userInfo);
        //更新靓号状态
        if (userBeautyAccount){
            UserInfoBeauty userInfoBeauty1 = new UserInfoBeauty();
            userInfoBeauty1.setStatus(BeautyAccountStatusEnum.USEED.getStatus());
            userInfoBeautyMapper.updateById(userInfoBeauty1,userInfoBeauty.getId());
        }
        //添加机器人为默认好友
        userContactService.addContact4Robot(userId);
    }

    @Override
    public UserInfoVO login(String email, String password) {
        UserInfoVO userInfoVO = new UserInfoVO();
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (null == userInfo || !password.equals(userInfo.getPassword())){
            throw new BusinessException("账号或者密码错误");
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())){
            throw new BusinessException("账号已禁用");
        }
        //查询联系人
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setUserId(userInfoVO.getUserId());
        userContactQuery.setStatusArray(new Integer[]{UserContactStatusEnum.FRIEND.getStatus()});
        List<UserContact> userContacts = userContactMapper.selectList(userContactQuery);
        List<String> contactIdList = userContacts.stream().map(UserContact::getContactId).collect(Collectors.toList());
        if (!contactIdList.isEmpty()){
            redisComponent
        }
        return userInfoVO;
    }
}
