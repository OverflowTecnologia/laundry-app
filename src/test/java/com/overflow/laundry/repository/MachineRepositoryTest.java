package com.overflow.laundry.repository;

import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Machine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MachineRepositoryTest {

    @Autowired
    private MachineRepository machineRepository;

    @Test
    public void should_returnMachine_whenFindMachineByIdentifierIsCalled(){
        Machine machine = new Machine();
        machine.setIdentifier("Washing Machine");
        machine.setCondominium("Condominium");
        machine.setType("Washer");

        machineRepository.save(machine);
        Machine foundMachine = machineRepository.findMachineByIdentifier("Washing Machine");
        assertEquals(machine, foundMachine);
    }

    @Test
    void given_nothing_whenFindMachineByIdentifierIsCalled_thenThrowMachineNotFoundException() {
        assertThrows(MachineNotFoundException.class, () -> {
            machineRepository.findMachineByIdentifier("Washing Machine");
        });
    }

}