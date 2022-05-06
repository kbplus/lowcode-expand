package com.kbplus.demo.ext.controller;

import com.kbplus.demo.ext.entity.User2;
import com.kbplus.demo.ext.service.User2Service;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/user2")
@Api(value = "TestController", tags = "扩展用户")
public class User2Controller {

    @Autowired
    private User2Service user1Service;

    @GetMapping("/get")
    public User2 test(@RequestParam("id") Long id) {
        return user1Service.get(id);
    }

}
