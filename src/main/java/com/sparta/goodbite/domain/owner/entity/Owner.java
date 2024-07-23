package com.sparta.goodbite.domain.owner.entity;

import com.sparta.goodbite.common.ExtendedTimestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "owner")
@Getter
@Entity
@NoArgsConstructor
public class Owner extends ExtendedTimestamped {
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

    @Column(columnDefinition = "varchar(30)",nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OwnerStatus ownerStatus;

    @Column(nullable = false, unique = true)
    private String businessNumber;

    /*@OneToMany(mappedBy = "owner",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waiting> waitingList;*/

    @Builder
    public Owner(String password, String email, String nickname, String phoneNumber, String businessNumber) {
        this.password=password;
        this.email=email;
        this.nickname=nickname;
        this.phoneNumber= phoneNumber;
        this.businessNumber=businessNumber;
        this.ownerStatus = OwnerStatus.UNVERIFIED;
    }

}

