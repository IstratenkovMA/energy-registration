package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
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

@Slf4j
@Service
public class CSVParseService {

    public Map<Profile, List<Fraction>> parseFractionsFromFile(MultipartFile file)
            throws DataFormatException {
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
            throw new DataFormatException("file format is incorrect");
        }
        return parsedFractions;
    }

    public Map<Profile, List<MeterMeasurement>> parseMeterMeasurementsFromFile(MultipartFile file)
            throws DataFormatException {
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
        return parsedMeasurements;
    }
}
