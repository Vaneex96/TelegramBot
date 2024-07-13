package com.example.node.service.impl;

import com.example.node.dao.AppUserDAO;
import com.example.node.dao.enums.UserState;
import com.example.node.dto.SearchingSeriesToParseDto;
import com.example.node.entity.AppUser;
import com.example.node.service.FollowReleaseService;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class FollowReleaseServiceImpl implements FollowReleaseService {

    private final AppUserDAO appUserDAO;
    private final ProducerService producerService;


    @Override
    public void followRelease(String url) {

    }

    @Override
    public String prepareUserToInputTitle(AppUser appUser) {
        appUser.setState(UserState.READY_FOR_INPUT_TITLE);
        appUserDAO.save(appUser);
        return "Введите, пожалуйста, название сериала: ";
    }

    @Override
    public String findSeriesOnWebsite(SearchingSeriesToParseDto searchingSeriesToParseDto){
        producerService.produceSearchingSeries(searchingSeriesToParseDto);
        return "Ваш запрос обрабатывается, пожалуйста, подождите...";
    }
}
