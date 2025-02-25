package com.xsh.service;

import com.xsh.entity.User;

public interface UserService {
    User authenticateUser(String userName, String password);
}
