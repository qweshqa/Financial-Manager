package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Earning;
import org.qweshqa.financialmanager.repositories.EarningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class EarningService {
    private final EarningRepository earningRepository;

    @Autowired
    public EarningService(EarningRepository earningRepository) {
        this.earningRepository = earningRepository;
    }

    public List<Earning> index(){
        return earningRepository.findAll();
    }

    public List<Earning> index(LocalDate date){
        return earningRepository.findAllByDate(date);
    }

    public BigDecimal getEarningTotalByDate(LocalDate date){
        BigDecimal earning_total = BigDecimal.ZERO;
        List<Earning> earningList = earningRepository.findAllByDate(date);

        for(Earning earning: earningList){
            earning_total =  earning_total.add(earning.getAmount());
        }

        return earning_total;
    }

    public void save(Earning earning){
        earningRepository.save(earning);
    }

    public void delete(int id){
        earningRepository.deleteById(id);
    }
}
