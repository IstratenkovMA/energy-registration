package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FractionServiceTest {

    @Mock
    FractionRepository fractionRepository;
    @Mock
    ProfileRepository profileRepository;

    FractionService fractionService = new FractionService(fractionRepository, profileRepository);

    @Test
    void validateParsedFractions() {
//        fractionService.validateParsedFractions()
    }

    @Test
    void saveFractions() {
    }
}