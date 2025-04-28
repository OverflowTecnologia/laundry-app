package com.overflow.laundry.controller;


import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.model.dto.MachineRequestDto;
import com.overflow.laundry.model.dto.MachineResponseDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import com.overflow.laundry.service.MachineService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_CREATED;
import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_DELETED;
import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_FOUND;
import static com.overflow.laundry.constant.MessageResponseEnum.MACHINE_UPDATED;


@RestController
@RequestMapping("/machines")
@Validated
public class MachineController {

  private final MachineService machineService;

  public MachineController(MachineService machineService) {
    this.machineService = machineService;
  }

  @PostMapping
  public ResponseEntity<StandardResponse<MachineResponseDto>> createMachine(
      @Valid @RequestBody MachineRequestDto machineRequestDto) {
    MachineResponseDto createdMachine = machineService.createMachine(machineRequestDto);
    return StandardResponse.success(MACHINE_CREATED, createdMachine);

  }

  @GetMapping("/{id}")
  public ResponseEntity<StandardResponse<MachineResponseDto>> getMachineById(@PathVariable Long id) {
    MachineResponseDto machine = machineService.getMachineById(id);
    return StandardResponse.success(MACHINE_FOUND, machine);
  }

  @PutMapping
  public ResponseEntity<StandardResponse<MachineResponseDto>> updateMachine(
      @Valid @RequestBody MachineRequestDto machineRequestDto) {
    MachineResponseDto updatedMachine = machineService.updateMachine(machineRequestDto);
    return StandardResponse.success(MACHINE_UPDATED, updatedMachine);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<StandardResponse<String>> deleteMachine(@PathVariable Long id) {
    machineService.deleteMachine(id);
    return StandardResponse.success(MACHINE_DELETED, "");
  }


  @GetMapping("/identifier")
  public ResponseEntity<StandardResponse<MachineResponseDto>> getMachineByIdentifier(
      @RequestParam @NotNull Long condominiumId,
      @RequestParam @NotBlank String identifier) {
    MachineResponseDto machine = machineService.getMachineByCondominiumAndIdentifier(condominiumId, identifier);
    return StandardResponse.success(MACHINE_FOUND, machine);
  }

  @GetMapping
  public ResponseEntity<StandardResponse<PaginationResponseDto<MachineResponseDto>>> getAllMachines(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String direction) {

    PaginationRequestDto paginationRequest = new PaginationRequestDto(page, size, sortBy, direction);
    PaginationResponseDto<MachineResponseDto> allMachines = machineService.getAllMachines(paginationRequest);
    return StandardResponse.success(MACHINE_FOUND, allMachines);

  }
}
