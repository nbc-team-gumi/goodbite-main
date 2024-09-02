package site.mygumi.goodbite.domain.owner.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OwnerStatus {
    VERIFIED("VERIFIED"), //인증완료 상태
    UNVERIFIED("UNVERIFIED");
    
    private final String ownerStatus;
}
