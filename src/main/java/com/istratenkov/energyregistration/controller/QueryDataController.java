package com.istratenkov.energyregistration.controller;

import com.istratenkov.energyregistration.service.impl.MeasurementServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that used for read operations only, following CQRS segregation.
 * Controller also connected to swagger and can be accessed through
 * your application {host:port}/swagger-ui/index.html#
 */
@RequestMapping("/v1")
@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryDataController {

    private final MeasurementServiceImpl measurementService;
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
}
