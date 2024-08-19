package com.example.node.dao;

import com.example.node.entity.AppSeriesUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppSeriesUrlDAO extends JpaRepository<AppSeriesUrl, Long> {
    Optional<List<AppSeriesUrl>> findByUrl(String url);
    Optional<List<AppSeriesUrl>> findByVoiceActingValue(int voiceActingValue);
    Optional<AppSeriesUrl> findByVoiceActingName(String voiceActingName);
    Optional<AppSeriesUrl> findByUrlAndVoiceActingName(String url, String voiceActingName);
}
