package cn.com.geovis.datamigration.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;


/**
 * @Description: springboot集成mybatis的基本入口 1）创建数据源 2）创建SqlSessionFactory
 */
@Configuration // 该注解类似于spring配置文件
@MapperScan(basePackages = "cn.com.geovis.datamigration.dao") // 扫描Dao文件
@EnableTransactionManagement //支持事务注解
public class MyBatisConfig implements TransactionManagementConfigurer {

    @Autowired
    DataSource dataSource;//默认配置文件中的数据源

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(dataSource);// 指定数据源(这个必须有，否则报错)
        return fb.getObject();
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return null;
    }
}
