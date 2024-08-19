package com.example.node.entity;

import com.example.node.dao.enums.UserState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private LocalDateTime firstLoginDate;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserState state;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="app_user_series_url",
            joinColumns = @JoinColumn(name="app_user_id"),
            inverseJoinColumns = @JoinColumn(name="url_id")
    )
    private List<AppSeriesUrl> urlList = new ArrayList<>();

    public void addAppSeriesUrl(AppSeriesUrl appSeriesUrl){
        urlList.add(appSeriesUrl);
        appSeriesUrl.getAppUsers().add(this);
    }

    public boolean removeAppSeriesUrlByUrlId(long urlId){
        for(int i = 0; i < this.urlList.size(); i++){
            if(this.urlList.get(i).getUrlId() == urlId){
                this.urlList.remove(i);
                return true;
            }
        }
        return false;
    }

}
