package com.sparta.goodbite.domain.review.entity;

import com.sparta.goodbite.common.Timestamped;
import com.sparta.goodbite.domain.menu.entity.Menu;
import com.sparta.goodbite.domain.review.dto.UpdateReviewRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float rating;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_id", nullable = false)
//    private Customer customer;

    @Builder
    public Review(float rating, String content, Menu menu) {
        this.rating = rating;
        this.content = content;
        this.menu = menu;
    }

    public void update(UpdateReviewRequestDto updateReviewRequestDto) {
        this.rating =
            updateReviewRequestDto.getRating() != null ? updateReviewRequestDto.getRating()
                : this.rating;
        this.content =
            updateReviewRequestDto.getContent() != null ? updateReviewRequestDto.getContent()
                : this.content;
    }
}