package com.overflow.laundry.repository;

import com.overflow.laundry.model.Condominium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CondominiumRepository extends JpaRepository<Condominium, Long> {
}
