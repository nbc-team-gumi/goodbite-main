package site.mygumi.goodbite.domain.restaurant.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import site.mygumi.goodbite.common.external.s3.service.S3Service;
import site.mygumi.goodbite.domain.restaurant.dto.RestaurantRequestDto;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.repository.RestaurantRepository;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import site.mygumi.goodbite.domain.user.owner.entity.Owner;
import site.mygumi.goodbite.domain.user.owner.repository.OwnerRepository;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @InjectMocks
    RestaurantService restaurantService;

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    OwnerRepository ownerRepository;

    @Mock
    S3Service s3Service;

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

        when(ownerRepository.findByIdOrThrow(user.getId())).thenReturn(owner);
        when(s3Service.upload(image)).thenReturn(restaurantImage);
        when(restaurantRequestDto.toEntity(owner, restaurantImage)).thenReturn(restaurant);
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        //when
        restaurantService.createRestaurant(restaurantRequestDto, user, image);

        //then
        verify(ownerRepository, times(1)).findByIdOrThrow(user.getId());
        verify(s3Service, times(1)).upload(image);
        verify(restaurantRepository, times(1)).save(restaurant);
        verify(s3Service, never()).deleteImageFromS3(restaurantImage);
    }

    @Test
    @DisplayName("내 레스토랑 수정 성공")
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

        when(restaurantRepository.findByIdOrThrow(restaurantId)).thenReturn(restaurant);
        when(ownerRepository.findByIdOrThrow(user.getId())).thenReturn(owner);
        when(restaurant.getOwner()).thenReturn(owner);
        when(restaurant.getImageUrl()).thenReturn(originalImage);
        when(s3Service.upload(image)).thenReturn(newImage);

        //when
        restaurantService.updateRestaurant(restaurantId, restaurantRequestDto, user, image);

        //then
        verify(restaurantRepository, times(1)).findByIdOrThrow(restaurantId);
        verify(ownerRepository, times(1)).findByIdOrThrow(user.getId());
        verify(s3Service, times(1)).upload(image);
        verify(s3Service, times(1)).deleteImageFromS3(originalImage);
        verify(restaurant, times(1)).update(restaurantRequestDto, newImage);
    }

    @Test
    @DisplayName("내 레스토랑 삭제 성공")
    void deleteRestaurantSuccess() {
        //given
        Long restaurantId = 1L;
        UserCredentials user = mock(UserCredentials.class);
        Owner owner = mock(Owner.class);
        Restaurant restaurant = mock(Restaurant.class);

        when(restaurantRepository.findByIdOrThrow(restaurantId)).thenReturn(restaurant);
        when(ownerRepository.findByIdOrThrow(user.getId())).thenReturn(owner);
        when(restaurant.getOwner()).thenReturn(owner);
        when(restaurant.getImageUrl()).thenReturn(
            "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg");

        //when
        restaurantService.deleteRestaurant(restaurantId, user);

        //then
        verify(restaurantRepository, times(1)).findByIdOrThrow(restaurantId);
        verify(ownerRepository, times(1)).findByIdOrThrow(user.getId());
        verify(restaurantRepository, times(1)).delete(restaurant);
        verify(s3Service, times(1)).deleteImageFromS3(
            "https://s3.ap-northeast-2.amazonaws.com/goodbite-bucket/1234.jpg");
    }
}