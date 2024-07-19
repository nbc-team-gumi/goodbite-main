package com.sparta.goodbite.common.response;

public record DataResponseDto<T>(int statusCode, String message, T data) {

}