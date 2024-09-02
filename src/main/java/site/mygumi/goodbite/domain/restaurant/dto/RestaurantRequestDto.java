package site.mygumi.goodbite.domain.restaurant.dto;

import site.mygumi.goodbite.domain.owner.entity.Owner;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.enums.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class RestaurantRequestDto {

    @NotBlank(message = "식당 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "식당의 시/도를 입력해 주세요.")
    private String sido;

    @NotBlank(message = "식당의 시/군/구를 입력해 주세요.")
    private String sigungu;

    @NotBlank(message = "식당 주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "식당 상세 주소를 입력해주세요.")
    private String detailAddress;

    @NotBlank(message = "식당 전화번호를 입력해주세요.")
    @Pattern(regexp = "^(0[2-8][0-5]?|01[01346-9]|050[2-9]?)-?([1-9]{1}[0-9]{2,3})-?([0-9]{4})$", message = "전화번호 형식에 맞게 입력해주세요.")
    private String phoneNumber;

    @NotNull(message = "카테고리를 입력해주세요.")
    private Category category;

    @Min(value = 1, message = "최대 수용 인원은 0명 이하일 수 없습니다.")
    @NotNull(message = "최대 수용 인원을 입력해주세요.")
    private Integer capacity;

    public Restaurant toEntity(Owner owner, String image) {
        return Restaurant.builder()
            .owner(owner)
            .name(name)
            .imageUrl(image)
            .sido(sido)
            .sigungu(sigungu)
            .address(address)
            .detailAddress(detailAddress)
            .phoneNumber(phoneNumber)
            .category(category)
            .capacity(capacity)
            .build();
    }
}