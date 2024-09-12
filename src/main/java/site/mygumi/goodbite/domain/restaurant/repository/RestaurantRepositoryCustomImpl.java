package site.mygumi.goodbite.domain.restaurant.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import site.mygumi.goodbite.domain.restaurant.entity.QRestaurant;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import site.mygumi.goodbite.domain.restaurant.enums.Category;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

    private static final QRestaurant qRestaurant = QRestaurant.restaurant;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Restaurant> findPageByFilters(String sido, String sigungu, Category category,
        Double rating, Pageable pageable) {

        List<Restaurant> restaurants = queryFactory
            .selectFrom(qRestaurant)
            .where(
                eqSido(sido),
                eqSigungu(sigungu),
                eqCategory(category),
                goeRating(rating)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(qRestaurant)
            .where(
                allOf(
                    eqSido(sido),
                    eqSigungu(sigungu),
                    eqCategory(category),
                    goeRating(rating)
                )
            )
            .fetch()
            .size();

        return new PageImpl<>(restaurants, pageable, total);
    }

    private BooleanExpression eqSido(String sido) {
        return sido != null ? QRestaurant.restaurant.sido.eq(sido) : null;
    }

    private BooleanExpression eqSigungu(String sigungu) {
        return sigungu != null ? QRestaurant.restaurant.sigungu.eq(sigungu) : null;
    }

    private BooleanExpression eqCategory(Category category) {
        return category != null ? QRestaurant.restaurant.category.eq(category) : null;
    }

    private BooleanExpression goeRating(Double rating) {
        return rating != null ? QRestaurant.restaurant.rating.goe(rating) : null;
    }

    private BooleanExpression allOf(BooleanExpression... expressions) {
        BooleanExpression result = null;
        for (BooleanExpression expression : expressions) {
            if (expression != null) {
                result = result == null ? expression : result.and(expression);
            }
        }
        return result;
    }
}