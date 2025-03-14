package com.overflow.laundry.repository;

import com.overflow.laundry.model.Machine;

public interface MachineRepositoryCustom {
  Machine findMachineByIdentifier(String identifier);
}