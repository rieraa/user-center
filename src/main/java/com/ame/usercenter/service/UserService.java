package com.ame.usercenter.service;

import com.ame.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author ameee
 */
public interface UserService extends IService<User> {

    /**
     * user register
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 用户校验码
     * @param yuCode         编号
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String yuCode);

    /**
     * user login
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      cookies
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);


    /**
     * author ame
     * @param username
     * @param request
     *
     * 测试用方法
     * @return
     */
    List<User> searchUsers(String username, HttpServletRequest request);
}
