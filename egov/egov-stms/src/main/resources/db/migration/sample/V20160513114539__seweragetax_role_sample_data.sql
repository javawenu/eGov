---------------Create new Role--------------------
INSERT INTO eg_role(id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (nextval('seq_eg_role'), 'Sewerage Tax Administrator', 'Sewerage Tax Administrator', now(), 1, 1, now(), 0);

INSERT INTO eg_role(id, name, description, createddate, createdby, lastmodifiedby, lastmodifieddate, version) VALUES (nextval('seq_eg_role'), 'Sewerage Tax Creator', 'Sewerage Tax Creator', now(), 1, 1, now(), 0);

------ Sewerage application status ------------------------------------

Delete from egw_status where MODULETYPE='SEWERAGETAXAPPLICATION';

Insert into egw_status (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Created',now(),'CREATED',1);
Insert into egw_status (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Inspection Fee Paid',now(),'INSPECTIONFEEPAID',2);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Field Inspected',now(),'FIELDINSPECTED',3);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Initial Approved',now(),'INITIALAPPROVED',4);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Estimation Notice Generated',now(),'ESTIMATIONNOTICEGENERATED',5);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Estimation Amount Paid',now(),'ESTIMATIONAMOUNTPAID',6);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Final Approved',now(),'FINALAPPROVED',7);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Work Order Generated',now(),'WORKORDERGENERATED',8);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Sanctioned',now(),'SANCTIONED',9);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Cancelled',now(),'CANCELLED',10);
Insert into EGW_STATUS (ID,MODULETYPE,DESCRIPTION,LASTMODIFIEDDATE,CODE,ORDER_ID) values (nextval('SEQ_EGW_STATUS'),'SEWERAGETAXAPPLICATION','Rejected',now(),'REJECTED',11);


------ Document type master sample data ----------------------

insert into egswtax_document_type_master(id,description,isactive,applicationtype,ismandatory,version) values (nextval('seq_egswtax_document_type_master'),'Others','t',(select id from egswtax_application_type where code='NEWSEWERAGECONNECTION' and active='t'),'f',0);
insert into egswtax_document_type_master(id,description,isactive,applicationtype,ismandatory,version) values (nextval('seq_egswtax_document_type_master'),'Property Tax Paid Receipt','t',(select id from egswtax_application_type where code='NEWSEWERAGECONNECTION' and active='t'),'f',0);
insert into egswtax_document_type_master(id,description,isactive,applicationtype,ismandatory,version) values (nextval('seq_egswtax_document_type_master'),'Water Tax Paid Receipt','t',(select id from egswtax_application_type where code='NEWSEWERAGECONNECTION' and active='t'),'f',0);


------ Fees Master sample data ----------------------

insert into egswtax_fees_master(id,description,code,applicationtype,createddate,createdby,lastmodifieddate,lastmodifiedby,version) values (nextval('seq_egswtax_fees_master'),'Inspection Charges','INSPECTIONCHARGES',(select id from egswtax_application_type where code='NEWSEWERAGECONNECTION' and active='t'),now(),1,now(),1,0);

insert into egswtax_fees_master(id,description,code,applicationtype,createddate,createdby,lastmodifieddate,lastmodifiedby,version) values (nextval('seq_egswtax_fees_master'),'Sewerage Tax','SEWERAGETAX',(select id from egswtax_application_type where code='NEWSEWERAGECONNECTION' and active='t'),now(),1,now(),1,0);


------ Fees Detail Master sample data ----------------------

insert into egswtax_feesdetail_master(id,description,code,fees,ismandatory,isactive,createddate,createdby,lastmodifieddate,lastmodifiedby,version) values (nextval('seq_egswtax_feesdetail_master'),'Inspection Charges','INSPECTIONCHARGES',(select id from egswtax_fees_master where code='INSPECTIONCHARGES'),'f','t',now(),1,now(),1,0);

insert into egswtax_feesdetail_master(id,description,code,fees,ismandatory,isactive,createddate,createdby,lastmodifieddate,lastmodifiedby,version) values (nextval('seq_egswtax_feesdetail_master'),'Donation Charges','DONATIONCHARGES',(select id from egswtax_fees_master where code='SEWERAGETAX'),'f','t',now(),1,now(),1,0);

insert into egswtax_feesdetail_master(id,description,code,fees,ismandatory,isactive,createddate,createdby,lastmodifieddate,lastmodifiedby,version) values (nextval('seq_egswtax_feesdetail_master'),'Estimation Charges','ESTIMATIONCHARGES',(select id from egswtax_fees_master where code='SEWERAGETAX'),'f','t',now(),1,now(),1,0);

