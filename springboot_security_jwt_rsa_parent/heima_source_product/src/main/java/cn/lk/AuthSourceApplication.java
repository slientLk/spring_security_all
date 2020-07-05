package cn.lk;

import cn.lk.config.RsaKeyProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("cn.lk.mapper")
@EnableConfigurationProperties(RsaKeyProperties.class)
public class AuthSourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthSourceApplication.class, args);
    }
}
