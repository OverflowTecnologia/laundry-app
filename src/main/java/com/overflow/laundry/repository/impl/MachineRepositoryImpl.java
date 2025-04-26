package com.overflow.laundry.repository.impl;

import com.overflow.laundry.model.Machine;
import com.overflow.laundry.repository.MachineRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MachineRepositoryImpl implements MachineRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Optional<Machine> findMachineByCondominiumIdAndIdentifier(String identifier, Long condominiumId) {
    try {
      TypedQuery<Machine> query = entityManager.createQuery(
          "SELECT m FROM Machine m WHERE m.identifier = :identifier AND m.condominium.id = :condominiumId",
          Machine.class);
      query.setParameter("identifier", identifier);
      query.setParameter("condominiumId", condominiumId);
      return Optional.of(query.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}