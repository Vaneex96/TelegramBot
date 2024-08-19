package com.example.node.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="voice_acting")
public class VoiceActing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="voice_acting_id")
    private long voiceActingId;

    @Column(name="voice_acting_value")
    private long voiceActingValue;

    @Column(name="voice_acting_name")
    private String voiceActingName;


}
