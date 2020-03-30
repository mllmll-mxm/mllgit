package com.mll.mp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mll.mp.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends BaseMapper<User> {
}
