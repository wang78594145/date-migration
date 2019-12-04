package cn.com.geovis.datamigration;



import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@EnableScheduling

@EnableAsync

@ServletComponentScan
@SpringBootApplication

@EnableSwagger2

@EnableTransactionManagement
@MapperScan("cn.com.geovis.datamigration.mapper")
@Slf4j
public class DataMigrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataMigrationApplication.class, args);
    }
}
