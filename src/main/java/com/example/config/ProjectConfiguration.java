package com.example.config;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.example.services", "com.example.repositories", "com.example.aspects"})
public class ProjectConfiguration {

    @PostConstruct
    private void postConstruct() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/USER/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");
    }

    @Bean
    public ChromeDriver driver() {
        return new ChromeDriver();
    }

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource("jdbc:mysql://localhost:3306/sheets", "root", "hx3bnm00ub!R.pie3.14159265");
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager (DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

