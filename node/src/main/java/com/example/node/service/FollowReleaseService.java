package com.example.node.service;

import com.example.node.dto.AppSeriesUrlDto;
import com.example.node.dto.TransferDataBetweenNodeAndParserDto;
import com.example.node.entity.AppUser;

import java.util.List;

public interface FollowReleaseService {
    String processFollowRelease(AppUser appUser, String text, long chatId);

    String getVoicesActing(AppUser appUser, String url, long chatId);
    String prepareUserToInputTitle(AppUser appUser);

    String findSeriesOnWebsite(TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto);

    void sendUrlsForCheckReleaseSeries();

    List<AppUser> findAllFollowedUsers(long id);

    void updateAppSeriesUrl(AppSeriesUrlDto appSeriesUrlDto);
}
