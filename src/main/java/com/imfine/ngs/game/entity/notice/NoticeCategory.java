package com.imfine.ngs.game.entity.notice;

import com.imfine.ngs.game.enums.NoticeType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임 공지사항({@link GameNotice})의 카테고리 엔티티 클래스.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NoticeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType; // 공지 타입 (enum)

    private String name; // 카테고리명 (예: 업데이트, 이벤트, 점검)

    private String description; // 카테고리 설명

    @OneToMany(mappedBy = "category")
    private List<GameNotice> notices = new ArrayList<>();
}
