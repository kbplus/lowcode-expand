package com.kbplus.demo.ext.service;

import com.kbplus.demo.ext.entity.User2;
import com.kbplus.demo.ext.mapper.User2Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class User2Service {

    @Resource
    private User2Mapper user2Mapper;

    @Transactional
    public User2 get(Long id) {
        return user2Mapper.get(id);
    }

}
