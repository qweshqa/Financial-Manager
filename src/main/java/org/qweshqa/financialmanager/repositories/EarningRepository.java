package org.qweshqa.financialmanager.repositories;

import org.qweshqa.financialmanager.models.Earning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EarningRepository extends JpaRepository<Earning, Integer> {
    List<Earning> findAllByDate(LocalDate localDate);
}
