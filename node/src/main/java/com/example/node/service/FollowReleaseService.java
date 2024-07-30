package com.example.node.service;

import com.example.node.dto.TransferDataBetweenNodeAndParserDto;
import com.example.node.entity.AppUser;

public interface FollowReleaseService {
    String processFollowRelease(AppUser appUser, String text, long chatId);

    String getVoicesActing(AppUser appUser, String url, long chatId);
    String prepareUserToInputTitle(AppUser appUser);

    String findSeriesOnWebsite(TransferDataBetweenNodeAndParserDto searchingSeriesToParseDto);
}
