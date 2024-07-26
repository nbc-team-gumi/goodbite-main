package com.sparta.goodbite.exception.review.detail;

import com.sparta.goodbite.exception.review.ReviewErrorCode;
import com.sparta.goodbite.exception.review.ReviewException;

public class ReviewNotFoundException extends ReviewException {

    public ReviewNotFoundException(ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }
}