package site.mygumi.goodbite.domain.review.exception;

import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

    private final ReviewErrorCode reviewErrorCode;

    public ReviewException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode.getMessage());
        this.reviewErrorCode = reviewErrorCode;
    }
}