package com.tmarket.model.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductImageMapper {

    ProductImageMapper INSTANCE = Mappers.getMapper(ProductImageMapper.class);

    List<ProductImageDTO> productImageToProductImageDTO(List<ProductImage> productImageList);
}
