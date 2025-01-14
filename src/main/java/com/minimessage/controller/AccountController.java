package com.minimessage.controller;

import com.minimessage.entity.constants.Constants;
import com.minimessage.entity.vo.ResponseVO;
import com.minimessage.exception.BusinessException;
import com.minimessage.redis.RedisUtils;
import com.minimessage.service.UserInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("accountController")
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/checkCode")
    public ResponseVO checkCode() {
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(100, 42);
        String code = arithmeticCaptcha.text();
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.setx(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, 60);
        String base64 = arithmeticCaptcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("checkCode", base64);
        result.put("checkCodeKey", checkCodeKey);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty String checkCodeKey,
                               @NotEmpty @Email String email,
                               @NotEmpty String password,
                               @NotEmpty String nickName,
                               @NotEmpty String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey)))
                throw new BusinessException("图片验证码不正确");
            userInfoService.register(email,nickName,password);
            return getSuccessResponseVO(null);
        } finally {
            redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
        }
    }
}
