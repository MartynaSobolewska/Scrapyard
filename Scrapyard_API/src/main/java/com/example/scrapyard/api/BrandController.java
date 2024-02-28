package com.example.scrapyard.api;

import com.example.scrapyard.api.exceptions.BrandExistsException;
import com.example.scrapyard.api.exceptions.BrandNotFoundException;
import com.example.scrapyard.domain.BrandDTO;
import com.example.scrapyard.model.Brand;
import com.example.scrapyard.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Brand Controller", description = "Allows admins to modify brand information.")
@RestController
@RequestMapping(path = "/brand")
public class BrandController {
    private final BrandService brandService;


    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @Operation(
            summary = "Create new brand.",
            description = "Allows adding a brand to admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "incorrect request")
    })
    @PostMapping("")
    public ResponseEntity<URI> addNewBrand(@Valid @RequestBody BrandDTO brandDTO) throws BrandExistsException {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").
                buildAndExpand(brandService.addBrand(brandDTO).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Fetch a saved brand by id.",
            description = "Allows fetching a brand information to admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "incorrect request"),
            @ApiResponse(responseCode = "404", description = "not found")

    })
    @GetMapping(path = "{id}")
    public ResponseEntity<Brand> getBrand(@PathVariable("id")
                                           @Parameter(name = "id",
                                                   description = "Brand uuid",
                                                   example = "dc84f2ca-5f63-46ec-8c62-7f59fbab5db7") UUID brandId)
            throws BrandNotFoundException {
        return ResponseEntity.ok(brandService.getBrandById(brandId));
    }

    @Operation(
            summary = "Get all saved brands.",
            description = "Allows fetching all brand information to admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "incorrect request")
    })
    @GetMapping(path = "")
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @Operation(
            summary = "Delete a saved brand by id.",
            description = "Allows deleting brand information to admins only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "incorrect request"),
            @ApiResponse(responseCode = "404", description = "not found")

    })
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteBrandById(@PathVariable("id")
                                          @Parameter(name = "id",
                                                  description = "Brand uuid",
                                                  example = "dc84f2ca-5f63-46ec-8c62-7f59fbab5db7") UUID brandId)
            throws BrandNotFoundException {
        brandService.deleteBrandById(brandId);
        return ResponseEntity.ok().build();
    }

}
