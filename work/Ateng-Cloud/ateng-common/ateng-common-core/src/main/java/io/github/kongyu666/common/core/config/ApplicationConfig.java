package io.github.kongyu666.common.core.config;

import io.github.kongyu666.common.core.factory.YmlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 程序注解配置
 *
 * @author Ateng
 * @email 2385569970@qq.com
 * @since 2025-04-21
 */
@EnableAsync(proxyTargetClass = true)
@AutoConfiguration
@PropertySource(value = "classpath:common-spring.yml", factory = YmlPropertySourceFactory.class)
public class ApplicationConfig {

}
