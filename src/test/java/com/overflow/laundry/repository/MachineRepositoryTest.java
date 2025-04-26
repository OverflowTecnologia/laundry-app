package com.overflow.laundry.repository;

import com.overflow.laundry.model.Condominium;
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

  @Autowired
  private CondominiumRepository condominiumRepository;

  private Condominium createAndSaveCondominium() {
    Condominium condominium = new Condominium();
    condominium.setName("Condominium 1");
    condominium.setAddress("123 Main St");
    condominium.setContactPhone("123456789");
    condominium.setEmail("test@test.com");
    return condominiumRepository.save(condominium);
  }

  private Machine createAndSaveMachine(Condominium condominium) {
    Machine machine = new Machine();
    machine.setIdentifier("Washing Machine");
    machine.setType("Washer");
    machine.setCondominium(condominium);
    return machineRepository.save(machine);
  }

  @Test
  void givenMachineIsSaved_whenFindMachineByIdentifierIsCalled_thenReturnMachine() {
    Condominium condominium = createAndSaveCondominium();
    Machine machine = createAndSaveMachine(condominium);

    Optional<Machine> foundMachine = machineRepository.findMachineByCondominiumIdAndIdentifier(
        "Washing Machine", condominium.getId());
    assertEquals(Optional.of(machine), foundMachine);
  }

  @Test
  void givenMachineIsSaved_whenFindMachineByIdentifierIsCalledWithDifferentCondominiumId_thenReturnMachine() {
    Condominium condominium = createAndSaveCondominium();
    createAndSaveMachine(condominium);

    Optional<Machine> foundMachine = machineRepository.findMachineByCondominiumIdAndIdentifier(
        "Washing Machine", 64984L);
    assertEquals(Optional.empty(), foundMachine);
  }

  @Test
  void givenMachineDoesNotExists_whenFindMachineByIdentifierIsCalled_thenReturnEmptyOptional() {
    Optional<Machine> foundMachine = machineRepository.findMachineByCondominiumIdAndIdentifier(
        "Washing Machine", 1L);
    assertEquals(Optional.empty(), foundMachine);
  }
}