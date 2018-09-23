package com.beautyboss.slogen.simplejobserver.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Author : Slogen
 * Date   : 2017/12/14
 */
@Configuration
public class DataSourceConfig {
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUserName;
    @Value("${db.password}")
    private String dbPassword;
    @Value("${db.maxactive}")
    private int dbMaxActive;
    @Value("${db.minidle}")
    private int dbMinIdle;

    @Bean("dataSource")
    @Primary
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setInitialSize(dbMinIdle);
        dataSource.setMaxActive(dbMaxActive);
        dataSource.setMinIdle(dbMinIdle);

        dataSource.setTimeBetweenEvictionRunsMillis(30000);

        dataSource.setMinEvictableIdleTimeMillis(30000);

        dataSource.setValidationQuery("select 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        return dataSource;

    }

}
