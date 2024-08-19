package com.example.node.dao;

import com.example.node.entity.VoiceActing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoiceActingDAO extends JpaRepository<VoiceActing, Long> {
    Optional<VoiceActing> findByVoiceActingValue(long voiceActingValue);
}
