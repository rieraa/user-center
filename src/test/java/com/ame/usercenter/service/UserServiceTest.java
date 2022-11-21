package com.ame.usercenter.service;


import com.ame.usercenter.model.domain.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author ame
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void addUser() {
        User user = new User();


        user.setUsername("ame");
        user.setUserAccount("123");
        user.setAvatarUrl("https://i1.hdslb.com/bfs/face/cf1ef9c2045e317dfe6dbe8659b6f7a69c7572d5.jpg@240w_240h_1c_1s.webp");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setEmail("456");
        user.setPhone("123");
        user.setIsDelete(0);


        boolean result = userService.save(user);
        System.out.println(user.getId());
//        断言
        assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "amee";
        String userPassword = "";
        String checkPassword = "123456";
        //非空
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        //账户长度
        userAccount = "am";
        userPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        //密码8位
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        //特殊字符校验
        userAccount = "am ee";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        //密码相同
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);


        userAccount = "ameeee";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        userAccount = "ameee9";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);




    }
}