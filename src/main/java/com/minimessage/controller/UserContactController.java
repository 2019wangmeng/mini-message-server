package com.minimessage.controller;

import com.minimessage.annotation.GlobalInterceptor;
import com.minimessage.entity.dto.TokenUserInfoDto;
import com.minimessage.entity.dto.UserContactSearchResultDto;
import com.minimessage.entity.vo.ResponseVO;
import com.minimessage.service.UserContactApplyService;
import com.minimessage.service.UserContactService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/contact")
public class UserContactController extends ABaseController {

    @Resource
    private UserContactService userContactService;
    @Resource
    private UserContactApplyService userContactApplyService;

    /**
     * 搜索好友或者群聊
     *
     * @param request
     * @param contactId
     * @return
     */
    @RequestMapping("/search")
    @GlobalInterceptor
    public ResponseVO search(HttpServletRequest request, @NotEmpty String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserContactSearchResultDto resultDto = userContactService.searchContact(tokenUserInfoDto.getUserId(), contactId);
        return getSuccessResponseVO(resultDto);
    }

    /**
     * 申请好友或者入群申请
     *
     * @param request
     * @param contactId
     * @param contactType
     * @param applyInfo
     * @return
     */
    @RequestMapping("/applyAdd")
    @GlobalInterceptor
    public ResponseVO applyAdd(HttpServletRequest request, @NotEmpty String contactId, @NotEmpty String contactType, String applyInfo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        Integer joinType = userContactApplyService.applyAdd(tokenUserInfoDto,contactId,contactType,applyInfo);
        return getSuccessResponseVO(joinType);
    }
}
