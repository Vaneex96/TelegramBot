package com.example.restservice.service.impl;

import com.example.commonutils.utils.CryptoTool;
import com.example.restservice.dao.AppUserDAO;
import com.example.restservice.entity.AppUser;
import com.example.restservice.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {

    private final CryptoTool cryptoTool;
    private final AppUserDAO appUserDAO;

    @Override
    public boolean activation(String cryptoUserId) {
        long userId = cryptoTool.idOf(cryptoUserId);
        Optional<AppUser> res = appUserDAO.findById(userId);
        if(res.isPresent()){
            AppUser appUser = res.get();
            appUser.setIsActive(true);
            appUserDAO.save(appUser);
            return true;
        }
        return false;
    }
}
