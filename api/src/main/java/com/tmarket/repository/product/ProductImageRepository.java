package com.tmarket.repository.product;

import com.tmarket.model.product.Product;
import com.tmarket.model.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product = :product")
    List<ProductImage> findAllByProduct(@Param("product") Product product);
}
