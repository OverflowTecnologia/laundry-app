package com.overflow.laundry.controller;

import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.model.dto.CondominiumDto;
import com.overflow.laundry.service.CondominiumService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.overflow.laundry.constant.MessageResponseEnum.CONDOMINIUM_CREATED;

@RestController
@RequestMapping("/condominiums")
public class CondominiumController {

  private final CondominiumService condominiumService;

  public CondominiumController(CondominiumService condominiumService) {
    this.condominiumService = condominiumService;
  }

  @PostMapping
  public ResponseEntity<StandardResponse<CondominiumDto>> createCondominium(@Valid @RequestBody
                                                                              CondominiumDto condominiumDto) {
    CondominiumDto createdCondominium = condominiumService.createCondominium(condominiumDto);
    return StandardResponse.success(CONDOMINIUM_CREATED, createdCondominium);
  }
}
