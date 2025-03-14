package com.overflow.laundry.repository.impl;

import com.overflow.laundry.constant.ObjectValidatorErrors;
import com.overflow.laundry.exception.MachineNotFoundException;
import com.overflow.laundry.model.Machine;
import com.overflow.laundry.repository.MachineRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class MachineRepositoryImpl implements MachineRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Machine findMachineByIdentifier(String identifier) {
    try {
      TypedQuery<Machine> query = entityManager.createQuery(
          "SELECT m FROM Machine m WHERE m.identifier = :identifier", Machine.class);
      query.setParameter("identifier", identifier);
      return query.getSingleResult();
    } catch (NoResultException e) {
      throw new MachineNotFoundException(ObjectValidatorErrors.MACHINE_NOT_FOUND);
    }
  }
}