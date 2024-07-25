package com.sparta.goodbite.domain.owner.entity;

import com.sparta.goodbite.common.ExtendedTimestamped;
import com.sparta.goodbite.common.UserCredentials;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email(message = "이메일 형식이 올바르지 않습니다.")
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
        String businessNumber) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.businessNumber = businessNumber;
        this.ownerStatus = OwnerStatus.UNVERIFIED;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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

    // 소프트 삭제를 위한 메서드 추가
    public void deactivate() {
        this.deletedAt = LocalDateTime.now();
    }
}

