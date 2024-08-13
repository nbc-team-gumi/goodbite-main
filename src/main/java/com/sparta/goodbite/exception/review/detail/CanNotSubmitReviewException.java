package com.sparta.goodbite.exception.review.detail;

import com.sparta.goodbite.exception.review.ReviewErrorCode;
import com.sparta.goodbite.exception.review.ReviewException;

public class CanNotSubmitReviewException extends ReviewException {

    public CanNotSubmitReviewException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }
}