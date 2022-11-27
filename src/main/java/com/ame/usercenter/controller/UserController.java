package com.ame.usercenter.controller;

import com.ame.usercenter.common.BaseResponse;
import com.ame.usercenter.common.ErrorCode;
import com.ame.usercenter.common.ResultUtils;
import com.ame.usercenter.exception.BusinessException;
import com.ame.usercenter.model.domain.User;
import com.ame.usercenter.model.domain.request.UserLoginRequest;
import com.ame.usercenter.model.domain.request.UserRegisterRequest;
import com.ame.usercenter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ame.usercenter.common.ErrorCode.PARAMS_ERROR;
import static com.ame.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.ame.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author ame
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String yuCode = userRegisterRequest.getYuCode();


        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, yuCode)) {
            return null;
        }

        long result = userService.userRegister(userAccount, userPassword, checkPassword, yuCode);
//        return new BaseResponse<>(0, result, "ok");

        return ResultUtils.success(result);

    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }

        User user = userService.userLogin(userAccount, userPassword, request);
//        return new BaseResponse<>(0, user, "ok");

        return ResultUtils.success(user);

    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        int result = userService.userLogout(request);
        return ResultUtils.success(result);

    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        //从用户的登录请求中取得Session
        //Session 作为用户登录过的一个凭据
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //由于用户的信息可能更新 session中存储的信息过时 所以这里选择查库 重新返回一个用户的信息
        Long userId = currentUser.getId();
        User user = userService.getById(userId);
        //返回脱敏后的数据
        //todo 校验用户是否合法
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);

    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        //判断字符串是否为空
        //根据用户名进行查询
        if (StringUtils.isNoneBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        //遍历处理List<User>中的user
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);

        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 鉴权 仅管理员可进行查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;

    }
}
