package com.kbplus.dynamic.demo.mapper;

import com.kbplus.dynamic.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Mapper
public interface UserMapper {

    User get(Long id);
}
