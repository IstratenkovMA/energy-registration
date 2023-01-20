package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

public interface CSVParseService {
    Map<Profile, List<Fraction>> parseFractionsFromFile(MultipartFile file) throws DataFormatException;

    Map<Profile, List<MeterMeasurement>> parseMeterMeasurementsFromFile(MultipartFile file) throws DataFormatException;
}
