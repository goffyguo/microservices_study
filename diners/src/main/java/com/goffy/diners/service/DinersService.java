package com.goffy.diners.service;

import cn.hutool.core.bean.BeanUtil;
import com.goffy.commons.ApiConstant;
import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.commons.utils.AssertUtil;
import com.goffy.commons.utils.ResultInfoUtil;
import com.goffy.diners.config.OAuth2ClientConfiguration;
import com.goffy.diners.domain.OAuthDinerInfo;
import com.goffy.diners.vo.LoginDinerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.ws.spi.http.HttpHandler;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/13/13:58
 * @Description:
 */
@Service
public class DinersService {
    @Resource
    private RestTemplate restTemplate;
    @Value("${service.name.ms-oauth-server}")
    private String oauthServerName;
    @Resource
    private OAuth2ClientConfiguration oAuth2ClientConfiguration;
    /**
     * 登录
     *
     * @param account  帐号：用户名或手机或邮箱
     * @param password 密码
     * @param servletPath     请求路径
     * @return
     */
    public ResultInfo signIn(String account, String password, String servletPath){
        // 参数校验
        AssertUtil.isNotEmpty(account, "请输入登录帐号");
        AssertUtil.isNotEmpty(password, "请输入登录密码");
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 构建请求体（请求参数）
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("username", account);
        body.add("password", password);
        body.setAll(BeanUtil.beanToMap(oAuth2ClientConfiguration));
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        // 设置 Authorization
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(oAuth2ClientConfiguration.getClientId(),
                oAuth2ClientConfiguration.getSecret()));
        // 发送请求
        ResponseEntity<ResultInfo> result = restTemplate.postForEntity(oauthServerName + "oauth/token", entity, ResultInfo.class);
        // 处理返回结果
        AssertUtil.isTrue(result.getStatusCode() != HttpStatus.OK, "登录失败");
        ResultInfo resultInfo = result.getBody();
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            // 登录失败
            resultInfo.setData(resultInfo.getMessage());
            return resultInfo;
        }
        // 这里的 Data 是一个 LinkedHashMap 转成了域对象 OAuthDinerInfo
        OAuthDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
                new OAuthDinerInfo(), false);
        // 根据业务需求返回视图对象
        LoginDinerInfo loginDinerInfo = new LoginDinerInfo();
        loginDinerInfo.setToken(dinerInfo.getAccessToken());
        loginDinerInfo.setAvatarUrl(dinerInfo.getAvatarUrl());
        loginDinerInfo.setNickname(dinerInfo.getNickname());
        return ResultInfoUtil.buildSuccess(servletPath, loginDinerInfo);
    };
}
