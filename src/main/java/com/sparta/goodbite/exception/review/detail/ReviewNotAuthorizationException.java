package com.sparta.goodbite.exception.review.detail;

import com.sparta.goodbite.exception.review.ReviewErrorCode;
import com.sparta.goodbite.exception.review.ReviewException;

public class ReviewNotAuthorizationException extends ReviewException {

    public ReviewNotAuthorizationException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }
}
