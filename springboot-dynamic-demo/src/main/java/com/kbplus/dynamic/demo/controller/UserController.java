package com.kbplus.dynamic.demo.controller;

import com.kbplus.dynamic.demo.entity.User;
import com.kbplus.dynamic.demo.service.ModuleServer;
import com.kbplus.dynamic.demo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Api(value = "UserController", tags = "用户管理Api")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleServer moduleServer;

    @ApiOperation(nickname = "get", value = "根据ID获取用户")
    @GetMapping("get/{id}")
    public String get(@PathVariable(value = "id") Long id){

        return userService.get(id).toString();
    }

}
