package site.mygumi.goodbite.domain.user.owner.dto;

import site.mygumi.goodbite.domain.user.owner.entity.Owner;

/**
 * 사업자 정보를 응답하기 위한 DTO 클래스입니다.
 * <p>사업자 이메일, 닉네임, 전화번호, 사업자 번호를 포함하며,
 * {@link Owner} 엔티티로부터 데이터를 받아 생성할 수 있습니다.</p>
 *
 * <b>주요 필드:</b>
 * <ul>
 *   <li>email: 사업자 이메일</li>
 *   <li>nickname: 사업자 닉네임</li>
 *   <li>phoneNumber: 사업자 전화번호</li>
 *   <li>businessNumber: 사업자 등록번호</li>
 * </ul>
 *
 * @param email          사업자 이메일
 * @param nickname       사업자 닉네임
 * @param phoneNumber    사업자 전화번호
 * @param businessNumber 사업자 등록번호
 */
public record OwnerResponseDto(String email, String nickname, String phoneNumber,
                               String businessNumber) {

    /**
     * {@link Owner} 엔티티 객체로부터 {@link OwnerResponseDto} 객체를 생성합니다.
     *
     * @param owner {@link Owner} 엔티티 객체
     * @return {@link OwnerResponseDto} 객체
     */
    public static OwnerResponseDto from(Owner owner) {
        return new OwnerResponseDto(
            owner.getEmail(),
            owner.getNickname(),
            owner.getPhoneNumber(),
            owner.getBusinessNumber());
    }
}
