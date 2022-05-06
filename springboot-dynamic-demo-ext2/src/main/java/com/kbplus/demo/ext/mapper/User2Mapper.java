package com.kbplus.demo.ext.mapper;

import com.kbplus.demo.ext.entity.User2;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface User2Mapper {

    User2 get(Long id);
}
