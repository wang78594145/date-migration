package cn.com.geovis.datamigration.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidConfiguration {

    @ConditionalOnClass(DruidDataSource.class)
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource", matchIfMissing = true)
    static class Druid extends DruidConfiguration {

        @Value("${spring.datasource.url}")
        private String dbUrl;
        @Value("${spring.datasource.username}")
        private String username;
        @Value("${spring.datasource.password}")
        private String password;
        @Value("${spring.datasource.driver-class-name}")
        private String driverClassName;


        @Bean("dataSource")
        @ConfigurationProperties("spring.datasource.druid")
        public DruidDataSource dataSource(DataSourceProperties properties) {
            DruidDataSource datasource = new DruidDataSource();
            datasource.setUrl(this.dbUrl);
            datasource.setUsername(username);
            datasource.setPassword(password);
            datasource.setDriverClassName(driverClassName);
            return datasource;
        }
    }
}
