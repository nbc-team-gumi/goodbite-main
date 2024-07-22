package com.sparta.goodbite.domain.review.controller;

import com.sparta.goodbite.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private ReviewService reviewService;
}