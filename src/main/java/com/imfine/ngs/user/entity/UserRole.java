package com.imfine.ngs.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

   @Lob
   @Column(nullable = false)
    private String description;
}
