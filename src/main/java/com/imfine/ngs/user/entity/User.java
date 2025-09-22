package com.imfine.ngs.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileUrl;

    @Column
    private LocalDate birthAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private UserStatus status;

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

    public void updateProfileUrl(String newProfileUrl) {this.profileUrl = newProfileUrl;}

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void assignRole(UserRole role) { this.role = role; }
    public void assignStatus(UserStatus status) { this.status = status; }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
