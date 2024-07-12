package com.example.node.service;

import com.example.node.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail(AppUser appUser, String email, long chatId);
}
