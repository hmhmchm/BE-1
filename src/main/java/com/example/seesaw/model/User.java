package com.example.seesaw.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String generation;

    @Column
    private Long postCount;

    @Column(nullable = false)
    private String mbti;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(unique = true)
    private Long kakaoId;

    @OneToMany(mappedBy = "user")
    private List<UserProfileNum> userProfileNum;


    public User(String username, String enPassword, String nickname, String generation, Long postCount, String mbti, UserRoleEnum role) {
        this.username = username;
        this.pwd = enPassword;
        this.nickname = nickname;
        this.generation = generation;
        this.postCount = postCount;
        this.mbti = mbti;
        this.role = role;
        this.kakaoId = null;
    }

    public User(String username, String enPassword, Long postCount, UserRoleEnum role, Long kakaoId) {
        this.username = username;
        this.pwd = enPassword;
        this.postCount = postCount;
        this.role = role;
        this.kakaoId = kakaoId;
    }

    public User(List<UserProfileNum> userProfileNum){
        this.userProfileNum = userProfileNum;
    }
}
