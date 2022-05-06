package com.kbplus.dynamic.demo.service;

import com.kbplus.dynamic.demo.entity.User;
import com.kbplus.dynamic.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User get(Long id){
        return userMapper.get(id);
    }
}
