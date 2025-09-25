package com.imfine.ngs.game.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 가격 범위 검증 어노테이션.
 * minPrice와 maxPrice의 유효성을 검증합니다.
 *
 * @author chan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceRangeValidator.class)
@Documented
public @interface ValidPriceRange {

    String message() default "최소 가격은 최대 가격보다 클 수 없습니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}