package com.ame.usercenter.service.impl;

import com.ame.usercenter.common.ErrorCode;
import com.ame.usercenter.common.ResultUtils;
import com.ame.usercenter.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ame.usercenter.model.domain.User;
import com.ame.usercenter.service.UserService;
import com.ame.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ame.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author ameee
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2022-11-20 03:23:17
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 混淆密码
     */
    private static final String SALT = "ame";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 用户校验码
     * @param yuCode        编号
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String yuCode) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, yuCode)) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }


        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (yuCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编码过长");
        }


        // 账户不能包含特殊字符
        String validPaten = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPaten).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }

        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        //this 就是UserServiceImpl 继承了所有的方法
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // yuCode不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("yuCode", yuCode);
        //this 就是UserServiceImpl 继承了所有的方法
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }


        // 2.加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setYuCode(yuCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      cookies
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验,
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }


        // 账户不能包含特殊字符
        String validPaten = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPaten).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword ");
            return null;
        }

        //3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        //session 将用户的数据存储在session会话中 将safetyUser存储在session中 名称为USER_LOGIN_STATE
        //可以通过request.getSession().getAttribute()取出当前session的客户端数据 来分辨是哪位用户进行登录
        //USER_LOGIN_STATE 的作用就是 若用户进行了登录操作 其session中就会存放对应的内容 若通过上方方法getAttribute时 若没有数据
        //就能判断当前用户未登录
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;


    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除用户的登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;

    }

    /**
     * author ame
     *
     * @param username
     * @param request  测试用方法
     * @return
     */
    @Override
    public List<User> searchUsers(String username, HttpServletRequest request) {

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
           return null;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        //判断字符串是否为空
        //根据用户名进行查询
        if (StringUtils.isNoneBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = list(queryWrapper);
        //遍历处理List<User>中的user
        List<User> list = userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
        return list;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setYuCode(originUser.getYuCode());
        return safetyUser;
    }


}




