package com.example.node.service;

import com.example.node.dto.SearchingSeriesToParseDto;
import com.example.node.entity.AppUser;

public interface FollowReleaseService {
    void followRelease(String url);
    String prepareUserToInputTitle(AppUser appUser);

    String findSeriesOnWebsite(SearchingSeriesToParseDto searchingSeriesToParseDto);
}
