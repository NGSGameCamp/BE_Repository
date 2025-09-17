package com.imfine.ngs.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    @Builder
    public static User create(String email, String pwd, String name, String nickname) {
        if (nickname == null || nickname.isBlank()) {
            nickname = name; // 닉네임이 없으면 name으로 기본값
        }
        return User.builder()
                .email(email)
                .pwd(pwd)
                .name(name)
                .nickname(nickname)
                .build();
    }

    public void updatePassword(String newPwd) {
        this.pwd = newPwd;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}

