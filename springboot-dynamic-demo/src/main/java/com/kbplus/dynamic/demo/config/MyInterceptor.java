package com.kbplus.dynamic.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.kbplus.dynamic.demo.entity.DbInfo;
import com.kbplus.dynamic.demo.service.DbInfoService;
import com.kbplus.dynamic.demo.utils.SpringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Component
public class MyInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String path = request.getServletPath();
        if (path.contains("test")) {
            System.out.println("requestUrl: {}"+path);
            // 进行前置处理
            if(DynamicDataSource.dataSourcesMap.get("dbkey")==null) {
                DbInfoService dbInfoService = SpringUtils.getBean(DbInfoService.class);
                DbInfo test = dbInfoService.getByTenantId("test2");

                DruidDataSource druidDataSource = new DruidDataSource();
                druidDataSource.setUrl(test.getUrl());
                druidDataSource.setUsername(test.getUsername());
                druidDataSource.setPassword(test.getPassword());
                druidDataSource.setDriverClassName(test.getDriverClassName());
                DynamicDataSource.dataSourcesMap.put("dbkey", druidDataSource);
            }
            DynamicDataSource.setDataSource("dbkey");

            return true;
            // 或者 return false; 禁用某些请求
        } else {
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex){
        String path = request.getServletPath();
        if (path.contains("test")) {
            DynamicDataSource.clear();
        }
    }
}
