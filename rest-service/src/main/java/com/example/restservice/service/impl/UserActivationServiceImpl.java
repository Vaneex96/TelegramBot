//package com.example.restservice.service.impl;
//
//import com.example.restservice.dao.AppUserDAO;
//import com.example.restservice.entity.AppUser;
//import com.example.restservice.service.UserActivationService;
//import com.example.restservice.utils.CryptoTool;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class UserActivationServiceImpl implements UserActivationService {
//
//    private final AppUserDAO appUserDAO;
//    @Value("${salt}")
//    private String salt;
//
//    @Override
//    public boolean activation(String cryptoUserId) {
//        CryptoTool cryptoTool = new CryptoTool(salt);
//        long userId = cryptoTool.idOf(cryptoUserId);
//        Optional<AppUser> res = appUserDAO.findById(userId);
//        if(res.isPresent()){
//            AppUser appUser = res.get();
//            appUser.setIsActive(true);
//            appUserDAO.save(appUser);
//            return true;
//        }
//        return false;
//    }
//}
