package com.imfine.ngs.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum SortType {

    PRICE_ASC("price", "ASC", "낮은 가격순"),
    PRICE_DESC("price", "DESC", "높은 가격순"),
    DATE_ASC("createdAt", "ASC", "오래된순"),
    DATE_DESC("createdAt", "DESC", "최신순"),
    NAME_ASC("name", "ASC", "이름 가나다순"),
    NAME_DESC("name", "DESC", "이름 역순");

    private final String field;
    private final String direction;
    private final String description;

    /**
     * 문자열로부터 SortType 찾기
     *
     * @param sortBy    정렬 필드 (price, date, name)
     * @param direction 정렬 방향 (오름차 , 내림차)
     * @return 매칭되는 SortType, 없으면 NAME_ASC
     */
    public static SortType from(String sortBy, String direction) {

        // NPE 체크
        // null 이면 NAME_ASC 사용
        if (sortBy == null || direction == null) {
            return NAME_ASC;
        }

        String normalizedDirection = direction.toUpperCase();

        for (SortType sortType : values()) {

            if (sortType.field.equalsIgnoreCase(sortBy) && sortType.direction.equals(normalizedDirection)) {
                return sortType;
            }
        }

        return NAME_ASC;
    }
}
