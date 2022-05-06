package com.kbplus.dynamic.demo.entity;

import java.io.Serializable;

/**
 * (DbInfo)实体类
 *
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
public class DbInfo implements Serializable {
    private static final long serialVersionUID = 407071134503904995L;


    private Integer id;
    
    private String username;
    
    private String password;
    
    private String url;
    
    private String driverClassName;
    
    private String expandJarName;
    
    private String tenantId;
    
    private String jarPath;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getExpandJarName() {
        return expandJarName;
    }

    public void setExpandJarName(String expandJarName) {
        this.expandJarName = expandJarName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

}

