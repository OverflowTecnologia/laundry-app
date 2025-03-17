package com.overflow.laundry.controller;


import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.model.dto.PaginationRequestDto;
import com.overflow.laundry.service.MachineService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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


@RestController
@RequestMapping("/machine")
public class MachineController {

  private final MachineService machineService;

  public MachineController(MachineService machineService) {
    this.machineService = machineService;
  }

  @PostMapping
  public ResponseEntity<MachineDto> createMachine(@Valid @RequestBody MachineDto machineDto) {
    MachineDto createdMachine = machineService.createMachine(machineDto);
    return ResponseEntity.accepted().body(createdMachine);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MachineDto> getMachineById(@PathVariable Long id) {
    MachineDto machine = machineService.getMachineById(id);
    return ResponseEntity.ok(machine);
  }

  @PutMapping
  public ResponseEntity<MachineDto> updateMachine(@Valid @RequestBody MachineDto machineDto) {
    MachineDto updatedMachine = machineService.updateMachine(machineDto);
    return ResponseEntity.accepted().body(updatedMachine);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteMachine(@PathVariable Long id) {
    machineService.deleteMachine(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public Page<MachineDto> getAllMachines(@RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(required = false) String sortBy,
                                         @RequestParam(required = false) String direction) {

    PaginationRequestDto paginationRequest = new PaginationRequestDto(page, size, sortBy, direction);
    Page<MachineDto> allMachines = machineService.getAllMachines(paginationRequest);
    return allMachines;

  }
}
