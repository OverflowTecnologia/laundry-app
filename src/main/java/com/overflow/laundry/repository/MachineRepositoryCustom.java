package com.overflow.laundry.repository;

import com.overflow.laundry.model.Machine;
import java.util.Optional;

public interface MachineRepositoryCustom {
  Optional<Machine> findMachineByCondominiumIdAndIdentifier(String identifier, Long condominiumId);
}