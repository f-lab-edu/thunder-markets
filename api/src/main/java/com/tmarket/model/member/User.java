package com.tmarket.model.member;

import com.tmarket.model.product.Products;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", length = 50)
    private Long userId; // 유저 ID (PK)

    @Column(name = "user_name", nullable = false, length = 30)
    private String userName; // 유저 이름

    @Column(name = "email", nullable = false, length = 30, unique = true)
    private String email; // 이메일

    @Column(name = "password", nullable = false, length = 200)
    private String password; // 비밀번호

    @Column(name = "member_stts", nullable = false, length = 10)
    private String memberStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private Date registDate; // 등록일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mod_dt")
    private Date modifyDate; // 수정일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "del_dt")
    private Date deleteDate; // 삭제일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_dt")
    private Date lastLoginDate; // 삭제일

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // User(판매자) -> Products (1:N 관계 설정)
    // CascadeType.ALL: User 삭제 시 관련된 Products도 삭제 가능
    // orphanRemoval = true: User에서 제거된 Products는 자동 삭제
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Products> products;

    // 엔티티가 처음 저장되기 전에 실행되는 메서드 (회원 가입 시 자동 설정)
    @PrePersist
    protected void onCreate() {
        this.registDate = new Date();
        this.modifyDate = new Date();
        this.isActive = true;
    }

    // 엔티티가 수정되기 전에 실행되는 메서드 (회원 정보 수정 시 자동 설정)
    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = new Date();
    }

    // 마지막 로그인 시간 업데이트 메서드 (로그인할 때 호출)
    public void updateLastLogin() {
        this.lastLoginDate = new Date();
    }

    // 계정 삭제 메서드 (실제 삭제 대신 비활성화 처리 가능)
    public void deactivateAccount() {
        this.isActive = false;
        this.deleteDate = new Date();
    }
}