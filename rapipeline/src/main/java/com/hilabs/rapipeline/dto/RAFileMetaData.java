package com.hilabs.rapipeline.dto;

import com.hilabs.roster.entity.RAPlmRoFileData;
import com.hilabs.roster.entity.RAPlmRoProfData;
import lombok.Data;

import java.util.Date;

@Data
public class RAFileMetaData  {
    Long raPlmRoProfDataId;
    String roId;
    String tCaseId;
    String racdId;
    String ractId;
    String racfId;
    int eId;
    String taxId;
    String orgName;
    String cntState;
    String plmNetwork;
    Date corporateReceiptDate;
    Long raPlmRoFileDataId;
    String fileName;
    String dcnId;
    String fileSize;
    String rAFileProcessingStatus;
    String reprocess;
    Date depositDate;
    String fileDocumentNumber;
    public RAFileMetaData(RAPlmRoProfData raPlmRoProfData, RAPlmRoFileData raPlmRoFileData) {
        this.raPlmRoProfDataId = raPlmRoProfData.getRaPlmRoProfDataId();
        this.roId = raPlmRoProfData.getRoId();
        this.tCaseId = raPlmRoProfData.getTCaseId();
        this.racdId = raPlmRoProfData.getRacdId();
        this.ractId = raPlmRoProfData.getRactId();
        this.racfId = raPlmRoProfData.getRacfId();
        this.eId = raPlmRoProfData.getEId();
        this.taxId = raPlmRoProfData.getTaxId();
        this.orgName = raPlmRoProfData.getOrgName();
        this.cntState = raPlmRoProfData.getCntState();
        this.plmNetwork = raPlmRoProfData.getPlmNetwork();
        this.corporateReceiptDate = raPlmRoProfData.getCorporateReceiptDate();
        this.raPlmRoFileDataId = raPlmRoFileData.getRaPlmRoFileDataId();
        this.fileName = raPlmRoFileData.getFileName();
        this.dcnId = raPlmRoFileData.getDcnId();
        this.fileSize = raPlmRoFileData.getFileSize();
        this.rAFileProcessingStatus = raPlmRoFileData.getRaFileProcessingStatus();
        this.reprocess = raPlmRoFileData.getReProcess();
        this.depositDate = raPlmRoFileData.getDepositDate();
        this.fileDocumentNumber = raPlmRoFileData.getFileDocumentNumber();
    }
}
