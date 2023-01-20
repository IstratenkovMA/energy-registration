package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.zip.DataFormatException;

public interface UploadService {
    ValidationResultDto uploadFractions(MultipartFile file) throws DataFormatException;

    ValidationResultDto uploadMeasurements(MultipartFile file) throws DataFormatException;
}
