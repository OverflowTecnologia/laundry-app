package com.overflow.laundry.controller;


import com.overflow.laundry.config.StandardResponse;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.model.dto.PaginationResponseDto;
import com.overflow.laundry.service.MachineService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
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
public class MachineController {

  private final MachineService machineService;

  public MachineController(MachineService machineService) {
    this.machineService = machineService;
  }

  @PostMapping
  public ResponseEntity<StandardResponse<MachineDto>> createMachine(@Valid @RequestBody MachineDto machineDto) {
    MachineDto createdMachine = machineService.createMachine(machineDto);
    return StandardResponse.success(MACHINE_CREATED, createdMachine);

  }

  @GetMapping("/{id}")
  public ResponseEntity<StandardResponse<MachineDto>> getMachineById(@PathVariable Long id) {
    MachineDto machine = machineService.getMachineById(id);
    return StandardResponse.success(MACHINE_FOUND, machine);
  }

  @PutMapping
  public ResponseEntity<StandardResponse<MachineDto>> updateMachine(@Valid @RequestBody MachineDto machineDto) {
    MachineDto updatedMachine = machineService.updateMachine(machineDto);
    return StandardResponse.success(MACHINE_UPDATED, updatedMachine);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<StandardResponse<String>> deleteMachine(@PathVariable Long id) {
    machineService.deleteMachine(id);
    return StandardResponse.success(MACHINE_DELETED, "");
  }

  @GetMapping("/identifier/{identifier}")
  public ResponseEntity<StandardResponse<MachineDto>> getMachineByIdentifier(
      @PathVariable @NotBlank String identifier) {
    MachineDto machine = machineService.getMachineByIdentifier(identifier);
    return StandardResponse.success(MACHINE_FOUND, machine);
  }

  @GetMapping
  public ResponseEntity<StandardResponse<PaginationResponseDto<MachineDto>>> getAllMachines(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String direction) {

    final PaginationRequestDto paginationRequest = new PaginationRequestDto(page, size, sortBy, direction);
    final PaginationResponseDto<MachineDto> allMachines = machineService.getAllMachines(paginationRequest);
    return StandardResponse.success(MACHINE_FOUND, allMachines);

  }
}
