package com.hilabs.rostertracker.controller;

import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.rostertracker.config.RosterConfig;
import com.hilabs.rostertracker.service.RAFalloutReportService;
import com.hilabs.rostertracker.service.RAFileDetailsService;
import com.hilabs.rostertracker.service.RAFileStatsService;
import com.hilabs.rostertracker.service.RASheetDetailsService;
import liquibase.repackaged.org.apache.commons.lang3.exception.ExceptionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

import static com.hilabs.rostertracker.utils.Utils.removeFileExtensionFromExcelFile;


@RestController
@RequestMapping("/api/v1/file-download")
@Log4j2
@CrossOrigin(origins = "*")
public class FileDownloadController {

    @Autowired
    RAFileStatsService raFileStatsService;

    @Autowired
    RAFileDetailsService raFileDetailsService;

    @Autowired
    RASheetDetailsService raSheetDetailsService;

    @Autowired
    RAFalloutReportService raFalloutReportService;

    @Autowired
    private RosterConfig rosterConfig;

    //TODO remove
    @RequestMapping(path = "/download-roster", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadRoster(@RequestParam() Long raFileDetailsId) throws IOException {
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

    @RequestMapping(path = "/download-sheet-report", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadSheetReport(@RequestParam() Long raSheetDetailsId) throws IOException {
        try {
            Optional<RASheetDetails> optionalRASheetDetails = raSheetDetailsService.findRASheetDetailsById(raSheetDetailsId);
            if (!optionalRASheetDetails.isPresent()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            RASheetDetails raSheetDetails = optionalRASheetDetails.get();
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsService.findRAFileDetailsById(raSheetDetails.getRaFileDetailsId());
            if (!optionalRAFileDetails.isPresent()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            String standardizedFileName = raFileDetails.getStandardizedFileName();
            if (standardizedFileName != null && standardizedFileName.endsWith(".xlsx")) {
                standardizedFileName = removeFileExtensionFromExcelFile(raFileDetails.getStandardizedFileName());
            }
            String trackerFileName = String.format("%s_%s_Tracker.xlsx", standardizedFileName, raSheetDetails.getId());
            File file = new File(rosterConfig.getRaTrackerFileFolder(), trackerFileName);
            return getDownloadFileResponseEntity(file);
        } catch (Exception ex) {
            log.error("Error in download sheet report - ex {} stackTrace {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }

    @RequestMapping(path = "/download-dart-report", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadDartReport(@RequestParam() Long raSheetDetailsId) throws IOException {
        try {
            Optional<RASheetDetails> optionalRASheetDetails = raSheetDetailsService.findRASheetDetailsById(raSheetDetailsId);
            if (!optionalRASheetDetails.isPresent()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            RASheetDetails raSheetDetails = optionalRASheetDetails.get();
            String outFileName = raSheetDetails.getOutFileName();
            File file = null;
            if (outFileName != null && outFileName.contains("/")) {
                file = new File(raSheetDetails.getOutFileName());
            } else {
                file = new File(rosterConfig.getDartFileFolder(), raSheetDetails.getOutFileName());
            }
            return getDownloadFileResponseEntity(file);
        }  catch (NoSuchFileException ex) {
            log.error("NoSuchFileException Error in download sheet report - ex {} stackTrace {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        } catch (Exception ex) {
            log.error("Error in download sheet report - ex {} stackTrace {}", ex.getMessage(), ExceptionUtils.getStackTrace(ex));
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