package com.tmarket.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private ProductDTO productDTO;
    private List<ProductImageDTO> productImageDTO;
}
