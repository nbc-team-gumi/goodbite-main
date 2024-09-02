package site.mygumi.goodbite.exception.review.detail;

import site.mygumi.goodbite.exception.review.ReviewErrorCode;
import site.mygumi.goodbite.exception.review.ReviewException;

public class ReviewNotFoundException extends ReviewException {

    public ReviewNotFoundException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }
}