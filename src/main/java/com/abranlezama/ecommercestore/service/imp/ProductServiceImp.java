package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;
import com.abranlezama.ecommercestore.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    @Override
    public List<ProductResponseDTO> getProducts(int page, int pageSize) {
        return null;
    }
}
