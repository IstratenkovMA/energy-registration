package com.istratenkov.energyregistration.exception;

import lombok.RequiredArgsConstructor;

/**
 * Exception raised if profile that data must had been presented in database wasn't found.
 */
@RequiredArgsConstructor
public class ProfileDataNotFoundInDBException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Data for %s profile is absent in DB!";
    private final String profileName;

    @Override
    public String getMessage() {
        return String.format(ERROR_MESSAGE, profileName);
    }
}
