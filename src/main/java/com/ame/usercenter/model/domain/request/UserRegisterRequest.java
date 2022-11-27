package com.ame.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author ame
 */
@Data
public class UserRegisterRequest implements Serializable {
    //继承序列化接口实现
    private static final long serialVersionUID = 5644878509618037801L;


    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String yuCode;
}
