ALTER TABLE SIBS_PAYMENT_FILE_PROCESS_REPORT ADD COLUMN DEGREE_CHANGE_INDIVIDUAL_CANDIDACY_AMOUNT varchar(255);
ALTER TABLE SIBS_PAYMENT_FILE_PROCESS_REPORT ADD COLUMN DEGREE_TRANSFER_INDIVIDUAL_CANDIDACY_AMOUNT varchar(255);
ALTER TABLE SIBS_PAYMENT_FILE_PROCESS_REPORT ADD COLUMN SECOND_CYCLE_INDIVIDUAL_CANDIDACY_AMOUNT varchar(255);
ALTER TABLE SIBS_PAYMENT_FILE_PROCESS_REPORT ADD COLUMN DEGREE_CANDIDACY_FOR_GRADUATED_PERSON_AMOUNT varchar(255);

UPDATE SIBS_PAYMENT_FILE_PROCESS_REPORT SET DEGREE_CHANGE_INDIVIDUAL_CANDIDACY_AMOUNT = '0.0';
UPDATE SIBS_PAYMENT_FILE_PROCESS_REPORT SET DEGREE_TRANSFER_INDIVIDUAL_CANDIDACY_AMOUNT = '0.0';
UPDATE SIBS_PAYMENT_FILE_PROCESS_REPORT SET SECOND_CYCLE_INDIVIDUAL_CANDIDACY_AMOUNT = '0.0';
UPDATE SIBS_PAYMENT_FILE_PROCESS_REPORT SET DEGREE_CANDIDACY_FOR_GRADUATED_PERSON_AMOUNT = '0.0';