package site.mygumi.goodbite.domain.user.customer.entity;

import site.mygumi.goodbite.common.entity.ExtendedTimestamped;
import site.mygumi.goodbite.domain.user.entity.UserCredentials;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "customer")
@Getter
@Entity
@NoArgsConstructor
public class Customer extends ExtendedTimestamped implements UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    /*@OneToMany(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waiting> waitingList;*/

    @Builder
    public Customer(String password, String email, String nickname, String phoneNumber) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
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
        return true;
    }

    @Override
    public boolean isOwner() {
        return false;
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

    // 소프트 삭제를 위한 메서드 추가
    public void deactivate() {
        this.deletedAt = LocalDateTime.now();
    }
}