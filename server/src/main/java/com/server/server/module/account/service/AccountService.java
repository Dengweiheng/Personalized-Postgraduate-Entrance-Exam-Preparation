package com.server.server.module.account.service;

public interface AccountService {

    boolean changePassword(String username, String oldPassword, String newPassword);

    boolean logout(String username);
}
