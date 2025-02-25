package com.xsh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {
    
    @Value("${data.storage.path}")
    private String dataStoragePath;
    
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (context != null && context.isActive()) {
                context.close();
            }
        }));
    }

    @PostConstruct
    public void init() {
        // 创建数据存储目录
        File dataDir = new File(dataStoragePath);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                System.out.println("Created data directory: " + dataStoragePath);
            } else {
                System.err.println("Failed to create data directory: " + dataStoragePath);
            }
        }
    }

    @PreDestroy
    public void onExit() {
        System.out.println("应用程序正在关闭，正在保存数据...");
    }
}
