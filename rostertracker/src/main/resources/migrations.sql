-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE RA_RT_PROVIDER_DETAILS (
  id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
  source_nm varchar,
  is_active NUMBER(1),
  creat_dt TIMESTAMP,
  creat_user_id VARCHAR2,
  last_updt_dt TIMESTAMP,
  last_updt_user_id VARCHAR2
);

-- changeset liquibase:2
CREATE TABLE RA_RT_PROV_MARKET_LOB_MAP (
  id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
  ra_prov_details_id Number,
  market varchar2,
  lob varchar2,
  is_active Number(1),
  creat_dt TIMESTAMP,
  creat_user_id varchar2,
  last_updt_dt TIMESTAMP,
  last_updt_user_id varchar2
);


-- changeset liquibase:3
CREATE TABLE roster_user (
    user_id varchar(255) not null,
    pwd varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    active_flag int DEFAULT 1,
    created_date timestamp DEFAULT sysdate,
    created_user_id varchar(255),
    updated_date timestamp DEFAULT sysdate,
    updated_user_id int,
    UNIQUE(user_id)
);

-- changeset liquibase:4
CREATE TABLE roster_user_x_user_role (
    user_id varchar(255) not null,
    role_cd varchar(255),
    created_date timestamp DEFAULT sysdate,
    created_user_id varchar(255),
    updated_date timestamp DEFAULT sysdate,
    updated_user_id int,
    UNIQUE(user_id)
);

-- changeset liquibase:5
CREATE TABLE ra_prov_details (
  id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
  src_nm varchar(255) UNIQUE,
  market varchar(255),
  is_active int DEFAULT 1,
  lob varchar(255),
  creat_dt timestamp DEFAULT sysdate,
  last_updt_dt timestamp DEFAULT sysdate,
  creat_user_id varchar(255),
  last_updt_user_id varchar(255),
  PRIMARY KEY(id)
);

-- changeset liquibase:4
CREATE TABLE ra_file_details(
  id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
  ra_prov_details_id int,
  market varchar(255),
  lob varchar(255),
  orgnl_file_nm varchar(255),
  stndrdzd_file_nm varchar(255),
  plm_ticket_id varchar(255),
  file_location varchar(1024),
  file_system varchar(1024),
  is_active int DEFAULT 1,
  is_compatible int DEFAULT 0,
  creat_dt timestamp DEFAULT sysdate,
  last_updt_dt timestamp DEFAULT sysdate,
  creat_user_id varchar(255),
  last_updt_user_id varchar(255),
  PRIMARY KEY(id)
);

-- changeset liquibase:5
CREATE TABLE ra_sheet_details(
  id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
  ra_file_details_id int,
  status varchar(255),
  name varchar(255),
  type varchar(255),
  roster_record_cnt int,
  auto_mapped_record_cnt int,
  dart_record_cnt int,
  dart_row_cnt int,
  successful_record_cnt int,
  manual_review_record_cnt int,
  sps_load_trnsctn_cnt int,
  sps_load_success_trnsctn_cnt int,
  creat_dt timestamp DEFAULT sysdate,
  last_updt_dt timestamp DEFAULT sysdate,
  creat_user_id varchar(255),
  last_updt_user_id varchar(255),
  PRIMARY KEY(id)
);

-- changeset liquibase:6
CREATE TABLE ra_conv_status_stage_mappings(
    id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    prcssng_status varchar(255),
    stage varchar(255),
    status varchar(255),
    creat_dt timestamp DEFAULT sysdate,
    last_updt_dt timestamp DEFAULT sysdate,
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    PRIMARY KEY(id)
);

-- changeset liquibase:7
CREATE TABLE ra_conv_processing_duration_stats(
    id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    ra_sheet_details_id int,
    status varchar(255),
    start_dt timestamp,
    cmpltn_dt timestamp,
    creat_dt timestamp DEFAULT sysdate,
    last_updt_dt timestamp DEFAULT sysdate,
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    PRIMARY KEY(id)
);

-- changeset liquibase:8
CREATE TABLE ra_fallout_report (
    id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    ra_sheet_details_id int,
    ra_row_id varchar(255),
    rule_ctgry_stage varchar(255),
    err_type varchar(255),
    err_code varchar(255),
    err_dscrptn varchar(1024),
    trnsctn_type varchar(255),
    recommended_action varchar(1024),
    creat_dt timestamp DEFAULT sysdate,
    last_updt_dt timestamp DEFAULT sysdate,
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    PRIMARY KEY(id)
);

-- changeset liquibase:8
create index ra_fallout_report_sheet_id on ra_fallout_report(ra_sheet_details_id);

-- changeset liquibase:9
CREATE TABLE RA_PLM_RO_PROF_DATA(
    RA_PLM_RO_PROF_DATA_ID NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    RO_ID VARCHAR2(32),
    T_CASE_ID VARCHAR2(32),
    RACD_ID VARCHAR2(32),
    RACT_ID VARCHAR2(32),
    RACF_ID VARCHAR2(32),
    EID NUMBER,
    TAX_ID VARCHAR2(9),
    ORG_NM VARCHAR2(32),
    CNT_STATE VARCHAR2(32),
    PLM_NTWK VARCHAR2(32),
    CORP_RECIPT_DT TIMESTAMP(6),
    CREAT_DT TIMESTAMP(6),
    CREAT_USER_ID VARCHAR2(10),
    LAST_UPDT_DT TIMESTAMP(6),
    LAST_UPDT_USER_ID VARCHAR2(10)
);

-- changeset liquibase:10
CREATE TABLE RA_PLM_RO_FILE_DATA(
    RA_PLM_RO_FILE_DATA_ID NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    RA_PLM_RO_PROF_DATA_ID NUMBER,
    FILE_NM VARCHAR2(255),
    DCN_ID VARCHAR2(50),
    FILE_SIZE VARCHAR2(50),
    RA_FILE_PRCS_STTS VARCHAR2(20),
    REPROCESS NUMBER,
    DEPOSIT_DT TIMESTAMP(6),
    F_DOCNUM VARCHAR2(50),
    CREAT_DT TIMESTAMP(6),
    CREAT_USER_ID VARCHAR2(10),
    LAST_UPDT_DT TIMESTAMP(6),
    LAST_UPDT_USER_ID VARCHAR2(10)
);

-- changeset liquibase:11
CREATE TABLE ra_file_x_status (
    id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    ra_file_details_id int,
    status_code int,
    creat_dt timestamp DEFAULT sysdate,
    last_updt_dt timestamp DEFAULT sysdate,
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    UNIQUE(ra_file_details_id),
    PRIMARY KEY(id)
)

-- changeset liquibase:12
CREATE TABLE ra_sheet_x_status (
    id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    ra_sheet_details_id int,
    status_code int,
    creat_dt timestamp DEFAULT sysdate,
    last_updt_dt timestamp DEFAULT sysdate,
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    UNIQUE(ra_sheet_details_id),
    PRIMARY KEY(id)
)

-- changeset liquibase:13
CREATE TABLE pipeline_status (
    id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    status_code int,
    dscrptn varchar(255),
    stage varchar(255),
    creat_dt timestamp DEFAULT sysdate,
    last_updt_dt timestamp DEFAULT sysdate,
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    UNIQUE(status_code)
)

-- changeset liquibase:14
CREATE TABLE ra_system_errors (
    id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
    ra_file_details_id int,
    last_stage varchar(255),
    last_status varchar(255),
    error_category varchar(255),
    error_description varchar(255),
    error_stack_trace varchar(1024),
    creat_dt timestamp DEFAULT sysdate,
    last_updt_dt timestamp DEFAULT sysdate,
    creat_user_id varchar(255),
    last_updt_user_id varchar(255)
);
