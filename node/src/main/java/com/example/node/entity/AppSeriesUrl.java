package com.example.node.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_series_url")
@Entity
public class AppSeriesUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long urlId;

    private String url;

    private String voiceActingName;

    private int voiceActingValue;

    private int lastSeason;

    private int lastEpisode;


    @Builder.Default
    @ManyToMany(mappedBy = "urlList", fetch = FetchType.EAGER)
    private List<AppUser> appUsers = new ArrayList<>();
}
