package com.tmarket.model.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_img")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_img_idx")
    private Long productImgIdx; // 상품 이미지 일련번호 (PK, auto_increment)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private Date regDt; // 등록일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mod_dt")
    private Date modDt; // 수정일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "del_dt")
    private Date delDt; // 삭제일

    @Column(name = "act_file_nm", nullable = false, length = 255)
    private String actFileNm; // 파일명 (UUID로 저장)

    @Column(name = "act_file_ori_nm", nullable = false, length = 255)
    private String actFileOriNm; // 파일 원본명

    @Column(name = "img_path_nm", nullable = false, length = 500)
    private String imgPathNm; // 파일 경로명

    @Column(name = "del_yn", nullable = false, length = 1)
    private char delYn; // 삭제 여부 ('Y' 또는 'N')

    // products 테이블의 product_id를 참조하는 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product; // 상품 정보

    // 엔티티 생성 전 실행되는 메서드
    @PrePersist
    protected void onCreate() {
        this.regDt = new Date();
        this.modDt = new Date();
        this.delYn = 'N'; // 기본값: 'N'
    }

    // 엔티티 수정 전 실행되는 메서드
    @PreUpdate
    protected void onUpdate() {
        this.modDt = new Date();
    }
}