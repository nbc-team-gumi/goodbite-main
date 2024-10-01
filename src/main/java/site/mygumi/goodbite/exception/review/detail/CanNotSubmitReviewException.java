package site.mygumi.goodbite.exception.review.detail;

import site.mygumi.goodbite.exception.review.ReviewErrorCode;
import site.mygumi.goodbite.exception.review.ReviewException;

public class CanNotSubmitReviewException extends ReviewException {

    public CanNotSubmitReviewException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }
}