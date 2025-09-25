package com.imfine.ngs.game.validation;

import com.imfine.ngs.game.dto.request.GameSearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 가격 범위 검증 구현 클래스.
 * GameSearchRequest의 minPrice와 maxPrice를 검증합니다.
 *
 * @author chan
 */
public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, GameSearchRequest> {

    @Override
    public void initialize(ValidPriceRange constraintAnnotation) {
        // 초기화 로직이 필요한 경우 여기에 구현
    }

    @Override
    public boolean isValid(GameSearchRequest request, ConstraintValidatorContext context) {
        // null 체크 - 둘 다 null이면 유효
        if (request == null || request.getMinPrice() == null || request.getMaxPrice() == null) {
            return true;
        }

        // minPrice가 maxPrice보다 크면 유효하지 않음
        if (request.getMinPrice() > request.getMaxPrice()) {
            // 커스텀 에러 메시지 설정
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("최소 가격(%d)은 최대 가격(%d)보다 클 수 없습니다",
                                request.getMinPrice(), request.getMaxPrice())
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}