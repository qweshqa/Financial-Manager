package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Spending;
import org.qweshqa.financialmanager.repositories.SpendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpendingService {

    private final SpendingRepository spendingRepository;

    @Autowired
    public SpendingService(SpendingRepository spendingRepository) {
        this.spendingRepository = spendingRepository;
    }

    public List<Spending> index(){
        return spendingRepository.findAll();
    }

    public List<Spending> index(short page){
        return spendingRepository.findAll(PageRequest.of(page, 5)).getContent();
    }

    @Transactional
    public void save(Spending spending){
        spendingRepository.save(spending);
    }

    @Transactional
    public void delete(int id){
        spendingRepository.deleteById(id);
    }
}
