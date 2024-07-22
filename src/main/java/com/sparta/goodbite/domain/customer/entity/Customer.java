package com.sparta.goodbite.domain.customer.entity;

import com.sparta.goodbite.common.ExtendedTimestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "customer")
@Getter
@Entity
@NoArgsConstructor

public class Customer extends ExtendedTimestamped {
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
    private String telNo;

    /*@OneToMany(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Waiting> waitingList;*/

    @Builder
    public Customer(String password, String email, String nickname, String telNo) {
        this.password=password;
        this.email=email;
        this.nickname=nickname;
        this.telNo= telNo;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

}
