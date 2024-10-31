package site.mygumi.goodbite.domain.review.exception.detail;

import site.mygumi.goodbite.domain.review.exception.ReviewErrorCode;
import site.mygumi.goodbite.domain.review.exception.ReviewException;

public class CanNotSubmitReviewException extends ReviewException {

    public CanNotSubmitReviewException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }
}