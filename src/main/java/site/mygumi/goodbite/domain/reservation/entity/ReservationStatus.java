package site.mygumi.goodbite.domain.reservation.entity;

public enum ReservationStatus {
    PENDING, // 예약 확정 또는 거절 전
    CONFIRMED, // 예약 확정 (예약 시간 전까지의 상태)
    CANCELLED, // 예약 취소
    COMPLETED, // 예약 완료 (예약 시간 후 상태)
    REJECTED // 예약할 수 없는 경우
}