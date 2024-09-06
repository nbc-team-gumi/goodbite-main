package site.mygumi.goodbite.domain.menu.repository;

import site.mygumi.goodbite.domain.menu.entity.Menu;
import site.mygumi.goodbite.exception.menu.MenuErrorCode;
import site.mygumi.goodbite.exception.menu.detail.MenuNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    default Menu findByIdOrThrow(Long menuId) {
        return findById(menuId).orElseThrow(
            () -> new MenuNotFoundException(MenuErrorCode.MENU_NOT_FOUND));
    }

    Page<Menu> findPageByRestaurantId(Long restaurantId, Pageable pageable);

    List<Menu> findAllByRestaurantId(Long restaurantId);
}