package org.qweshqa.financialmanager.utils;

import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.services.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class OperationScheduler {

    private final OperationService operationService;

    @Autowired
    public OperationScheduler(OperationService operationService) {
        this.operationService = operationService;
    }

    @Scheduled(fixedRate = 60000)
    public void processScheduledOperations() throws IOException, InterruptedException{
        List<Operation> operations = operationService.findAll()
                .stream().filter(Operation::isScheduled).toList();

        for (Operation operation : operations) {
            operationService.prepareForSave(operation);
        }
    }
}
