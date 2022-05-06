package com.kbplus.dynamic.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
/**
 * @author kbplus
 * @date 2022-03-28
 * @blog https://blog.csdn.net/cyy9487
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource defaultDataSource() {
        return new DruidDataSource();
    }

    @Bean
    @Primary
    @DependsOn({"springUtils", "defaultDataSource"})
    public DynamicDataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(DynamicDataSource.dataSourcesMap);
        return dynamicDataSource;
    }
}