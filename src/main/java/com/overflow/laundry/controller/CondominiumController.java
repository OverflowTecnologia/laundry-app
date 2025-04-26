package com.overflow.laundry.controller;

import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.model.dto.CondominiumRequestDto;
import com.overflow.laundry.model.dto.CondominiumResponseDto;
import com.overflow.laundry.service.CondominiumService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.overflow.laundry.constant.MessageResponseEnum.CONDOMINIUM_CREATED;
import static com.overflow.laundry.constant.MessageResponseEnum.CONDOMINIUM_FOUND;

@RestController
@RequestMapping("/condominiums")
public class CondominiumController {

  private final CondominiumService condominiumService;

  public CondominiumController(CondominiumService condominiumService) {
    this.condominiumService = condominiumService;
  }

  @PostMapping
  public ResponseEntity<StandardResponse<CondominiumResponseDto>> createCondominium(
      @Valid @RequestBody CondominiumRequestDto condominiumRequestDto) {
    CondominiumResponseDto createdCondominium = condominiumService.createCondominium(condominiumRequestDto);
    return StandardResponse.success(CONDOMINIUM_CREATED, createdCondominium);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StandardResponse<CondominiumResponseDto>> getCondominiumById(@PathVariable Long id) {
    CondominiumResponseDto condominium = condominiumService.getCondominiumById(id);
    return StandardResponse.success(CONDOMINIUM_FOUND, condominium);
  }
}
