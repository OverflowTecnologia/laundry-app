package com.overflow.laundry.repository;

import com.overflow.laundry.model.Machine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MachineRepositoryTest {

  @Autowired
  private MachineRepository machineRepository;

  @Test
  void givenMachineIsSaved_whenFindMachineByIdentifierIsCalled_thenReturnMachine() {
    Machine machine = new Machine();
    machine.setIdentifier("Washing Machine");
    machine.setCondominium("Condominium");
    machine.setType("Washer");

    machineRepository.save(machine);
    Optional<Machine> foundMachine = machineRepository.findMachineByIdentifier("Washing Machine");
    assertEquals(machine, foundMachine.orElse(null));
  }

  @Test
  void givenMachineDoesNotExists_whenFindMachineByIdentifierIsCalled_thenReturnEmptyOptional() {
    Optional<Machine> foundMachine = machineRepository.findMachineByIdentifier("Washing Machine");
    assertEquals(Optional.empty(), foundMachine);
  }

}