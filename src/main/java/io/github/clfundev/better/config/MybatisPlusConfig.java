package io.github.clfundev.better.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("io.github.clfundev.better.mapper")
public class MybatisPlusConfig {
}
