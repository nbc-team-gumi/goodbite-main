package com.sparta.goodbite.domain.review.dto;

import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.review.dto.validation.RatingConstraint;
import com.sparta.goodbite.domain.review.entity.Review;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateReviewRequestDto {

    @NotNull(message = "메뉴 ID를 입력해 주세요.")
    private Long menuId;

    @NotNull(message = "평점을 입력해 주세요.")
    @RatingConstraint
    private float rating;

    @NotNull(message = "리뷰 내용을 입력해 주세요.")
    private String content;

    public Review toEntity(Menu menu) {
        return Review.builder()
            .rating(rating)
            .content(content)
            .menu(menu)
            .build();
    }
}