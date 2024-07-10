package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.Setting;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.SettingRepository;
import org.qweshqa.financialmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.CurrencyUnit;

@Service
@Transactional(readOnly = true)
public class SettingService {

    private final SettingRepository settingRepository;

    private final UserRepository userRepository;

    @Autowired
    public SettingService(SettingRepository settingRepository, UserRepository userRepository) {
        this.settingRepository = settingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveSettingSetup(Setting setting, User user, CurrencyUnit currencyUnit){
        setting.setUser(user);
        setting.setCurrencyUnit(currencyUnit.getCurrencyCode());

        settingRepository.save(setting);
    }

    @Transactional
    public void updateCurrencySetup(int settingsIdToUpdate, Setting updatedSettings){
        Setting setting = settingRepository.findById(settingsIdToUpdate).get();
        setting.setCurrencyUnit(updatedSettings.getCurrencyUnit());
    }
}
