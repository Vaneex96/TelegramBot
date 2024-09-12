package com.example.node.service;

import com.example.node.dto.AppSeriesUrlDto;
import com.example.node.dto.FindSeriesVoiceActsResultDto;
import com.example.node.entity.AppUser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public interface FollowReleaseService {
    SendMessage processFollowRelease(AppUser appUser, String text, long chatId);

    SendMessage getVoicesActing(AppUser appUser, String url, long chatId);
    String prepareUserToInputTitle(AppUser appUser);

    String findSeriesOnWebsite(AppUser appUser, String text, long chatId);

    void sendUrlsForCheckReleaseSeries();

    List<AppUser> findAllFollowedUsers(long id);

    void updateAppSeriesUrl(AppSeriesUrlDto appSeriesUrlDto);

    SendMessage createSearchedSeriesVoiceButtonsAnswer(FindSeriesVoiceActsResultDto findSeriesVoiceActsResultDto);
}
