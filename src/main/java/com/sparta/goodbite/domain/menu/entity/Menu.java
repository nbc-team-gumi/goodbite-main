package com.sparta.goodbite.domain.menu.entity;

import com.sparta.goodbite.common.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Menu extends Timestamped {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "restaurant_id", nullable = false)
//    Restaurant restaurant;

    @Id
    private Long id;

    private int price;
    private String name;
    private String description;
//    private String imageUrl;

    @Builder
    public Menu(int price, String name, String description) {
        this.price = price;
        this.name = name;
        this.description = description;
    }
}