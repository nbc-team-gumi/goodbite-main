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
    private String picture;
    private String address;
    private String area;
    private String telno;
    private String category;

    @Builder
    public Restaurant(Long ownerId, String name, String picture, String address, String area,
        String telno, String category) {
        this.ownerId = ownerId;
        this.name = name;
        this.picture = picture;
        this.address = address;
        this.area = area;
        this.telno = telno;
        this.category = category;
    }

    public void update(RestaurantRequestDto restaurantRequestDto) {
        this.name = restaurantRequestDto.getName();
        this.picture = restaurantRequestDto.getPicture();
        this.address = restaurantRequestDto.getAddress();
        this.area = restaurantRequestDto.getArea();
        this.telno = restaurantRequestDto.getTelno();
        this.category = restaurantRequestDto.getCategory();
    }
}
