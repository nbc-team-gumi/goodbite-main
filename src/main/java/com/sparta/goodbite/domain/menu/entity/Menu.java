package com.sparta.goodbite.domain.menu.entity;

import com.sparta.goodbite.common.Timestamped;
import com.sparta.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer price;
    private String name;
    private String description;
//    private String imageUrl;

    @Builder
    public Menu(int price, String name, String description) {
        this.price = price;
        this.name = name;
        this.description = description;
    }

    public void update(UpdateMenuRequestDto updateMenuRequestDto) {
        this.price =
            updateMenuRequestDto.getPrice() != null ? updateMenuRequestDto.getPrice() : this.price;
        this.name =
            updateMenuRequestDto.getName() != null ? updateMenuRequestDto.getName()
                : this.getName();
        this.description =
            updateMenuRequestDto.getDescription() != null ? updateMenuRequestDto.getDescription()
                : this.description;
    }
}