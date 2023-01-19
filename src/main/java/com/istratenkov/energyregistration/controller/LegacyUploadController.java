package com.istratenkov.energyregistration.controller;

import com.istratenkov.energyregistration.exception.ConsumptionCheckSumValidationException;
import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.service.CSVParseService;
import com.istratenkov.energyregistration.service.FractionService;
import com.istratenkov.energyregistration.service.MeasurementService;
import com.istratenkov.energyregistration.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@RestController
@RequiredArgsConstructor
public class LegacyUploadController {

    private final CSVParseService csvParseService;
    private final FractionService fractionService;
    private final MeasurementService measurementService;
    private final ProfileService profileService;

    @PostMapping("/fractions")
    public ResponseEntity<Object> uploadFractions(@RequestBody MultipartFile file) throws DataFormatException {
        Map<Profile, List<Fraction>> profileFractionsParsed = csvParseService.parseFractionsFromFile(file);
        ValidationResultDto validationResultDto = fractionService.validateParsedFractions(profileFractionsParsed);
        if(!validationResultDto.getInvalidProfiles().isEmpty()) {
            throw new ConsumptionCheckSumValidationException(validationResultDto.getInvalidProfiles());
        }
        fractionService.saveFractions(profileFractionsParsed);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/measurements")
    public ResponseEntity<Object> uploadMeasurements(@RequestBody MultipartFile file) throws DataFormatException {
        Map<Profile, List<MeterMeasurement>> parsedMeasurements = csvParseService.parseMeterMeasurementsFromFile(file);
        ValidationResultDto validationResultDto = measurementService.validateParsedMeasurements(parsedMeasurements);
        List<Profile> validProfiles = validationResultDto.getValidProfiles();
        for (Profile profile : validProfiles) {
            profile.getMeasurements().forEach(e -> e.setProfile(profile));
        }
        measurementService.saveAll(validProfiles
                .stream().flatMap(e -> e.getMeasurements().stream()).collect(Collectors.toList()));
        profileService.saveAll(validProfiles);
        return ResponseEntity.ok().build();
    }
}
