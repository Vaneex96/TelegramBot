package com.example.node.service.impl;

import com.example.node.dao.AppSeriesUrlDAO;
import com.example.node.dao.AppUserDAO;
import com.example.node.dao.enums.UserState;
import com.example.node.dto.AppSeriesUrlDto;
import com.example.node.dto.TransferDataBetweenNodeAndParserDto;
import com.example.node.entity.AppSeriesUrl;
import com.example.node.entity.AppUser;
import com.example.node.service.FollowReleaseService;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        }

        return output;
    }

    public String followRelease(AppUser appUser, String urlIdAndVoice, long chatId){
        long urlId = Long.parseLong(urlIdAndVoice.substring(0, urlIdAndVoice.indexOf("/")));
        String voice = urlIdAndVoice.substring(urlIdAndVoice.indexOf("/") + 1);
        AppSeriesUrl url = appSeriesUrlDAO.findById(urlId).orElseThrow();

        TransferDataBetweenNodeAndParserDto dto = TransferDataBetweenNodeAndParserDto.builder()
                .chatId(chatId)
                .telegramUserId(appUser.getTelegramUserId())
                .voiceActing(List.of(voice))
                .url(url.getUrl())
                .userState(READY_FOR_INPUT_VOICE_STATE)
                .urlSeriesId(urlId)
                .build();
        producerService.produceSearchingSeries(dto);

        return "Идет обработка информации...";
    }


    @Override
    public String getVoicesActing(AppUser appUser, String url, long chatId) {

        AppSeriesUrl transientAppSeriesUrl = AppSeriesUrl.builder()
                .url(url)
                .build();

        AppSeriesUrl persistentAppSeriesUrl = appSeriesUrlDAO.save(transientAppSeriesUrl);

        appUser.addAppSeriesUrl(persistentAppSeriesUrl);
        appUser.setState(READY_FOR_INPUT_VOICE_STATE);
        appUserDAO.save(appUser);

        TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto = TransferDataBetweenNodeAndParserDto.builder()
                .chatId(chatId)
                .url(url)
                .userState(READY_FOR_INPUT_URL_STATE)
                .urlSeriesId(persistentAppSeriesUrl.getUrlId())
                .build();
        producerService.produceSearchingSeries(searchingSeriesToParseDto);

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

    @Override
    public void sendUrlsForCheckReleaseSeries() {
        List<AppSeriesUrlDto> appSeriesUrlDtoList = new ArrayList<>();
        appSeriesUrlDAO.findAll().forEach(item -> {
            System.out.println("url: " + item.getUrl());
            AppSeriesUrlDto appSeriesUrlDto = AppSeriesUrlDto.builder()
                    .id(item.getUrlId())
                    .url(item.getUrl())
                    .lastSeason(item.getLastSeason())
                    .lastEpisode(item.getLastEpisode())
                    .voiceActingName(item.getVoiceActingName())
                    .build();
            appSeriesUrlDtoList.add(appSeriesUrlDto);
        });

        if(appSeriesUrlDtoList.size() == 0) return;

        TransferDataBetweenNodeAndParserDto dto = TransferDataBetweenNodeAndParserDto.builder()
                .chatId(-777)
                .userState(BASIC_STATE)
                .appSeriesUrlDtoList(appSeriesUrlDtoList)
                .build();

        producerService.produceSearchingSeries(dto);
    }

    @Override
    public List<AppUser> findAllFollowedUsers(long id) {
        System.out.println("findByUrl: " + id);
        AppSeriesUrl appSeriesUrl = appSeriesUrlDAO.findById(id).orElseThrow();
        return appSeriesUrl.getAppUsers();
    }

    @Override
    public void updateAppSeriesUrl(AppSeriesUrlDto appSeriesUrlDto) {
        AppSeriesUrl appSeriesUrl = appSeriesUrlDAO.findById(appSeriesUrlDto.getId()).get();
        appSeriesUrl.setUrl(appSeriesUrlDto.getNewUrl());
        appSeriesUrl.setLastSeason(appSeriesUrlDto.getLastSeason());
        appSeriesUrl.setLastEpisode(appSeriesUrlDto.getLastEpisode());
        appSeriesUrlDAO.save(appSeriesUrl);
    }
}
