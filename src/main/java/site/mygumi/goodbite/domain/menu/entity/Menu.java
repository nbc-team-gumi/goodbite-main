package site.mygumi.goodbite.domain.menu.entity;

import site.mygumi.goodbite.common.entity.Timestamped;
import site.mygumi.goodbite.domain.menu.dto.UpdateMenuRequestDto;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    public static final int DEFAULT_PAGE_SIZE = 9;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;
    private String name;
    private String description;

    @Column(length = 2083)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Builder
    public Menu(int price, String name, String description, String imageUrl,
        Restaurant restaurant) {

        this.price = price;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.restaurant = restaurant;
    }

    public void update(UpdateMenuRequestDto updateMenuRequestDto, String menuImage) {
        this.price =
            updateMenuRequestDto.getPrice() != null ? updateMenuRequestDto.getPrice() : this.price;
        this.name =
            updateMenuRequestDto.getName() != null ? updateMenuRequestDto.getName()
                : this.getName();
        this.description =
            updateMenuRequestDto.getDescription() != null ? updateMenuRequestDto.getDescription()
                : this.description;
        this.imageUrl = menuImage;
    }
}