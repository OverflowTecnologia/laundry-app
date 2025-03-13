package com.overflow.laundry.repository;

import com.overflow.laundry.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> , MachineRepositoryCustom {
}
