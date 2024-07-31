package org.qweshqa.financialmanager.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.Setting;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.SettingRepository;
import org.qweshqa.financialmanager.utils.RequestSender;
import org.qweshqa.financialmanager.utils.exceptions.SettingNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class SettingService {

    private final SettingRepository settingRepository;

    private final RequestSender requestSender;

    @Autowired
    public SettingService(SettingRepository settingRepository, RequestSender requestSender) {
        this.settingRepository = settingRepository;
        this.requestSender = requestSender;
    }

    public Setting findById(int id){
        Optional<Setting> setting = settingRepository.findById(id);

        if(setting.isEmpty()){
            throw new SettingNotFoundException("Setting with id" + id + " doesn't exist.");
        }

        return setting.get();
    }

    @Transactional
    public void saveSettingSetup(Setting setting, User user, String currencyUnit){
        setting.setUser(user);
        setting.setCurrencyUnit(currencyUnit);

        settingRepository.save(setting);
    }

    @Transactional
    public void changeCurrencyUnit(Setting settingsToUpdate, Setting updatedSettings) {
        settingsToUpdate.setCurrencyUnit(updatedSettings.getCurrencyUnit());
    }

    @Transactional
    public void convertAmountsToNewCurrency(User user, Setting setting, String newCurrencyUnit) throws IOException, InterruptedException{
        List<Operation> operations = user.getUserOperations();

        for (Operation operation : operations) {
            HttpResponse<String> response = requestSender.sendCurrencyConvertRequest(setting.getCurrencyUnit(), newCurrencyUnit, operation.getAmount());

            if(response.statusCode() == 200){
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(response.body());

                operation.setAmount(operation.getAmount() * (float) node.get("data").get(newCurrencyUnit).asDouble());
            }
        }

        List<Account> accounts = user.getUserAccounts();

        for(Account account: accounts){
            HttpResponse<String> response = requestSender.sendCurrencyConvertRequest(setting.getCurrencyUnit(), newCurrencyUnit, account.getBalance());

            if(response.statusCode() == 200){
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode node = objectMapper.readTree(response.body());

                account.setBalance(account.getBalance() * (float) node.get("data").get(newCurrencyUnit).asDouble());
            }
        }

    }
}