package com.overflow.laundry.repository;

import com.overflow.laundry.model.Machine;

public interface MachineRepositoryCustom {
  // TODO Implement on controller
  Machine findMachineByIdentifier(String identifier);
}