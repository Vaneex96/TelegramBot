package com.example.node.dao;

import com.example.node.entity.AppSeriesUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppSeriesUrlDAO extends JpaRepository<AppSeriesUrl, Long> {
    Optional<AppSeriesUrl> findAppSeriesUrlByUrl(String url);
}
