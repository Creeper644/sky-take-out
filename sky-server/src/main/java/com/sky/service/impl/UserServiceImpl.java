package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信登录接口
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties wechatProperties;
    @Autowired
    private UserMapper userMapper;
    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {

        String openid = getopenid(userLoginDTO.getCode());

        //判断openid是否为空
        if(openid == null){
            throw new RuntimeException(MessageConstant.LOGIN_FAILED);
        }
        //根据openid查询用户信息
        User user = userMapper.getByOpenid(openid);
        //如果用户不存在，则创建新用户
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        return user;
    }

    /**
     * 获取微信用户openid
     * @param code
     * @return
     */
    private String getopenid(String code) {
        //调用微信接口，获取用户openid
        Map<String,String> map = new HashMap<>();
        map.put("appid", wechatProperties.getAppid());
        map.put("secret", wechatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL,map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
