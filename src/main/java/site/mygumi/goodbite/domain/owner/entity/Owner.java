package site.mygumi.goodbite.domain.owner.entity;

import site.mygumi.goodbite.common.ExtendedTimestamped;
import site.mygumi.goodbite.common.UserCredentials;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "owner")
@Getter
@Entity
@NoArgsConstructor
public class Owner extends ExtendedTimestamped implements UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(columnDefinition = "varchar(30)", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OwnerStatus ownerStatus;

    @Column(nullable = false, unique = true)
    private String businessNumber;

    /*@OneToMany(mappedBy = "owner",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waiting> waitingList;*/


    @Builder
    public Owner(String password, String email, String nickname, String phoneNumber,
        String businessNumber, OwnerStatus ownerStatus) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.businessNumber = businessNumber;
        this.ownerStatus = ownerStatus;
        //기존 방식에서는 가입후 어드민계정에서 승인을 해주는 방식을 생각했기때문에 가입하자마자는 UNVERIFIED가 되게 했지만,
        //지금은 사업자번호조회API를 통해 유효하지 않은 사업자번호일때 아예 가입이 되지 않게 했고,
        //사업자번호조회시 01인 오너유저, 즉 계속사업자의 경우에만 verifyBusinessNumber메서드에서 true를 반환하고,
        //OwnerService에서는 해당 반환값을 확인해서 true일때 VERIFIED값을 주도록 설정했다.
        //결국 정리하자면 기존 방식과의 차이점은
        //일단 가입시키되 미인증상태를 기본값으로 주었다가 어드민에서 승인시 인증상태로 변환하여, 인증이 완료되기전엔 가입을 했더라도 로그인할수 없었다.
        //이제는 가입시 유효하지않으면 아예 가입이 불가능하고 유효할경우 바로 인증상태를 발급해주어 가입이 성공했다면 로그인도 당연히 바로 가능하다.
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isCustomer() {
        return false;
    }

    @Override
    public boolean isOwner() {
        return true;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updatePhoneNumber(String newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateBusinessNumber(String newBusinessNumber) {
        this.businessNumber = newBusinessNumber;
    }

    public void updateOwnerStatus(OwnerStatus newOwnerStatus) {
        this.ownerStatus = newOwnerStatus;
    }
    //주석되어있던 오너의 상태변경 메서드를 이름변경과 함께 메서드를 만들었다.
    //사실 애초에 사업자번호또한 유효하지 않으면 변경이 불가하기 때문에
    //UNVERIFIED상태가 저장될일은 없다. 기존번호가 VERIFIED-> 새로바뀐 번호가 VERIFIED이렇게 된다.

    // 소프트 삭제를 위한 메서드 추가
    public void deactivate() {
        this.deletedAt = LocalDateTime.now();
    }

}