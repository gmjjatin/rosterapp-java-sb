insert into ra_conv_processing_duration_stats(ra_sheet_details_id, status, cmpltn_dt, start_dt) 
with p as (
    select 1, 'NOT_YET_STARTED', sysdate, sysdate - interval '1' hour from dual union all
    select 5, 'READY_FOR_SUMMARY', sysdate, sysdate - interval '1' hour from dual union all
    select 8, 'HEADER_MAPPING_FINISHED', sysdate, sysdate - interval '1' hour from dual
)
select * from p;

insert into ra_conv_status_stage_mappings(prcssng_status, stage, status) 
with p as (
select 'NOT_YET_STARTED',   'Roster Received',  'Final' from dual union all
select 'RUNNING_HEADER_MAPPING', 'Auto Mapped', 'Intermediate' from dual union all
select 'HEADER_MAPPING_FINISHED',   'Auto Mapped', 'Final' from dual union all
select 'READY_FOR_ROSTER_ISF_CONVERSION',    'Converted DART',  'Intermediate' from dual union all
select 'CONVERTING_ROSTER_ISF', 'Converted DART',   'Intermediate' from dual union all
select 'READY_FOR_ISF_DART_CONVERSION',  'Converted DART',  'Intermediate' from dual union all
select 'CONVERTING_ISF_DART',    'Converted DART',  'Final' from dual union all
select 'READY_FOR_DART_UI', 'SPS load', 'Intermediate' from dual union all
select 'PROCESSING_IN_DART_UI', 'SPS load', 'Intermediate' from dual union all
select 'READY_FOR_SPS_LOAD',    'SPS load', 'Intermediate' from dual union all
select 'PROCESSING_IN_SPS_LOAD',    'SPS load', 'Final' from dual union all
select 'READY_FOR_SUMMARY', 'Report',   'Intermediate' from dual union all
select 'CREATING_SUMMARY',  'Report',   'Intermediate' from dual union all
select 'SUCCEEDED', 'Report',   'Final' from dual union all
select 'FAILED',    NULL, NULL from dual)
select * from p;

insert into ra_prov_details(src_nm, market, lob) with p as (
  select 'Wellstar',   'GA',   'Commercial, Medicare' from dual union all
  select 'Carilion', 'VA',    'Commercial' from dual union all
  select 'MHS',   'WA',   'Medicaid' from dual)
  select * from p;

insert into ra_sheet_details(ra_file_details_id, status, name, type, roster_record_cnt, auto_mapped_record_cnt, dart_record_cnt, dart_row_cnt, successful_record_cnt, manual_review_record_cnt, sps_load_trnsctn_cnt, sps_load_success_trnsctn_cnt) with p as (
    select 1, 'READY_FOR_DART_UI', 'Adds', '', 72, 71, 60, 200, 50, 0, 300, 280 from dual union all
    select 1, 'READY_FOR_DART_UI', 'Updates', '', 72, 71, 60, 200, 50, 0, 300, 280 from dual union all
    select 1, 'READY_FOR_DART_UI', 'Terms', '', 72, 0, 0, 0, 0, 0, 0, 0 from dual union all
    select 2, 'READY_FOR_DART_UI', 'Adds', '', 72, 71, 60, 200, 50, 0, 300, 280 from dual union all
    select 2, 'READY_FOR_DART_UI', 'Updates', '', 72, 71, 60, 200, 50, 0, 300, 280 from dual union all
    select 2, 'READY_FOR_DART_UI', 'Terms', '', 72, 0, 0, 0, 0, 0, 0, 0 from dual union all
    select 3, 'READY_FOR_DART_UI', 'Adds', '', 72, 71, 60, 200, 50, 0, 300, 280 from dual union all
    select 3, 'READY_FOR_DART_UI', 'Updates', '', 72, 71, 60, 200, 50, 0, 300, 280 from dual union all
    select 3, 'READY_FOR_DART_UI', 'Terms', '', 72, 0, 0, 0, 0, 0, 0, 0 from dual
)
select * from p;

insert into ra_file_details(ra_prov_details_id, orgnl_file_nm, stndrdzd_file_nm, plm_ticket_id, file_location, file_system) values (1, '7. JULY 2022 BCBS ENROLLMENT REQUEST - WORKING DOCUMENT FINAL', '7. JULY 2022 BCBS ENROLLMENT REQUEST - WORKING DOCUMENT FINAL', 'T-1256734', '', '');
insert into ra_file_details(ra_prov_details_id, orgnl_file_nm, stndrdzd_file_nm, plm_ticket_id, file_location, file_system) values (2, 'Carilion Anthem July SS', 'Carilion Anthem July SS', 'P-3789124', '', '');
insert into ra_file_details(ra_prov_details_id, orgnl_file_nm, stndrdzd_file_nm, plm_ticket_id, file_location, file_system) values (3, 'MCO - BH SR Version - 7-15-2022', 'MCO - BH SR Version - 7-15-2022', 'T-7685431', '', '');

-- insert into ra_file_details(ra_prov_details_id, orgnl_file_nm, stndrdzd_file_nm, plm_ticket_id, file_location, file_system) with p as (
--     select 1, '7. JULY 2022 BCBS ENROLLMENT REQUEST - WORKING DOCUMENT FINAL', '7. JULY 2022 BCBS ENROLLMENT REQUEST - WORKING DOCUMENT FINAL', 'T-1256734', '', '' from dual union all
--     select 2, 'Carilion Anthem July SS', 'Carilion Anthem July SS', 'P-3789124', '', '' from dual union all
--     select 3, 'MCO - BH SR Version - 7-15-2022', 'MCO - BH SR Version - 7-15-2022', 'T-7685431', '', '' from dual
--  )
--  select * from p;