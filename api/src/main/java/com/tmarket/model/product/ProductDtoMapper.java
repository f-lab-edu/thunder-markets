package com.tmarket.model.product;
import com.tmarket.model.member.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductDtoMapper {

    ProductDtoMapper INSTANCE = Mappers.getMapper(ProductDtoMapper.class);

    // seller는 무시하고 ProductDTO -> Product 변환
    @Mapping(target = "seller", ignore = true)
    Product productDTOtoProduct(ProductDTO productDTO);

    // Custom Method Parameter방식으로 seller를 따로 주입
    default Product productDTOtoProduct(ProductDTO productDTO, User seller) {
        Product product = productDTOtoProduct(productDTO);
        product.setSeller(seller);
        return product;
    }

    // 엔티티 -> DTO 변환
    // source: 엔티티, target: DTO

    // DTO -> 엔티티 변환
    // source: DTO, target: 엔티티
}
