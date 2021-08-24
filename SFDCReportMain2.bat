@echo off
cd /d "U:\SFDC\SFDC_Monitor_Report"
E:\usr\sap\trans\sapjvm_6\jre\bin\java.exe -classpath U:\SFDC\SFDC_Monitor_Report\bin;U:\SFDC\SFDC_Monitor_Report\lib\activation.jar;U:\SFDC\SFDC_Monitor_Report\lib\javax.mail-1.5.5.jar;U:\SFDC\SFDC_Monitor_Report\lib\commons-vfs2-2.0.jar;U:\SFDC\SFDC_Monitor_Report\lib\log4j-1.2.17.jar com.rexnord.sfdc.report.SFDCReportMain Props.properties