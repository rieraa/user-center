package com.ame.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author ame
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 177264405224383883L;
    //继承序列化接口实现



    private String userAccount;

    private String userPassword;



}
