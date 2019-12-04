package cn.com.geovis.datamigration.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wangqianyi
 * @Title: WebAppConfigurer
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/21 17:08
 */

@Configuration
public class WebAppConfigurer {


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("PUT", "GET", "POST", "DELETE");
            }
        };
    }

}
