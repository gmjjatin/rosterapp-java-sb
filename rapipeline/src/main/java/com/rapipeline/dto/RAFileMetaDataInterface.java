package com.rapipeline.dto;

import java.util.Date;

public interface RAFileMetaDataInterface {
    Long getRAPlmRoProfDataId();
    String getRoId();
    String getTCaseId();
    String getRacdId();
    String getRactId();
    String getRacfId();
    int getEId();
    String getTaxId();
    String getOrgName();
    String getCntState();
    String getPlmNetwork();
    Date getCorporateReceiptDate();
    Long getRaPlmRoFileDataId();
    String getFileName();
    String getDcnId();
    String getFileSize();
    String getRAFileProcessingStatus();
    Date getDepositDate();
    String getFileDocumentNumber();
}
