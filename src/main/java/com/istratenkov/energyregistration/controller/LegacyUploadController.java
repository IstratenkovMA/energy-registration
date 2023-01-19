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
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;


/**
 * Legacy controller to support old api with csv file uploading.
 */
@RestController
@RequiredArgsConstructor
public class LegacyUploadController {

    private final CSVParseService csvParseService;
    private final FractionService fractionService;
    private final MeasurementService measurementService;
    private final ProfileService profileService;

    /**
     * Parse csv file with fractions and profile information.
     * Validates it for any mistakes in value of percentages.
     * If everything is ok data will be saved in database.
     * @param file file with data about fractions with profile name.
     * @return ok response if everything is processed correctly.
     * @throws DataFormatException is thrown if format of csv file is incorrect.
     */
    @Operation(summary = "Upload fractions")
    @PostMapping("/v1/fractions")
    public ResponseEntity<Object> uploadFractions(@RequestBody MultipartFile file) throws DataFormatException {
        Map<Profile, List<Fraction>> profileFractionsParsed = csvParseService.parseFractionsFromFile(file);
        ValidationResultDto validationResultDto = fractionService.validateParsedFractions(profileFractionsParsed);
        if(!validationResultDto.getInvalidProfiles().isEmpty()) {
            throw new ConsumptionCheckSumValidationException(validationResultDto.getInvalidProfiles());
        }
        fractionService.saveFractions(profileFractionsParsed);
        return ResponseEntity.ok().build();
    }

    /**
     * Parse csv file with measurements with meterId and profile information.
     * If data about fractions of the profile is absent in database error will be raised.
     * Validates it to be sure that meter measurements in next month is not lower than in previous.
     * Validates it for any mistakes in value of percentages of consumption.
     * If everything is ok data will be saved in database.
     * @param file file with data about measurements with profile name.
     * @return ok response if everything is processed correctly.
     * @throws DataFormatException is thrown if format of csv file is incorrect.
     */
    @PostMapping("/v1/measurements")
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
        return ResponseEntity.ok().build();//todo provide info about incorrect profiles
    }

    /**
     * Provides information about consumption of specific meter in specific month and year.
     * @param meterId meter id of meter that was sent to the system previously.
     * @param month month that consumption is need ot be calculated.
     * @param year year that data is needed for.
     * @return
     */
    @GetMapping("/v1/consumption")
    public ResponseEntity<Object> getConsumptionForMeter(@RequestParam("meterId") String meterId,
                                                         @RequestParam("month") String month,
                                                         @RequestParam("year") Integer year) {
        return ResponseEntity.ok(measurementService.getConsumptionForMeterByMonth(meterId, month, year));
    }

    /**
     * Don't allow any exception escape of our application for security reasons.
     * @param exception handled exception.
     * @return response entity with general server error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleStorageFileNotFound(RuntimeException exception) {
        return ResponseEntity.internalServerError().build();
    }
}
