package com.kbplus.demo.ext.controller;

import com.kbplus.demo.ext.entity.User1;
import com.kbplus.demo.ext.service.User1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/user1")
@Api(value = "TestController", tags = "扩展用户")
public class User1Controller {

    @Autowired
    private User1Service user1Service;

    @ApiOperation(nickname = "get", value = "根据ID获取用户")
    @PostMapping("/get")
    public User1 test(@RequestBody User1 user1) {
        Long id = user1.getId();
        return user1Service.get(id);
    }

    @ApiOperation(nickname = "getAll", value = "无参数获取所有用户")
    @PostMapping("/getAll")
    public List<User1> getAll() {
        return user1Service.getAll();
    }

}
