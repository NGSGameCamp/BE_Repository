package com.imfine.ngs.game.entity.bundle;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 게임{@link com.imfine.ngs.game.entity.Game} 번들 엔티티 클래스.
 * 스팀의 번들 할인과 같은 역할을 수행한다.
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 번들 entity id

    private String bundleName; // 번들 할인명

    private BigDecimal discountRate; // 번들 할인율 (예: 10.50%)

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BundleGameList> bundleGames = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt; //  번들 생성일
}
