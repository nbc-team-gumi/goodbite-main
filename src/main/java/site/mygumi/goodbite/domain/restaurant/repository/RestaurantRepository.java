package site.mygumi.goodbite.domain.restaurant.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.exception.RestaurantErrorCode;
import site.mygumi.goodbite.domain.restaurant.exception.detail.RestaurantNotFoundException;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>,
    RestaurantRepositoryCustom {

    Optional<Restaurant> findByOwnerId(Long ownerId);

    default void validateById(Long restaurantId) {
        if (!existsById(restaurantId)) {
            throw new RestaurantNotFoundException(RestaurantErrorCode.RESTAURANT_NOT_FOUND);
        }
    }

    default Restaurant findByIdOrThrow(Long restaurantId) {
        return findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(
            RestaurantErrorCode.RESTAURANT_NOT_FOUND));
    }

    default Restaurant findByOwnerIdOrThrow(Long ownerId) {
        return findByOwnerId(ownerId).orElseThrow(() -> new RestaurantNotFoundException(
            RestaurantErrorCode.RESTAURANT_NOT_FOUND));
    }
}