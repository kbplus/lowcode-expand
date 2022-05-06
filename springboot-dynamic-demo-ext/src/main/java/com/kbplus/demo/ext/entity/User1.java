package com.kbplus.demo.ext.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 CREATE TABLE `tab_user` (
 `id` bigint(64) NOT NULL AUTO_INCREMENT,
 `name` varchar(32) NOT NULL,
 PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
 */
public class User1 {

    @ApiModelProperty(value = "user主键ID")
    private Long id;

    @ApiModelProperty(value = "user名字")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
