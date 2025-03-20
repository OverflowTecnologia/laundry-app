package com.overflow.laundry.repository;

import com.overflow.laundry.model.Machine;
import java.util.Optional;

public interface MachineRepositoryCustom {
  Optional<Machine> findMachineByIdentifier(String identifier);
}