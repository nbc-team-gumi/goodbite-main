package site.mygumi.goodbite.domain.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import site.mygumi.goodbite.auth.exception.AuthException;
import site.mygumi.goodbite.common.external.s3.service.S3Service;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantResponseDto;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.exception.RestaurantErrorCode;
import site.mygumi.goodbite.domain.restaurant.exception.detail.RestaurantCreateFailedException;
import site.mygumi.goodbite.domain.restaurant.exception.detail.RestaurantUpdateFailedException;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private S3Service s3Service;


    @Nested
    @DisplayName("레스토랑 생성")
    class CreateRestaurant {

        @Test
        @DisplayName("레스토랑 생성 성공")
        void createRestaurantSuccess() {

            //given
            RestaurantRequestDto restaurantRequestDto = mock(RestaurantRequestDto.class);
            UserCredentials user = mock(UserCredentials.class);
            MultipartFile image = mock(MultipartFile.class);
            Owner owner = mock(Owner.class);
            Restaurant restaurant = mock(Restaurant.class);

            String restaurantImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg";

            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(s3Service.upload(image)).willReturn(restaurantImage);
            given(restaurantRequestDto.toEntity(owner, restaurantImage)).willReturn(restaurant);
            given(restaurantRepository.save(restaurant)).willReturn(restaurant);

            //when
            restaurantService.createRestaurant(restaurantRequestDto, user, image);

            //then
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(s3Service).should(times(1)).upload(image);
            then(restaurantRepository).should(times(1)).save(restaurant);
            then(s3Service).should(never()).deleteImageFromS3(restaurantImage);
        }

        @Test
        @DisplayName("레스토랑 생성 실패")
        void createRestaurantFail() {
            //given
            RestaurantRequestDto restaurantRequestDto = mock(RestaurantRequestDto.class);
            UserCredentials user = mock(UserCredentials.class);
            MultipartFile image = mock(MultipartFile.class);
            Owner owner = mock(Owner.class);
            Restaurant restaurant = mock(Restaurant.class);

            String restaurantImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg";

            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(s3Service.upload(image)).willReturn(restaurantImage);
            given(restaurantRequestDto.toEntity(owner, restaurantImage)).willReturn(restaurant);
            given(restaurantRepository.save(restaurant)).willThrow(
                new RestaurantCreateFailedException(RestaurantErrorCode.RESTAURANT_CREATE_FAILED));

            //when-then
            assertThrows(RestaurantCreateFailedException.class,
                () -> restaurantService.createRestaurant(restaurantRequestDto, user, image));
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
            Long restaurantId = 1L;
            Restaurant restaurant = mock(Restaurant.class);
            RestaurantResponseDto expectedResponse = RestaurantResponseDto.from(restaurant);

            given(restaurantRepository.findByIdOrThrow(restaurantId)).willReturn(restaurant);

            //when
            RestaurantResponseDto restaurantResponseDto = restaurantService.getRestaurant(
                restaurantId);

            //then
            assertThat(restaurantResponseDto).isEqualTo(expectedResponse);
            then(restaurantRepository).should(times(1)).findByIdOrThrow(restaurantId);
        }
    }

    @Nested
    @DisplayName("레스토랑 수정")
    class UpdateRestaurant {

        @Test
        @DisplayName("레스토랑 수정 성공")
        void updateRestaurantSuccess() {
            //given
            Long restaurantId = 1L;
            RestaurantRequestDto restaurantRequestDto = mock(RestaurantRequestDto.class);
            UserCredentials user = mock(UserCredentials.class);
            MultipartFile image = mock(MultipartFile.class);
            Owner owner = mock(Owner.class);
            Restaurant restaurant = mock(Restaurant.class);

            String originalImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg";
            String newImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/123456.jpg";

            given(restaurantRepository.findByIdOrThrow(restaurantId)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(restaurant.getOwner()).willReturn(owner);
            given(restaurant.getImageUrl()).willReturn(originalImage);
            given(s3Service.upload(image)).willReturn(newImage);

            //when
            restaurantService.updateRestaurant(restaurantId, restaurantRequestDto, user, image);

            //then
            then(restaurantRepository).should(times(1)).findByIdOrThrow(restaurantId);
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(s3Service).should(times(1)).upload(image);
            then(s3Service).should(times(1)).deleteImageFromS3(originalImage);
            then(restaurant).should(times(1)).update(restaurantRequestDto, newImage);
        }

        @Test
        @DisplayName("레스토랑 수정 실패")
        void updateRestaurantFail() {

            //given
            Long restaurantId = 1L;
            RestaurantRequestDto restaurantRequestDto = mock(RestaurantRequestDto.class);
            UserCredentials user = mock(UserCredentials.class);
            MultipartFile image = mock(MultipartFile.class);
            Owner owner = mock(Owner.class);
            Restaurant restaurant = mock(Restaurant.class);

            String originalImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg";
            String newImage = "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/123456.jpg";

            given(restaurantRepository.findByIdOrThrow(restaurantId)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(restaurant.getOwner()).willReturn(owner);
            given(image.isEmpty()).willReturn(false);
            given(restaurant.getImageUrl()).willReturn(originalImage);
            given(s3Service.upload(image)).willReturn(newImage);
            doThrow(
                new RestaurantUpdateFailedException(RestaurantErrorCode.RESTAURANT_UPDATE_FAILED))
                .when(restaurant).update(restaurantRequestDto, newImage);

            //when-then
            assertThrows(RestaurantUpdateFailedException.class,
                () -> restaurantService.updateRestaurant(restaurantId, restaurantRequestDto, user,
                    image));
            then(restaurantRepository).should(times(1)).findByIdOrThrow(restaurantId);
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
            Long restaurantId = 1L;
            UserCredentials user = mock(UserCredentials.class);
            Owner owner = mock(Owner.class);
            Restaurant restaurant = mock(Restaurant.class);

            given(restaurantRepository.findByIdOrThrow(restaurantId)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(restaurant.getOwner()).willReturn(owner);
            given(restaurant.getImageUrl()).willReturn(
                "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg");

            //when
            restaurantService.deleteRestaurant(restaurantId, user);

            //then
            then(restaurantRepository).should(times(1)).findByIdOrThrow(restaurantId);
            then(ownerRepository).should(times(1)).findByIdOrThrow(user.getId());
            then(restaurantRepository).should(times(1)).delete(restaurant);
            then(s3Service).should(times(1)).deleteImageFromS3(
                "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg");
        }

        @Test
        @DisplayName("레스토랑 삭제 실패 - 해당 레스토랑에 대한 권한이 없을 경우")
        void deleteRestaurantFail() {
            //given
            Long restaurantId = 1L;
            UserCredentials user = mock(UserCredentials.class);
            Owner owner = mock(Owner.class);
            Owner restaurantOwner = mock(Owner.class);
            Restaurant restaurant = mock(Restaurant.class);

            given(restaurantRepository.findByIdOrThrow(restaurantId)).willReturn(restaurant);
            given(ownerRepository.findByIdOrThrow(user.getId())).willReturn(owner);
            given(restaurant.getOwner()).willReturn(restaurantOwner);
            given(owner.getId()).willReturn(1L);
            given(restaurantOwner.getId()).willReturn(2L);

            //when-then
            assertThrows(AuthException.class,
                () -> restaurantService.deleteRestaurant(restaurantId, user));
            then(restaurantRepository).should(never()).delete(restaurant);
            then(s3Service).should(never()).deleteImageFromS3(anyString());
        }
    }
}