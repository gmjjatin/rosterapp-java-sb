package com.hilabs.rostertracker.controller;

import com.google.gson.Gson;
import com.hilabs.roster.dto.RAFileMetaData;
import com.hilabs.roster.entity.*;
import com.hilabs.roster.repository.*;
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
import org.springframework.http.MediaType;
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
import java.util.stream.Collectors;

import static com.hilabs.rostertracker.utils.SheetTypeUtils.allTypeList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

    @Autowired
    private RAPlmRoProfDataRepository raPlmRoProfDataRepository;

    @Autowired
    private RAPlmRoFileDataRepository raPlmRoFileDataRepository;

    @Autowired
    private RARCRosterISFMapRepository rarcRosterISFMapRepository;

    @Autowired
    private RAFileErrorCodeDetailRepository raFileErrorCodeDetailRepository;

    @Autowired
    private RASheetErrorCodeDetailRepository raSheetErrorCodeDetailRepository;

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
                                                                                            @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Sort sort = Sort.by(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "creat_dt")));
            Page<RAFileDetails> raFileDetailsListPage = raFileDetailsRepository.findRAFileDetailsWithData(Collections.emptyList(),
                    Collections.singletonList(plmTicketId), PageRequest.of(pageNo, limit, sort));
            CollectionResponse collectionResponse = new CollectionResponse(pageNo, pageSize, raFileDetailsListPage.getContent(),
                    raFileDetailsListPage.getTotalElements());
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getRAProvAndStatsList pageNo {} pageSize {} plmTicketId {}", pageNo, pageSize, plmTicketId);
            throw ex;
        }
    }

    @GetMapping("/sheet-details")
    public ResponseEntity<CollectionResponse<RASheetDetails>> getSheetDetailsList(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                                @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Sort sort = Sort.by(Collections.singletonList(new Sort.Order(Sort.Direction.DESC, "creat_dt")));
            List<Long> raSheetDetailsIdList = Collections.emptyList();
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

    @GetMapping("/file-meta-data")
    public ResponseEntity<CollectionResponse<RAFileMetaData>> getFileMetaDataList(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                                  @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                  @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Sort sort = Sort.by(Collections.singletonList(new Sort.Order(Sort.Direction.DESC, "creat_dt")));
            Page<RAPlmRoFileData> raPlmRoFileDataPage =  raPlmRoFileDataRepository.findRAPlmRoFileDataList(plmTicketId, PageRequest.of(pageNo, limit, sort));
            List<RAPlmRoProfData> raPlmRoProfDataList = raPlmRoProfDataRepository.findRAPlmRoProfDataByIds(raPlmRoFileDataPage.getContent().stream().map(RAPlmRoFileData::getRaPlmRoProfDataId).collect(Collectors.toList()), PageRequest.of(pageNo, limit, sort));
            Map<Long, RAPlmRoProfData> raPlmRoProfDataMap = new HashMap<>();
            for (RAPlmRoProfData raPlmRoProfData : raPlmRoProfDataList) {
                raPlmRoProfDataMap.put(raPlmRoProfData.getRaPlmRoProfDataId(), raPlmRoProfData);
            }

            List<RAFileMetaData> raFileMetaDataList = new ArrayList<>();
            for (RAPlmRoFileData raPlmRoFileData : raPlmRoFileDataPage.getContent()) {
                raFileMetaDataList.add(new RAFileMetaData(raPlmRoProfDataMap.getOrDefault(raPlmRoFileData.getRaPlmRoProfDataId(), null),
                        raPlmRoFileData));
            }
            CollectionResponse<RAFileMetaData> raFileMetaDataCollectionResponse = new CollectionResponse<>(pageNo, pageSize, raFileMetaDataList, raPlmRoFileDataPage.getTotalElements());
            return new ResponseEntity<CollectionResponse<RAFileMetaData>>(raFileMetaDataCollectionResponse, HttpStatus.OK);
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

    @GetMapping("/mappings")
    public ResponseEntity<CollectionResponse<RARCRosterISFMap>> getMappingsList(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                                  @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                  @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "RA_RT_SHEET_DETAILS.id"),
                    new Sort.Order(Sort.Direction.DESC, "RA_RC_ROSTER_ISF_MAP.ROSTER_COLUMN_NM"));
            Page<RARCRosterISFMap> rarcRosterISFMapPage = rarcRosterISFMapRepository.findRARCRosterISFMapData(Collections.singletonList(plmTicketId), PageRequest.of(pageNo, limit, sort));
            CollectionResponse<RARCRosterISFMap> collectionResponse = new CollectionResponse<>(pageNo, pageSize,
                    rarcRosterISFMapPage.getContent(), rarcRosterISFMapPage.getTotalElements());
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getMappingsList pageNo {} pageSize {} plmTicketId {}", pageNo, pageSize, plmTicketId);
            throw ex;
        }
    }

    @GetMapping("/file-error-code-details")
    public ResponseEntity<CollectionResponse<RAFileErrorCodeDetails>> getRAFileErrorCodeDetailsList(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                                @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "RA_RT_FILE_DETAILS.id"));
            Page<RAFileErrorCodeDetails> raFileErrorCodeDetailsPage = raFileErrorCodeDetailRepository.findRAFileErrorCodeDetailsData(Collections.singletonList(plmTicketId),
                    PageRequest.of(pageNo, limit, sort));
            CollectionResponse<RAFileErrorCodeDetails> collectionResponse = new CollectionResponse<>(pageNo, pageSize,
                    raFileErrorCodeDetailsPage.getContent(), raFileErrorCodeDetailsPage.getTotalElements());
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getMappingsList pageNo {} pageSize {} plmTicketId {}", pageNo, pageSize, plmTicketId);
            throw ex;
        }
    }

    @GetMapping("/sheet-error-code-details")
    public ResponseEntity<CollectionResponse<RASheetErrorCodeDetails>> getRASheetErrorCodeDetailsList(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                                                    @RequestParam(defaultValue = "100") Integer pageSize,
                                                                                                    @RequestParam(defaultValue = "") String plmTicketId) {
        try {
            LimitAndOffset limitAndOffset = Utils.getLimitAndOffsetFromPageInfo(pageNo, pageSize);
            int limit = limitAndOffset.getLimit();
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "RA_RT_SHEET_DETAILS.id"));
            Page<RASheetErrorCodeDetails> raSheetErrorCodeDetailsPage = raSheetErrorCodeDetailRepository.findRASheetErrorCodeDetailsData(Collections.singletonList(plmTicketId),
                    PageRequest.of(pageNo, limit, sort));
            CollectionResponse<RASheetErrorCodeDetails> collectionResponse = new CollectionResponse<>(pageNo, pageSize,
                    raSheetErrorCodeDetailsPage.getContent(), raSheetErrorCodeDetailsPage.getTotalElements());
            return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getMappingsList pageNo {} pageSize {} plmTicketId {}", pageNo, pageSize, plmTicketId);
            throw ex;
        }
    }

    @RequestMapping(path = "/upload-roster", method = POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<FileUploadResponse> uploadRoster(@RequestParam("file") MultipartFile multipartFile,
                                                           @RequestParam("market") String market,
                                                           @RequestParam("lineOfBusiness") String lineOfBusiness,
                                                           @RequestParam("plmTicketId") String plmTicketId) throws IOException {
        try {
            if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OriginalFilename not found");
            }
            if (market == null || lineOfBusiness == null || plmTicketId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "market or lineOfBusiness or plmTicketId not found");
            }
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            long size = multipartFile.getSize();
            saveFile(fileName, multipartFile, applicationConfig.getRaSourceFolder());
            FileUploadResponse response = new FileUploadResponse();
            response.setFileName(fileName);
            response.setSize(size);
            RAPlmRoProfData raPlmRoProfData = new RAPlmRoProfData();
            raPlmRoProfData.setRoId(plmTicketId);
            raPlmRoProfData.setCntState(market);
            raPlmRoProfData.setLob(lineOfBusiness);
            raPlmRoProfData = raPlmRoProfDataRepository.save(raPlmRoProfData);
            RAPlmRoFileData raPlmRoFileData = new RAPlmRoFileData();
            raPlmRoFileData.setRaPlmRoProfDataId(raPlmRoProfData.getRaPlmRoProfDataId());
            raPlmRoFileData.setFileName(fileName);
            raPlmRoFileDataRepository.save(raPlmRoFileData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in uploadRoster market {} lineOfBusiness {} plmTicketId {}", market, lineOfBusiness, plmTicketId);
            throw ex;
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
