package com.github.twh.redis;

import com.github.twh.redis.exception.RedisConfigFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.annotation.ElementType.FIELD;

/**
 * FIXME 不要修改这个类的字段
 *
 * @author wenhai.tan
 * @date 2021/11/23
 */
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    private static final String DEFAULT_CONFIG_FILE_PATH = "redis.conf";

    private static RedisConfig instance;

    @CfgName("bind")
    private String host = "0.0.0.0";

    @CfgName("port")
    private Integer port = 6379;

    @CfgName("databases")
    private Integer databases = 16;

    private RedisConfig() {
    }

    public static RedisConfig getConfig() {
        if (instance == null) {
            String configPath = System.getenv("CONFIG");
            if (configPath == null || configPath.isEmpty()) {
                configPath = DEFAULT_CONFIG_FILE_PATH;
            }
            if (Files.exists(Paths.get(configPath))) {
                try {
                    byte[] bytes = Files.readAllBytes(Paths.get(configPath));
                    instance = parse(bytes);
                } catch (IOException e) {
                    throw new RedisConfigFileException("读取配置文件失败", e);
                }
            } else {
                instance = new RedisConfig();
            }
        }

        return instance;
    }

    @SuppressWarnings("squid:S3011")
    private static RedisConfig parse(byte[] bytes) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8));
        try {
            String line;
            // 一行一行的读取，如果是空行或者字符串是#开头的跳过
            Map<String, Field> fieldMap = getFileds();
            RedisConfig config = new RedisConfig();
            while ((line = reader.readLine()) != null) {
                String trimLine = line.trim();
                if (trimLine.isEmpty() || trimLine.startsWith("#")) {
                    continue;
                }
                int pos = trimLine.indexOf(" ");
                if (pos == -1) {
                    throw new RedisConfigFileException("解析配置文件发生错误，错误行是 [  " + trimLine + " ]");
                }
                String key = trimLine.substring(0, pos).trim();
                String value = trimLine.substring(pos).trim();
                Field field = fieldMap.get(key);
                if (field == null) {
                    log.warn("未知的配置项 {}", key);
                    continue;
                }
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(String.class)) {
                    field.set(config, value);
                } else if (field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(int.class)) {
                    field.setInt(config, Integer.parseInt(value));
                } else if (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(long.class)) {
                    field.setLong(config, Long.parseLong(value));
                } else if (field.getType().isAssignableFrom(Short.class) || field.getType().isAssignableFrom(short.class)) {
                    field.setShort(config, Short.parseShort(value));
                } else if (field.getType().isAssignableFrom(Boolean.class) || field.getType().isAssignableFrom(boolean.class)) {
                    field.setBoolean(config, "yes".equals(value));
                } else if (field.getType().isAssignableFrom(Collection.class)) {
                    // TODO 集合
                }
            }
            return config;
        } catch (RedisConfigFileException e) {
            throw e;
        } catch (Exception e) {
            throw new RedisConfigFileException(e);
        }
    }

    private static Map<String, Field> getFileds() {
        Field[] fields = RedisConfig.class.getDeclaredFields();
        Map<String, Field> filedMap = new HashMap<>();
        for (Field field : fields) {
            CfgName cfgName = field.getAnnotation(CfgName.class);
            if (cfgName == null) {
                continue;
            }

            filedMap.put(cfgName.value(), field);
        }

        return filedMap;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getDatabases() {
        return databases;
    }

    @Target({FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface CfgName {
        String value() default "";
    }
}
