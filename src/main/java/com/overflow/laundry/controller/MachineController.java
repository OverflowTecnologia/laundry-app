package com.overflow.laundry.controller;


import com.oracle.svm.core.annotate.Delete;
import com.overflow.laundry.model.dto.MachineDto;
import com.overflow.laundry.service.MachineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}
