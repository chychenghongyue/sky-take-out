package com.sky.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User weLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口，获取当前用户的openid
        String openId = getOpenId(userLoginDTO.getCode());
        //调用openid，判断是否为空
        if (openId == null) {
            try {
                throw new LoginException(MessageConstant.LOGIN_FAILED);
            } catch (LoginException e) {
                throw new RuntimeException(e);
            }
        }
        //判断是否为新用户，自动完成注册

        User user = userMapper.getByOpenId(openId);
        if (user == null) {
            user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        log.info("user:{}", user);
        return user;
    }

    private String getOpenId(String code) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");
        String string = HttpClientUtil.doGet(WX_LOGIN, paramMap);
        log.info("请求返回值:{}", string);
        String openId;
        try {
            JsonNode jsonNode = objectMapper.readValue(string, JsonNode.class);
            openId = String.valueOf(jsonNode.get("openid"));
            log.info("openid:{}", openId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return openId;
    }
}
