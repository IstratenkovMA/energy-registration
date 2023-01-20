package com.istratenkov.energyregistration.service.impl;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import com.istratenkov.energyregistration.service.CSVParseService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * Class that provide csv file paring functionality.
 * OpenCSV library used for file parsing.
 */
@Slf4j
@Service
public class CSVParseServiceImpl implements CSVParseService {

    /**
     * Parse csv of fraction file. (examples of format can be found in test/resources)
     *
     * @param file multipart file with data about fractions of profiles.
     * @return Map of profile and it's list parsed fractions.
     * @throws DataFormatException in case of file has incorrect format.
     */
    public Map<Profile, List<Fraction>> parseFractionsFromFile(MultipartFile file)
            throws DataFormatException {
        log.info("[parseFractionsFromFile] File parsing started file: {}", file.getName());
        Map<Profile, List<Fraction>> parsedFractions = new HashMap<>();
        int year = Year.now().getValue();
        try (InputStream is = file.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVReader csvReader = new CSVReader(br)) {
            csvReader.skip(1); //skip header
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String month = line[0];
                String profileName = line[1];
                float value = Float.parseFloat(line[2]);
                Profile profile = new Profile();
                profile.setName(profileName);
                Fraction fraction = new Fraction();
                fraction.setMonth(Month.valueOf(month));
                fraction.setValue(value);
                fraction.setYear(year);
                parsedFractions.computeIfAbsent(profile, p -> new ArrayList<>());
                parsedFractions.get(profile).add(fraction);
            }
        } catch (IOException | CsvValidationException e) {
            log.error(e.getMessage());
            throw new DataFormatException("File format is incorrect!");
        }
        log.info("[parseFractionsFromFile] File parsing finished file: {}", file.getName());
        return parsedFractions;
    }

    /**
     * Parse csv file with information about measurements of a given meter.
     * (examples of format can be found in test/resources)
     *
     * @param file file with information about profile with meterId and it's measurements for 12 months.
     * @return Map of profile and it's list parsed measurements.
     * @throws DataFormatException in case of file has incorrect format.
     */
    public Map<Profile, List<MeterMeasurement>> parseMeterMeasurementsFromFile(MultipartFile file)
            throws DataFormatException {
        log.info("[parseMeterMeasurementsFromFile] File parsing started file: {}", file.getName());
        Map<Profile, List<MeterMeasurement>> parsedMeasurements = new HashMap<>();
        int year = Year.now().getValue();
        try (InputStream is = file.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVReader csvReader = new CSVReader(br)) {
            csvReader.skip(1); //skip header
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String meterId = line[0];
                String profileName = line[1];
                String month = line[2];
                Integer value = Integer.parseInt(line[3]);

                Profile profile = new Profile();
                profile.setMeterId(meterId);
                profile.setName(profileName);

                MeterMeasurement measurement = new MeterMeasurement();
                measurement.setMonth(Month.valueOf(month));
                measurement.setValue(value);
                measurement.setYear(year);

                parsedMeasurements.computeIfAbsent(profile, p -> new ArrayList<>());
                parsedMeasurements.get(profile).add(measurement);
            }
        } catch (IOException | CsvValidationException e) {
            log.error(e.getMessage());
            throw new DataFormatException("file format is incorrect");
        }
        log.info("[parseMeterMeasurementsFromFile] File parsing finished file: {}", file.getName());
        return parsedMeasurements;
    }
}
