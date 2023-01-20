package com.istratenkov.energyregistration.service.impl;

import com.istratenkov.energyregistration.exception.ConsumptionCheckSumValidationException;
import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {
    private final CSVParseServiceImpl csvParseService;
    private final FractionServiceImpl fractionService;
    private final ProfileServiceImpl profileService;
    private final MeasurementServiceImpl measurementService;

    @Override
    public ValidationResultDto uploadFractions(MultipartFile file) throws DataFormatException {
        Map<Profile, List<Fraction>> profileFractionsParsed = csvParseService.parseFractionsFromFile(file);
        ValidationResultDto validationResultDto = fractionService.validateParsedFractions(profileFractionsParsed);
        Set<Profile> invalidProfiles = validationResultDto.getInvalidProfiles();
        if (!invalidProfiles.isEmpty()) {
            throw new ConsumptionCheckSumValidationException(invalidProfiles);
        }
        fractionService.saveFractions(profileFractionsParsed);
        return validationResultDto;
    }

    @Override
    public ValidationResultDto uploadMeasurements(MultipartFile file) throws DataFormatException {
        Map<Profile, List<MeterMeasurement>> parsedMeasurements = csvParseService.parseMeterMeasurementsFromFile(file);
        ValidationResultDto validationResultDto = measurementService.validateParsedMeasurements(parsedMeasurements);
        List<Profile> validProfiles = validationResultDto.getValidProfiles();
        for (Profile profile : validProfiles) {
            profile.getMeasurements().forEach(e -> e.setProfile(profile));
        }
        measurementService.saveAll(validProfiles
                .stream().flatMap(e -> e.getMeasurements().stream()).collect(Collectors.toList()));
        profileService.saveAll(validProfiles);
        return validationResultDto;
    }
}
