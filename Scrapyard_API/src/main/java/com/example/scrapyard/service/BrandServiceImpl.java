package com.example.scrapyard.service;

import com.example.scrapyard.api.exceptions.BrandExistsException;
import com.example.scrapyard.api.exceptions.BrandNotFoundException;
import com.example.scrapyard.domain.BrandDTO;
import com.example.scrapyard.model.Brand;
import com.example.scrapyard.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BrandServiceImpl implements BrandService{
    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Brand addBrand(BrandDTO brandDTO) throws BrandExistsException {
        if (brandRepository.findByName(brandDTO.getName()).isEmpty()){
            return brandRepository.save(Brand.builder().name(brandDTO.getName()).build());
        }else {
            throw BrandExistsException.createWith(brandDTO.getName());
        }
    }

    @Override
    public Brand getBrandById(UUID id) throws BrandNotFoundException {
        Optional<Brand> foundBrand = brandRepository.findById(id);
        if (foundBrand.isPresent()){
            return foundBrand.get();
        }
        throw BrandNotFoundException.createWith(id.toString());
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public void deleteBrandById(UUID brandId) throws BrandNotFoundException {
        if (brandRepository.existsById(brandId))
            brandRepository.deleteById(brandId);
        else
            throw BrandNotFoundException.createWith(brandId.toString());
    }
}
