package com.sparta.goodbite.domain.restaurant.entity;

import com.sparta.goodbite.common.Timestamped;
import com.sparta.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerId;
    private String name;
    private String imageUrl;
    private String address;
    private String area;
    private String phoneNumber;
    private String category;

    @Builder
    public Restaurant(Long ownerId, String name, String imageUrl, String address, String area,
        String phoneNumber, String category) {
        this.ownerId = ownerId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.area = area;
        this.phoneNumber = phoneNumber;
        this.category = category;
    }

    public void update(RestaurantRequestDto restaurantRequestDto) {
        this.name = restaurantRequestDto.getName();
        this.imageUrl = restaurantRequestDto.getImageUrl();
        this.address = restaurantRequestDto.getAddress();
        this.area = restaurantRequestDto.getArea();
        this.phoneNumber = restaurantRequestDto.getPhoneNumber();
        this.category = restaurantRequestDto.getCategory();
    }
}
