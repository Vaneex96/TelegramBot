package com.example.node.entity;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

    @Override
    public String toString() {
        return "AppSeriesUrl{" +
                "urlId=" + urlId +
                ", url='" + url + '\'' +
                ", voiceActingName='" + voiceActingName + '\'' +
                ", voiceActingValue=" + voiceActingValue +
                ", lastSeason=" + lastSeason +
                ", lastEpisode=" + lastEpisode +
                ", appUsers=" + appUsers +
                '}';
    }

    public boolean isVoiceActingNameNull(){
        return this.voiceActingName == null;
    }

}
