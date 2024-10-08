package site.mygumi.goodbite.domain.reservation.entity;

import site.mygumi.goodbite.common.entity.ExtendedTimestamped;
import site.mygumi.goodbite.domain.user.customer.entity.Customer;
import site.mygumi.goodbite.domain.restaurant.entity.Restaurant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reservation extends ExtendedTimestamped {

    public static final int RESERVATION_DURATION_HOUR = 1;
    public static final int DEFAULT_PAGE_SIZE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationMenu> reservationMenus;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    private String requirement;

    @Column(nullable = false)
    private int partySize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Builder
    public Reservation(Customer customer, Restaurant restaurant,
        List<ReservationMenu> reservationMenus, LocalDate date, LocalTime time, String requirement,
        int partySize, ReservationStatus status) {

        this.customer = customer;
        this.restaurant = restaurant;
        this.reservationMenus = reservationMenus;
        this.date = date;
        this.time = time;
        this.requirement = requirement;
        this.partySize = partySize;
        this.status = status;
    }

    public boolean canSubmitReview() {
        return this.status == ReservationStatus.COMPLETED &&
            ChronoUnit.MINUTES.between(
                LocalDateTime.of(this.date, this.time).plusHours(RESERVATION_DURATION_HOUR),
                LocalDateTime.now())
                <= 60 * 24 * 3;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }

    public void cancel() {
        this.deletedAt = LocalDateTime.now();
        this.status = ReservationStatus.CANCELLED;
    }

    public void reject() {
        this.deletedAt = LocalDateTime.now();
        this.status = ReservationStatus.REJECTED;
    }
}