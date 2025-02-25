package com.xsh.controller;

import com.xsh.entity.AuthResponse;
import com.xsh.entity.CommonResponse;
import com.xsh.entity.User;
import com.xsh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public CommonResponse<?> login(@RequestBody User user) {
        User authenticatedUser = userService.authenticateUser(user.getUserName(), user.getPassword());
        if (authenticatedUser != null) {
            // 生成简单的token（实际应用中应使用更安全的token生成方式）
            String token = java.util.UUID.randomUUID().toString();
            AuthResponse authResponse = new AuthResponse(token, authenticatedUser);
            return CommonResponse.success("登录成功", authResponse);
        }
        return CommonResponse.error("用户名或密码错误");
    }
    
    @GetMapping("/currentUser")
    public CommonResponse<?> getCurrentUser() {
        // 这里可以从SecurityContext中获取当前用户信息
        return CommonResponse.success("成功获取当前用户", null);
    }

    @PostMapping("/logout")
    public CommonResponse<?> logout() {
        return CommonResponse.success("退出成功", null);
    }
}
