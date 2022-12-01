package com.hilabs.rostertracker.controller;

import com.google.gson.Gson;
import com.hilabs.roster.entity.RAFileDetails;
import com.hilabs.roster.entity.RASheetDetails;
import com.hilabs.roster.repository.RAFileDetailsRepository;
import com.hilabs.roster.repository.RASheetDetailsRepository;
import com.hilabs.rostertracker.config.ApplicationConfig;
import com.hilabs.rostertracker.dto.*;
import com.hilabs.rostertracker.model.FileUploadResponse;
import com.hilabs.rostertracker.model.RestoreRosterRequest;
import com.hilabs.rostertracker.model.TargetPhaseType;
import com.hilabs.rostertracker.service.PythonInvocationService;
import com.hilabs.rostertracker.utils.LimitAndOffset;
import com.hilabs.rostertracker.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.hilabs.rostertracker.utils.SheetTypeUtils.allTypeList;

@RestController
@RequestMapping("/api/v1/roster")
@Log4j2
@CrossOrigin(origins = "*")
public class RosterController {
    public static Gson gson = new Gson();
    @Autowired
    private PythonInvocationService pythonInvocationService;
    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private RAFileDetailsRepository raFileDetailsRepository;
    @Autowired
    private RASheetDetailsRepository raSheetDetailsRepository;

    @PutMapping("/{rosterId}/restore")
    public ResponseEntity<Map<String, String>> restoreRoster(@RequestBody RestoreRosterRequest restoreRosterRequest, @PathVariable Long rosterId) {
        try {
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(rosterId);
            if (!optionalRAFileDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("raFileDetails not found with rosterId %s", rosterId));
            }
            TargetPhaseType targetPhaseType = TargetPhaseType.getTargetPhaseTypeFromStr(restoreRosterRequest.getTargetPhase());
            if (targetPhaseType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid targetPhase " + restoreRosterRequest.getTargetPhase());
            }
            File file = new File(applicationConfig.getRestoreWrapper());
            pythonInvocationService.invokePythonProcess(file.getPath(), "--envConfigs", applicationConfig.getEnvConfigs(),
                    "--fileDetailsId", "" + rosterId, "--targetPhase", targetPhaseType.name());
            //TODO return better response
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Error in restoreRoster restoreRosterRequest {} rosterId {}", gson.toJson(restoreRosterRequest), rosterId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        } catch (Exception ex) {
            log.error("Error in restoreRoster restoreRosterRequest {} rosterId {}", gson.toJson(restoreRosterRequest), rosterId);
            throw ex;
        }
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<Map<String, String>> updateStatus(@RequestBody List<UpdateStatusRequestElement> updateStatusRequestElementList) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            updateStatusRequest(updateStatusRequestElementList, username);
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateStatus updateStatusRequestElementList {} ex {}", gson.toJson(updateStatusRequestElementList),
                    ex.getMessage());
            throw ex;
        }
    }


    @GetMapping("/file-details")
    public ResponseEntity<CollectionResponse<RAFileDetails>> getFileDetailsList(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                                            @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                            @RequestParam(defaultValue = "-1") Long raFileDetailsId,
                                                                                            @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            if (raFileDetailsId != -1) {
                Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(raFileDetailsId);
                return optionalRAFileDetails.map(raFileDetails -> new ResponseEntity<>(new CollectionResponse<>(pageNo, pageSize, Collections.singletonList(raFileDetails), 1L), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(new CollectionResponse<>(pageNo, pageSize, Collections.emptyList(), 0L), HttpStatus.OK));
            }
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Utils.StartAndEndTime startAndEndTime = Utils.getAdjustedStartAndEndTime(-1, -1);
            long startTime = startAndEndTime.startTime;
            long endTime = startAndEndTime.endTime;
            Sort sort = Sort.by(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "creat_dt")));
            Page<RAFileDetails> raFileDetailsListPage = raFileDetailsRepository.findRAFileDetailsWithFilters(new ArrayList<>(), Collections.singletonList(plmTicketId),
                    new ArrayList<>(), new ArrayList<>(), new Date(startTime),
                    new Date(endTime), new ArrayList<>(), allTypeList, 0, new ArrayList<>(), PageRequest.of(pageNo, limit, sort));
            CollectionResponse collectionResponse = new CollectionResponse(pageNo, pageSize, raFileDetailsListPage.getContent(),
                    raFileDetailsListPage.getTotalElements());
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} plmTicketId {}", pageNo, pageSize, plmTicketId);
            throw ex;
        }
    }

    @PostMapping("/upload-roster")
    public ResponseEntity<FileUploadResponse> uploadRoster(@RequestParam("file") MultipartFile multipartFile,
                                                           @RequestParam("market") String market,
                                                           @RequestParam("lineOfBusiness") String lineOfBusiness,
                                                           @RequestParam("plmTicketId") String plmTicketId) throws IOException {
        if (multipartFile.getOriginalFilename() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OriginalFilename not found");
        }
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();
        saveFile(fileName, multipartFile, applicationConfig.getRaSourceFolder());
        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/sheet-details")
    public ResponseEntity<CollectionResponse<RASheetDetails>> getSheetDetailsList(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                                @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                  @RequestParam(defaultValue = "-1") Long raSheetDetailsId,
                                                                                @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Sort sort = Sort.by(Collections.singletonList(new Sort.Order(Sort.Direction.DESC, "creat_dt")));
            List<Long> raSheetDetailsIdList = raSheetDetailsId != -1 ? Collections.singletonList(raSheetDetailsId) : Collections.emptyList();
            Page<RASheetDetails> raSheetDetailsPage = raSheetDetailsRepository.findRASheetDetailsData(raSheetDetailsIdList,
                    Collections.singletonList(plmTicketId), PageRequest.of(pageNo, limit, sort));
            CollectionResponse<RASheetDetails> collectionResponse = new CollectionResponse<>(pageNo, pageSize,
                    raSheetDetailsPage.getContent(), raSheetDetailsPage.getTotalElements());
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getSheetDetailsList pageNo {} pageSize {} plmTicketId {}", pageNo, pageSize, plmTicketId);
            throw ex;
        }
    }

    @Transactional
    private void updateStatusRequest(List<UpdateStatusRequestElement> updateStatusRequestElementList, String username) {
        Date lastUpdateDate = new Date();
        for (UpdateStatusRequestElement updateStatusRequestElement : updateStatusRequestElementList) {
            Long raFileDetailsId = updateStatusRequestElement.getRaFileDetailsId();
            Optional<RAFileDetails> optionalRAFileDetails = raFileDetailsRepository.findById(raFileDetailsId);
            if (!optionalRAFileDetails.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("raFileDetails not found with raFileDetailsId %s", raFileDetailsId));
            }
            RAFileDetails raFileDetails = optionalRAFileDetails.get();
            raFileDetails.setLastUpdatedUserId(username);
            raFileDetails.setLastUpdatedDate(lastUpdateDate);
            raFileDetails.setStatusCode(updateStatusRequestElement.getStatusCode());
            raFileDetailsRepository.save(raFileDetails);
            for (SheetIdAndStatusInfo sheetIdAndStatusInfo : updateStatusRequestElement.getSheetStatsList()) {
                Long raSheetDetailsId = sheetIdAndStatusInfo.getRaSheetDetailsId();
                Optional<RASheetDetails> optionalRASheetDetails = raSheetDetailsRepository.findById(raSheetDetailsId);
                if (!optionalRASheetDetails.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("raSheetDetails not found with raSheetDetailsId %s", raSheetDetailsId));
                }
                RASheetDetails raSheetDetails = optionalRASheetDetails.get();
                raSheetDetails.setStatusCode(sheetIdAndStatusInfo.getStatusCode());
                raSheetDetails.setLastUpdatedUserId(username);
                raSheetDetails.setLastUpdatedDate(lastUpdateDate);
                raSheetDetailsRepository.save(raSheetDetails);
            }
        }
    }

    public static void saveFile(String fileName, MultipartFile multipartFile, String uploadFolder)
            throws IOException {
        Path uploadPath = Paths.get(uploadFolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }
    }
}
