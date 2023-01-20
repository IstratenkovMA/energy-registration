package com.istratenkov.energyregistration.controller;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.service.MeasurementServiceImpl;
import com.istratenkov.energyregistration.service.UploadServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;


/**
 * Legacy controller to support old api with csv file uploading.
 * Controller also connected to swagger and can be accessed through
 * your application {host:port}/swagger-ui/index.html#
 */
@RequestMapping("/v1")
@RestController
@Slf4j
@RequiredArgsConstructor
public class LegacyUploadController {

    private final MeasurementServiceImpl measurementService;
    private final UploadServiceImpl uploadService;

    /**
     * Parse csv file with fractions and profile information.
     * Validates it for any mistakes in value of percentages.
     * If everything is ok data will be saved in database.
     *
     * @param file file with data about fractions with profile name.
     * @return ok response if everything is processed correctly.
     * @throws DataFormatException is thrown if format of csv file is incorrect.
     */
    @Operation(summary = "Upload fractions from csv file.",
            description = "Uploads file using multipart form. Also contain profile data." +
                    " Parse data and validate it before saving.")
    @PostMapping(value = "/fractions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFractions(@RequestBody MultipartFile file)
            throws DataFormatException {
        ValidationResultDto validationResultDto = uploadService.uploadFractions(file);
        Set<Profile> invalidProfiles = validationResultDto.getInvalidProfiles();
        if(invalidProfiles.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(buildResponseForDataLoading(invalidProfiles));
    }

    /**
     * Parse csv file with measurements with meterId and profile information.
     * If data about fractions of the profile is absent in database error will be raised.
     * Validates it to be sure that meter measurements in next month is not lower than in previous.
     * Validates it for any mistakes in value of percentages of consumption.
     * If everything is ok data will be saved in database.
     *
     * @param file file with data about measurements with profile name.
     * @return ok response if everything is processed correctly.
     * @throws DataFormatException is thrown if format of csv file is incorrect.
     */
    @Operation(summary = "Upload meter measurements from csv file.",
            description = "Uploads file using multipart form. Also contain profile data with meterId" +
                    " connected to it. Parse data and validate it before saving.")
    @PostMapping(value = "/measurements", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadMeasurements(@RequestBody MultipartFile file) throws DataFormatException {
        ValidationResultDto validationResultDto = uploadService.uploadMeasurements(file);
        Set<Profile> invalidProfiles = validationResultDto.getInvalidProfiles();
        if (invalidProfiles.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(buildResponseForDataLoading(invalidProfiles));
    }

    /**
     * Provides information about consumption of specific meter in specific month and year.
     *
     * @param meterId meter id of meter that was sent to the system previously.
     * @param month   month that consumption is need ot be calculated.
     * @param year    year that data is needed for.
     * @return
     */
    @Operation(summary = "Get information about consumption of given meter in specified year and month.")
    @GetMapping("/consumption")
    public ResponseEntity<Object> getConsumptionForMeter(@RequestParam("meterId") String meterId,
                                                         @RequestParam("month") String month,
                                                         @RequestParam("year") Integer year) {
        return ResponseEntity.ok(measurementService.getConsumptionForMeterByMonth(meterId, month, year));
    }

    /**
     * Don't allow any exception escape of our application for security reasons.
     *
     * @param exception handled exception.
     * @return response entity with general server error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> lastBarrierExceptionHandler(Exception exception) {
        log.error("[lastBarrierExceptionHandler] Exception handled before used response. ", exception);
        return ResponseEntity.internalServerError().build();
    }

    private String buildResponseForDataLoading(Set<Profile> invalidProfiles) {
        return "Invalid profiles: " + invalidProfiles
                .stream().map(Profile::getName).collect(Collectors.joining(","));
    }
}
