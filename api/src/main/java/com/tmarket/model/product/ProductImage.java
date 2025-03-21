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
    private Long productImageIndex; // 상품 이미지 일련번호 (PK, auto_increment)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private Date registDate; // 등록일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mod_dt")
    private Date modifyDate; // 수정일

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "del_dt")
    private Date deleteDate; // 삭제일

    @Column(name = "act_file_nm", nullable = false, length = 255)
    private String actFileName; // 파일명 (UUID로 저장)

    @Column(name = "act_file_ori_nm", nullable = false, length = 255)
    private String actFileOriginName; // 파일 원본명

    @Column(name = "img_path_nm", nullable = false, length = 500)
    private String imagePathName; // 파일 경로명

    @Column(name = "del_yn", nullable = false, length = 1)
    private char deleteYesNo; // 삭제 여부 ('Y' 또는 'N')

    // products 테이블의 product_id를 참조하는 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    //@JsonBackReference
    private Products product; // 상품 정보

    // 엔티티 생성 전 실행되는 메서드
    @PrePersist
    protected void onCreate() {
        this.registDate = new Date();
        this.modifyDate = new Date();
        this.deleteYesNo = 'N'; // 기본값: 'N'
    }

    // 엔티티 수정 전 실행되는 메서드
    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = new Date();
    }

    public ProductImage(ProductImageDTO imageDTO, Products product) {
        this.product = product;
        this.registDate = imageDTO.getRegistDate();
        this.modifyDate = imageDTO.getModifyDate();
        this.deleteDate = imageDTO.getDeleteDate();
        this.actFileName = imageDTO.getActFileName();
        this.actFileOriginName = imageDTO.getActFileOriginName();
        this.imagePathName = imageDTO.getImagePathName();
        this.deleteYesNo = imageDTO.getDeleteYesNo();
    }

    public ProductImageDTO toDTO() {
        return ProductImageDTO.builder()
                .productImageIndex(this.productImageIndex)
                .actFileName(this.actFileName)
                .actFileOriginName(this.actFileOriginName)
                .imagePathName(this.imagePathName)
                .deleteYesNo(this.deleteYesNo)
                .build();
    }

}