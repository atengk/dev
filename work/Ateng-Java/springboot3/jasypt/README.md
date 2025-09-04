# Jasypt

JASYPT Spring Boot为Spring Boot应用程序中的属性源提供了加密支持。



## 基础配置

### 添加依赖

```xml
<!-- jasypt：配置文件参数值加密 -->
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

### 创建工具类

```java
package local.ateng.java.jasypt.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Jasypt 加密/解密工具类
 * 用于快速生成 ENC(...) 格式的密文，或解密校验。
 * <p>
 * 注意：生产环境不要把加密密钥写死在代码里，建议通过 JVM 参数或环境变量传入。
 *
 * @author 孔余
 * @since 2025-09-03
 */
public class EncryptorUtil {

    /**
     * 默认加密算法，可根据需要调整
     * PBEWITHHMACSHA512ANDAES_128 在所有 JDK 上都能跑
     * PBEWITHHMACSHA512ANDAES_256 在 JDK8u162+ / JDK21 默认支持
     */
    private static final String ALGORITHM = "PBEWITHHMACSHA512ANDAES_256";

    private static PooledPBEStringEncryptor getEncryptor(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(ALGORITHM);
        config.setKeyObtentionIterations("1000"); // 派生迭代次数
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        return encryptor;
    }

    /**
     * 加密字符串
     *
     * @param plainText 明文
     * @param password  加密密钥
     * @return 加密后的密文，格式 ENC(xxx)
     */
    public static String encrypt(String plainText, String password) {
        PooledPBEStringEncryptor encryptor = getEncryptor(password);
        return "ENC(" + encryptor.encrypt(plainText) + ")";
    }

    /**
     * 解密字符串
     *
     * @param encryptedText 加密后的字符串（可以带 ENC(...)，也可以直接是密文）
     * @param password      加密密钥
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedText, String password) {
        PooledPBEStringEncryptor encryptor = getEncryptor(password);

        String text = encryptedText;
        if (encryptedText.startsWith("ENC(") && encryptedText.endsWith(")")) {
            text = encryptedText.substring(4, encryptedText.length() - 1);
        }
        return encryptor.decrypt(text);
    }
}

```

### 使用加密解密

```java
public class Tests {

    @Test
     void test() {
        String password = "Admin@123"; // 加密密钥（建议用 JVM 参数传入）
        String plain = "root123";           // 待加密的明文

        // 加密
        String enc = EncryptorUtil.encrypt(plain, password);
        System.out.println("加密后: " + enc);

        // 解密
        String dec = EncryptorUtil.decrypt(enc, password);
        System.out.println("解密后: " + dec);
    }
    
}
```

输出：

```
加密后: ENC(XyTk3mP6ZutCcqIQMcHRVB8diUg4VENywLfPdKFBoqpDj+caJmyNlxX7UIY0E3mW)
解密后: root123
```



## 使用Jasypt

### 编辑配置文件

**配置 jasypt**

```
---
jasypt:
  encryptor:
    password: ${JASYPT_ENCRYPTOR_PASSWORD}
    algorithm: PBEWITHHMACSHA512ANDAES_256
    iv-generator-classname: org.jasypt.iv.RandomIvGenerator
```

将需要加密的配置使用 `ENC` 包裹起来，启动项目后会自动解密

```yaml
---
user:
  name: "ateng"
  password: ENC(XyTk3mP6ZutCcqIQMcHRVB8diUg4VENywLfPdKFBoqpDj+caJmyNlxX7UIY0E3mW)
```



### 添加环境变量

系统添加环境变量

```
JASYPT_ENCRYPTOR_PASSWORD=Admin@123
```



### 测试加密配置

在使用到加密的配置后会自动解密

```java
package local.ateng.java.jasypt.runner;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ApplicationRunner：实现此接口的类会在Spring Boot应用启动后执行其run方法。
 *
 * @author 孔余
 * @email 2385569970@qq.com
 * @since 2024-09-29
 */
@Component
@Slf4j
public class MyApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        log.info("加密后的配置：{}",SpringUtil.getProperty("user.password", ""));
    }
}
```

输出：

```
2025-09-04T14:18:24.468+08:00  INFO 19300 --- [jasypt] [           main] l.a.j.jasypt.runner.MyApplicationRunner  : 加密后的配置：root123
```


