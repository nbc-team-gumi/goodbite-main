package site.mygumi.goodbite.domain.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import site.mygumi.goodbite.auth.exception.AuthErrorCode;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.common.external.s3.service.S3Service;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.enums.Category;
import site.mygumi.goodbite.domain.restaurant.exception.RestaurantErrorCode;
import site.mygumi.goodbite.domain.restaurant.exception.detail.RestaurantCreateFailedException;
import site.mygumi.goodbite.domain.restaurant.exception.detail.RestaurantUpdateFailedException;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    private static final Long RESTAURANT_ID = 1L;

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private S3Service s3Service;

    private UserCredentials user;
    private Owner owner;
    private Restaurant restaurant;

    @BeforeEach
    void setup() {
        user = mock(UserCredentials.class);
        owner = mock(Owner.class);

        restaurant = Restaurant.builder()
            .owner(owner)
            .name("가게1")
            .imageUrl("https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg")
            .sido("서울특별시")
            .sigungu("송파구")
            .address("잠실동")
            .detailAddress("1동")
            .phoneNumber("010-5465-1234")
            .category(Category.KOREAN)
            .capacity(30)
            .build();
        ReflectionTestUtils.setField(restaurant, "id", RESTAURANT_ID);
    }

    private RestaurantRequestDto createRestaurantRequestDto() {
        RestaurantRequestDto restaurantRequestDto = new RestaurantRequestDto();
        ReflectionTestUtils.setField(restaurantRequestDto, "name", "가게1");
        ReflectionTestUtils.setField(restaurantRequestDto, "sido", "서울특별시");
        ReflectionTestUtils.setField(restaurantRequestDto, "sigungu", "송파구");
        ReflectionTestUtils.setField(restaurantRequestDto, "address", "잠실동");
        ReflectionTestUtils.setField(restaurantRequestDto, "detailAddress", "1동");
        ReflectionTestUtils.setField(restaurantRequestDto, "phoneNumber", "010-5465-1234");
        ReflectionTestUtils.setField(restaurantRequestDto, "category", Category.KOREAN);
        ReflectionTestUtils.setField(restaurantRequestDto, "capacity", 30);
        return restaurantRequestDto;
    }

    @Nested
    @DisplayName("레스토랑 생성")
    class CreateRestaurant {

        @Test
        @DisplayName("레스토랑 생성 성공")
        void createRestaurantSuccess() {

            //given
            RestaurantRequestDto restaurantRequestDto = createRestaurantRequestDto();

            MultipartFile image = mock(MultipartFile.class);
            String restaurantImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg";

            ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);

            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(s3Service.upload(image)).willReturn(restaurantImage);

            //when
            restaurantService.createRestaurant(restaurantRequestDto, user, image);

            //then
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(s3Service).should(times(1)).upload(image);
            then(restaurantRepository).should(times(1)).save(restaurantCaptor.capture());
            then(s3Service).should(never()).deleteImageFromS3(restaurantImage);

            Restaurant savedRestaurant = restaurantCaptor.getValue();
            assertThat(savedRestaurant.getName()).isEqualTo("가게1");
            assertThat(savedRestaurant.getSido()).isEqualTo("서울특별시");
            assertThat(savedRestaurant.getCapacity()).isEqualTo(30);
        }

        @Test
        @DisplayName("레스토랑 생성 실패")
        void createRestaurantFail() {
            //given
            RestaurantRequestDto restaurantRequestDto = createRestaurantRequestDto();
            MultipartFile image = mock(MultipartFile.class);

            String restaurantImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg";

            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(s3Service.upload(image)).willReturn(restaurantImage);
            given(restaurantRepository.save(restaurant)).willThrow(
                new RestaurantCreateFailedException(RestaurantErrorCode.RESTAURANT_CREATE_FAILED));

            //when-then
            assertThatThrownBy(
                () -> restaurantService.createRestaurant(restaurantRequestDto, user, image))
                .isInstanceOf(RestaurantCreateFailedException.class)
                .hasMessage(RestaurantErrorCode.RESTAURANT_CREATE_FAILED.getMessage());
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(s3Service).should(times(1)).upload(image);
            then(s3Service).should(times(1)).deleteImageFromS3(restaurantImage);
        }
    }

    @Nested
    @DisplayName("레스토랑 조회")
    class GetRestaurant {

        @Test
        @DisplayName("특정 레스토랑 조회 성공")
        void getRestaurantSuccess() {
            //given
            RestaurantResponseDto expectedResponse = RestaurantResponseDto.from(restaurant);

            given(restaurantRepository.findByIdOrThrow(RESTAURANT_ID)).willReturn(restaurant);

            //when
            RestaurantResponseDto restaurantResponseDto = restaurantService.getRestaurant(
                RESTAURANT_ID);

            //then
            assertThat(restaurantResponseDto).isEqualTo(expectedResponse);
            then(restaurantRepository).should(times(1)).findByIdOrThrow(RESTAURANT_ID);
        }
    }

    @Nested
    @DisplayName("레스토랑 수정")
    class UpdateRestaurant {

        @Test
        @DisplayName("레스토랑 수정 성공")
        void updateRestaurantSuccess() {
            //given
            RestaurantRequestDto restaurantRequestDto = createRestaurantRequestDto();

            MultipartFile image = mock(MultipartFile.class);

            String originalImage = restaurant.getImageUrl();
            String newImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/123456.jpg";

            given(restaurantRepository.findByIdOrThrow(RESTAURANT_ID)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(s3Service.upload(image)).willReturn(newImage);

            //when
            restaurantService.updateRestaurant(RESTAURANT_ID, restaurantRequestDto, user, image);

            //then
            then(restaurantRepository).should(times(1)).findByIdOrThrow(RESTAURANT_ID);
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(s3Service).should(times(1)).upload(image);
            then(s3Service).should(times(1)).deleteImageFromS3(originalImage);

            assertThat(restaurant.getName()).isEqualTo("가게1");
            assertThat(restaurant.getSigungu()).isEqualTo("송파구");
            assertThat(restaurant.getCategory()).isEqualTo(Category.KOREAN);
            assertThat(restaurant.getPhoneNumber()).isEqualTo("010-5465-1234");
        }

        @Test
        @DisplayName("레스토랑 수정 실패")
        void updateRestaurantFail() {

            //given
            RestaurantRequestDto restaurantRequestDto = createRestaurantRequestDto();
            MultipartFile image = mock(MultipartFile.class);
            Restaurant restaurant = mock(Restaurant.class);

            String originalImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg";
            String newImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/123456.jpg";

            given(restaurantRepository.findByIdOrThrow(RESTAURANT_ID)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(restaurant.getOwner()).willReturn(owner);
            given(image.isEmpty()).willReturn(false);
            given(restaurant.getImageUrl()).willReturn(originalImage);
            given(s3Service.upload(image)).willReturn(newImage);
            doThrow(
                new RestaurantUpdateFailedException(RestaurantErrorCode.RESTAURANT_UPDATE_FAILED))
                .when(restaurant).update(restaurantRequestDto, newImage);

            //when-then
            assertThatThrownBy(
                () -> restaurantService.updateRestaurant(RESTAURANT_ID, restaurantRequestDto, user,
                    image))
                .isInstanceOf(RestaurantUpdateFailedException.class)
                .hasMessage(RestaurantErrorCode.RESTAURANT_UPDATE_FAILED.getMessage());
            then(restaurantRepository).should(times(1)).findByIdOrThrow(RESTAURANT_ID);
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(s3Service).should(times(1)).upload(image);
            then(s3Service).should(times(1)).deleteImageFromS3(newImage);
            then(restaurant).should(times(1)).update(restaurantRequestDto, newImage);
        }
    }

    @Nested
    @DisplayName("레스토랑 삭제")
    class DeleteRestaurant {

        @Test
        @DisplayName("레스토랑 삭제 성공")
        void deleteRestaurantSuccess() {
            //given
            given(restaurantRepository.findByIdOrThrow(RESTAURANT_ID)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);

            //when
            restaurantService.deleteRestaurant(RESTAURANT_ID, user);

            //then
            then(restaurantRepository).should(times(1)).findByIdOrThrow(RESTAURANT_ID);
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(restaurantRepository).should(times(1)).delete(restaurant);
            then(s3Service).should(times(1)).deleteImageFromS3(
                "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg");
        }

        @Test
        @DisplayName("레스토랑 삭제 실패 - 해당 레스토랑에 대한 권한이 없을 경우")
        void deleteRestaurantFail() {
            //given
            Owner otherOwner = mock(Owner.class);

            given(restaurantRepository.findByIdOrThrow(RESTAURANT_ID)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(otherOwner);
            given(owner.getId()).willReturn(1L);
            given(otherOwner.getId()).willReturn(2L);

            //when-then
            assertThatThrownBy(() -> restaurantService.deleteRestaurant(RESTAURANT_ID, user))
                .isInstanceOf(AuthException.class)
                .hasMessage(AuthErrorCode.UNAUTHORIZED.getMessage());
            then(restaurantRepository).should(never()).delete(restaurant);
            then(s3Service).should(never()).deleteImageFromS3(anyString());
        }
    }
}