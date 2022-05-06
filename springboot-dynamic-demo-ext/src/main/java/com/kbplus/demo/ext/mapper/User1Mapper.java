package com.kbplus.demo.ext.mapper;

import com.kbplus.demo.ext.entity.User1;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface User1Mapper {

    User1 get(Long id);


    List<User1> getAll();
}
