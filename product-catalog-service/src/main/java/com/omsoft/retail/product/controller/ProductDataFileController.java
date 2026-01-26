package com.omsoft.retail.product.controller;

import com.omsoft.retail.product.entity.FileDetails;
import com.omsoft.retail.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products/file")
public class ProductDataFileController {
    private final JobLauncher jobLauncher;
    private final Job importProductsJob;
    private final ProductService service;

    public ProductDataFileController(JobLauncher jobLauncher, Job importProductsJob, ProductService service) {
        this.jobLauncher = jobLauncher;
        this.importProductsJob = importProductsJob;
        this.service = service;
    }

    @GetMapping("list")
    public List<FileDetails> getFileDetails() {
        return service.getFileRecords();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType) throws Exception {
        FileDetails details = service.saveFileDetails(file.getOriginalFilename(), fileType, "START");
        if (!service.runValidation(details)) {
            return "Upload file validation failed check file_details table for more details.";
        }
        // Save uploaded file to temp location
        Path tempFile = Files.createTempFile("batch-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());
        try {
            details.setStatus("RUNNING");
            service.updateFileDetails(details);
            // Pass file path to Spring Batch
            JobParameters params = new JobParametersBuilder()
                    .addString("filePath", tempFile.toString())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(importProductsJob, params);
            details.setStatus("SUCCESS");
            service.updateFileDetails(details);
        } catch (Exception ex) {
            String msg = "Error while running spring batch.. Error : " + ex.getMessage();
            log.error(msg);
            details.setDetail(msg);
            details.setStatus("ERROR");
            service.updateFileDetails(details);
        }
        return "Batch Started for file: " + file.getOriginalFilename();
    }
}

