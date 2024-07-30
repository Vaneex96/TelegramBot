package com.example.node.service.impl;

import com.example.node.dao.AppSeriesUrlDAO;
import com.example.node.dao.AppUserDAO;
import com.example.node.dao.enums.UserState;
import com.example.node.dto.TransferDataBetweenNodeAndParserDto;
import com.example.node.entity.AppSeriesUrl;
import com.example.node.entity.AppUser;
import com.example.node.service.FollowReleaseService;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.node.dao.enums.UserState.*;

@Log4j
@RequiredArgsConstructor
@Service
public class FollowReleaseServiceImpl implements FollowReleaseService {

    private final AppUserDAO appUserDAO;
    private final ProducerService producerService;
    private final AppSeriesUrlDAO appSeriesUrlDAO;

    @Override
    public String processFollowRelease(AppUser appUser, String text, long chatId) {
        UserState userState = appUser.getState();
        String output = "Ошибка! Введите /cancel и начните заново!";

        if(READY_FOR_INPUT_TITLE_STATE.equals(userState)){
            TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto = TransferDataBetweenNodeAndParserDto.builder()
                            .title(text)
                            .chatId(chatId)
                            .userState(READY_FOR_INPUT_TITLE_STATE)
                            .build();
            output = findSeriesOnWebsite(searchingSeriesToParseDto);
            AppUser appUserTemp = appUserDAO.findByTelegramUserId(appUser.getTelegramUserId()).get();
            appUserTemp.setState(READY_FOR_INPUT_URL_STATE);
            appUserDAO.save(appUserTemp);
        } else if (READY_FOR_INPUT_URL_STATE.equals(userState)){
            output = getVoicesActing(appUser, text, chatId);
        } else if (READY_FOR_INPUT_VOICE_STATE.equals(userState)) {
            output = followRelease(appUser, text, chatId);
            return null;
        }

        return output;
    }

    public String followRelease(AppUser appUser, String voice, long chatId){
                List<AppSeriesUrl> appSeriesUrls = appUserDAO.findByEmail(appUser.getEmail())
                .orElseThrow()
                .getUrlList();
        String url = "";
        if(appSeriesUrls.size() > 0){
            url = appSeriesUrls.get(0).getUrl();
        } else {
            appUser.setState(BASIC_STATE);
            appUserDAO.save(appUser);
            return null;
        }
        TransferDataBetweenNodeAndParserDto dto = TransferDataBetweenNodeAndParserDto.builder()
                .chatId(chatId)
                .voiceActing(List.of(voice))
                .url(url)
                .userState(READY_FOR_INPUT_VOICE_STATE)
                .build();
        producerService.produceSearchingSeries(dto);
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return url;
    }



    @Override
    public String getVoicesActing(AppUser appUser, String url, long chatId) {
        TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto = TransferDataBetweenNodeAndParserDto.builder()
                .chatId(chatId)
                .url(url)
                .userState(READY_FOR_INPUT_URL_STATE)
                .build();
        producerService.produceSearchingSeries(searchingSeriesToParseDto);
        appUser.setState(READY_FOR_INPUT_VOICE_STATE);

        AppSeriesUrl appSeriesUrl = AppSeriesUrl.builder()
                .url(url)
                .build();

        appUser.addAppSeriesUrl(appSeriesUrl);
        appUserDAO.save(appUser);

        return "Идет обработка информации...";
    }

    @Override
    public String prepareUserToInputTitle(AppUser appUser) {
        appUser.setState(READY_FOR_INPUT_TITLE_STATE);
        appUserDAO.save(appUser);
        return "Введите, пожалуйста, название сериала: ";
    }

    @Override
    public String findSeriesOnWebsite(TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto){
        producerService.produceSearchingSeries(searchingSeriesToParseDto);
        return "Ваш запрос обрабатывается, пожалуйста, подождите...";
    }
}
