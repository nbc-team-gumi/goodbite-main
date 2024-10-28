package site.mygumi.goodbite.domain.user.customer.dto;

import site.mygumi.goodbite.domain.user.customer.entity.Customer;

/**
 * 고객 정보를 담은 DTO 클래스입니다.
 * <p>
 * 이메일, 닉네임, 전화번호를 포함하여 고객의 정보를 간결하게 표현하며, {@link Customer} 엔티티로부터 DTO로 변환하는 정적 메서드를 제공합니다.
 * </p>
 *
 * @param email       고객의 이메일 주소
 * @param nickname    고객의 닉네임
 * @param phoneNumber 고객의 전화번호
 */
public record CustomerResponseDto(String email, String nickname, String phoneNumber) {

    /**
     * {@link Customer} 엔티티를 {@link CustomerResponseDto}로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param customer 변환할 {@link Customer} 객체
     * @return {@link CustomerResponseDto}로 변환된 객체
     */
    public static CustomerResponseDto from(Customer customer) {
        return new CustomerResponseDto(
            customer.getEmail(),
            customer.getNickname(),
            customer.getPhoneNumber());
    }
}