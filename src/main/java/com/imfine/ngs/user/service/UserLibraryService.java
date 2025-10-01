package com.imfine.ngs.user.service;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.tag.GameTag;
import com.imfine.ngs.game.entity.tag.LinkedTag;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.repository.OrderRepository;
import com.imfine.ngs.user.dto.response.UserLibraryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLibraryService {

    private final OrderRepository orderRepository;
    private final GameRepository gameRepository;

    public List<UserLibraryResponse> getUserLibrary(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        log.info("[DEBUG] 1. Total orders for userId {}: {}", userId, orders.size());

        // 대상 상태의 주문만 필터링 후, 게임 ID 집합 추출 (중복 제거)
        Set<Long> gameIds = orders.stream()
                .peek(o -> log.info("[DEBUG] 2. Order {} status: {}", o.getOrderId(), o.getStatus()))
                .filter(o -> o.getStatus() == OrderStatus.PURCHASED_CONFIRMED
                        || o.getStatus() == OrderStatus.REFUND_REQUESTED)
                .peek(o -> log.info("[DEBUG] 3. After filter - order {}", o.getOrderId()))
                .flatMap(o -> o.getOrderDetails().stream())
                .peek(od -> log.info("[DEBUG] 4. OrderDetail game: {}", od.getGame() != null ? od.getGame().getId() : "null"))
                .map(od -> od.getGame() != null ? od.getGame().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        log.info("[DEBUG] 5. Total gameIds: {}", gameIds.size());
        log.info("[DEBUG] 5-1. GameIds: {}", gameIds);

        // 활성 게임만 조회
        List<Game> games = gameIds.stream()
                .peek(id -> log.info("[DEBUG] 6-1. Searching for game ID: {} with status: {}", id, GameStatusType.ACTIVE))
                .map(id -> gameRepository.findByIdAndGameStatus(id, GameStatusType.ACTIVE))
                .peek(opt -> log.info("[DEBUG] 6-2. Game found: {}, Game: {}", opt.isPresent(), opt.orElse(null)))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        log.info("[DEBUG] 7. Final games size: {}", games.size());

        // Game → UserLibraryResponse 변환
        List<UserLibraryResponse> result = games.stream()
                .map(this::toUserLibraryResponse)
                .collect(Collectors.toList());

        log.info("[DEBUG] 8. Final result size: {}", result.size());

        return result;
    }

    /**
     * Game 엔티티를 UserLibraryResponse DTO로 변환
     */
    private UserLibraryResponse toUserLibraryResponse(Game game) {
        return UserLibraryResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .price(game.getPrice())
                .thumbnailUrl(game.getThumbnailUrl())
                .tags(extractTagNames(game.getTags()))
                .build();
    }

    /**
     * LinkedTag Set을 태그 이름 List로 변환
     */
    private List<String> extractTagNames(Set<LinkedTag> linkedTags) {
        if (linkedTags == null || linkedTags.isEmpty()) {
            return new ArrayList<>();
        }

        return linkedTags.stream()
                .filter(Objects::nonNull)
                .map(LinkedTag::getGameTag)
                .filter(Objects::nonNull)
                .map(GameTag::getTagType)
                .filter(Objects::nonNull)
                .map(GameTagType::getKoreanName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
