-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE roster_user (
    user_id varchar(255) not null,
    pwd varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    active_flag int DEFAULT 1,
    created_date timestamp DEFAULT now(),
    created_user_id varchar(255),
    updated_date timestamp DEFAULT now(),
    updated_user_id int,
    UNIQUE(user_id)
)

-- changeset liquibase:2
CREATE TABLE roster_user_x_user_role (
    user_id varchar(255) not null,
    role_cd varchar(255),
    created_date timestamp DEFAULT now(),
    created_user_id varchar(255),
    updated_date timestamp DEFAULT now(),
    updated_user_id int,
    UNIQUE(user_id)
)

-- changeset liquibase:3
CREATE TABLE ra_prov_details (
  id int not null auto_increment,
  src_nm varchar(255) UNIQUE,
  market varchar(255),
  is_active int DEFAULT 1,
  lob varchar(255),
  creat_dt timestamp DEFAULT now(),
  last_updt_dt timestamp DEFAULT now(),
  creat_user_id varchar(255),
  last_updt_user_id varchar(255),
  PRIMARY KEY(id)
)

-- changeset liquibase:4
CREATE TABLE ra_file_details(
  id int not null auto_increment,
  ra_prov_details_id int,
  orgnl_file_nm varchar(255),
  stndrdzd_file_nm varchar(255),
  plm_ticket_id varchar(255),
  file_location varchar(1024),
  file_system varchar(1024),
  is_active int DEFAULT 1,
  creat_dt timestamp DEFAULT now(),
  last_updt_dt timestamp DEFAULT now(),
  creat_user_id varchar(255),
  last_updt_user_id varchar(255),
  PRIMARY KEY(id)
)

-- changeset liquibase:5
CREATE TABLE ra_sheet_details(
  id int not null auto_increment,
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
  creat_dt timestamp DEFAULT now(),
  last_updt_dt timestamp DEFAULT now(),
  creat_user_id varchar(255),
  last_updt_user_id varchar(255),
  PRIMARY KEY(id)
)

-- changeset liquibase:6
CREATE TABLE ra_conv_status_stage_mappings(
    id int not null auto_increment,
    prcssng_status varchar(255),
    stage varchar(255),
    status varchar(255),
    creat_dt timestamp DEFAULT now(),
    last_updt_dt timestamp DEFAULT now(),
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    PRIMARY KEY(id)
)

-- changeset liquibase:7
CREATE TABLE ra_conv_processing_duration_stats(
    id int not null auto_increment,
    ra_sheet_details_id int,
    status varchar(255),
    start_dt timestamp,
    cmpltn_dt timestamp,
    creat_dt timestamp DEFAULT now(),
    last_updt_dt timestamp DEFAULT now(),
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    PRIMARY KEY(id)
)

-- changeset liquibase:8
CREATE TABLE ra_fallout_report (
    id int not null auto_increment,
    ra_sheet_details_id int,
    ra_row_id varchar(255),
    rule_ctgry_stage varchar(255),
    err_type varchar(255),
    err_code varchar(255),
    err_dscrptn varchar(1024),
    trnsctn_type varchar(255),
    recommended_action varchar(1024),
    creat_dt timestamp DEFAULT now(),
    last_updt_dt timestamp DEFAULT now(),
    creat_user_id varchar(255),
    last_updt_user_id varchar(255),
    PRIMARY KEY(id)
)

