package site.mygumi.goodbite.domain.review.exception.detail;

import site.mygumi.goodbite.domain.review.exception.ReviewErrorCode;
import site.mygumi.goodbite.domain.review.exception.ReviewException;

public class ReviewNotFoundException extends ReviewException {

    public ReviewNotFoundException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }
}