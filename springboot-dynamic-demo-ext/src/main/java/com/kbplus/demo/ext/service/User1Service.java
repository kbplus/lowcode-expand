package com.kbplus.demo.ext.service;

import com.kbplus.demo.ext.entity.User1;
import com.kbplus.demo.ext.mapper.User1Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class User1Service {

    @Resource
    private User1Mapper user1Mapper;

    @Transactional
    public User1 get(Long id) {
        return user1Mapper.get(id);
    }


    public List<User1> getAll() {
        return user1Mapper.getAll();
    }
}
