package com.ame.usercenter.mapper;

import com.ame.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ameee
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2022-11-20 03:23:17
 * @Entity User
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

}




