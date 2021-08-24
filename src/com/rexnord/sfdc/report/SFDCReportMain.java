package com.rexnord.sfdc.report;

import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.rexnord.sfdc.mail.*;

public class SFDCReportMain {

	final static Logger logger = Logger.getLogger(SFDCReportMain.class);
	static Properties props;

	public static void exportCSVFiles(String propertiesFile)  {
		ReportGenerator csv = new ReportGenerator();
		csv.exportCSVReport(propertiesFile);
	}

	public static void mailCSVReport(String propertiesFile) {
		ReportMailSender sendMyFiles = new ReportMailSender();
		sendMyFiles.sendReport(propertiesFile);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java " + " SFDCReportMain "
					+ " Properties_file_name");
			System.exit(1);
		}
		String propertiesFileName = args[0].trim();
		SFDCReportMain.exportCSVFiles(propertiesFileName);
		SFDCReportMain.mailCSVReport(propertiesFileName);
	}
}
