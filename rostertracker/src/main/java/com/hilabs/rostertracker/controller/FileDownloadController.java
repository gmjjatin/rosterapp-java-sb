package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.rostertracker.config.RosterConfig;
import com.hilabs.rostertracker.service.RAFalloutReportService;
import com.hilabs.rostertracker.service.RAFileDetailsService;
import com.hilabs.rostertracker.service.RAFileStatsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/file-download")
@Log4j2
public class FileDownloadController {

    @Autowired
    RAFileStatsService raFileStatsService;

    @Autowired
    RAFileDetailsService raFileDetailsService;

    @Autowired
    RAFalloutReportService raFalloutReportService;

    @Autowired
    private RosterConfig rosterConfig;

    //TODO remove
    @RequestMapping(path = "/downloadRoster", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadSampleReport(@RequestParam() Long raFileDetailsId) throws IOException {
        try {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findRAFileDetailsById(raFileDetailsId);
            if (!optionalRAFileDetails.isPresent()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            File file = new File(rosterConfig.getRaArchiveFolder(), raFileDetails.getOriginalFileName());
            return getDownloadFileResponseEntity(file);
        } catch (Exception ex) {
            log.error("Error in downloadSampleReport - ex {}", ex.getMessage());
            throw ex;
        }
    }

    //
    public ResponseEntity<InputStreamResource> getDownloadFileResponseEntity(File file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
}