package com.xsh.service;

import com.xsh.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Value("${spring.security.user.userName}")
    private String adminUserName;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    @Override
    public User authenticateUser(String userName, String password) {
        // 简单的用户认证逻辑，仅支持admin用户
        if (adminUserName.equals(userName) && adminPassword.equals(password)) {
            User user = new User();
            user.setUserName(userName);
            // 不返回密码以提高安全性
            user.setRole("ADMIN");
            return user;
        }
        return null;
    }
}
