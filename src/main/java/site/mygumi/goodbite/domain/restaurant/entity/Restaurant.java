package site.mygumi.goodbite.domain.restaurant.entity;

import site.mygumi.goodbite.common.entity.Timestamped;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import site.mygumi.goodbite.domain.restaurant.enums.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant extends Timestamped {

    public static final int DEFAULT_PAGE_SIZE = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    private String name;

    @Column(length = 2083)
    private String imageUrl;

    private String sido;
    private String sigungu;
    private String address;
    private String detailAddress;
    private String phoneNumber;
    private int capacity;
    private double rating;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder
    public Restaurant(Owner owner, String name, String imageUrl, String sido, String sigungu,
        String address, String detailAddress, String phoneNumber, Category category, int capacity) {
        this.owner = owner;
        this.name = name;
        this.imageUrl = imageUrl;
        this.sido = sido;
        this.sigungu = sigungu;
        this.address = address;
        this.detailAddress = detailAddress;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.capacity = capacity;
    }

    public void updateRating(double rating) {
        this.rating = rating;
    }

    public void update(RestaurantRequestDto restaurantRequestDto, String restaurantImage) {
        this.name = restaurantRequestDto.getName();
        this.imageUrl = restaurantImage;
        this.sido = restaurantRequestDto.getSido();
        this.sigungu = restaurantRequestDto.getSigungu();
        this.address = restaurantRequestDto.getAddress();
        this.detailAddress = restaurantRequestDto.getDetailAddress();
        this.phoneNumber = restaurantRequestDto.getPhoneNumber();
        this.category = restaurantRequestDto.getCategory();
        this.capacity = restaurantRequestDto.getCapacity();
    }
}