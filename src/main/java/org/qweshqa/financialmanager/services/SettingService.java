package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Setting;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.CurrencyUnit;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SettingService {

    private final SettingRepository settingRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public Optional<Setting> findSettingByUser(User user){
        return settingRepository.findByUser(user);
    }

    @Transactional
    public void saveSettingSetup(Setting setting, User user, CurrencyUnit currencyUnit){
        setting.setUser(user);
        setting.setCurrencyUnit(currencyUnit.getCurrencyCode());

        settingRepository.save(setting);
    }
}
