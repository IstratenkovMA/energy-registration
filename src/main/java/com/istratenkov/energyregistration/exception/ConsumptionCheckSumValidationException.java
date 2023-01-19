package com.istratenkov.energyregistration.exception;

import com.istratenkov.energyregistration.model.entity.Profile;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ConsumptionCheckSumValidationException extends RuntimeException {
    private static final String ERROR_MESSAGE = "For given profiles: [%s] Consumption for whole year is greater then 100%";
    private final Collection<Profile> failedProfiles;

    @Override
    public String getMessage() {
        String profilesString = failedProfiles.stream().map(Profile::getName).collect(Collectors.joining(", "));
        return String.format(ERROR_MESSAGE, profilesString);
    }
}
