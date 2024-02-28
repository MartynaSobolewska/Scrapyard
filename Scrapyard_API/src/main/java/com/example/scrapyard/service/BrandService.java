package com.example.scrapyard.service;

import com.example.scrapyard.api.exceptions.BrandExistsException;
import com.example.scrapyard.api.exceptions.BrandNotFoundException;
import com.example.scrapyard.domain.BrandDTO;
import com.example.scrapyard.model.Brand;

import java.util.List;
import java.util.UUID;

public interface BrandService {
    Brand addBrand(BrandDTO brandDTO) throws BrandExistsException;
    Brand getBrandById(UUID id) throws BrandNotFoundException;

    List<Brand> getAllBrands();

    void deleteBrandById(UUID brandId) throws BrandNotFoundException;
}
