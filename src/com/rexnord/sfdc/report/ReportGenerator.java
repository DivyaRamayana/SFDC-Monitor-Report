package com.rexnord.sfdc.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.rexnord.sfdc.mail.ReportMailSender;

public class ReportGenerator {
	static Properties props;
	final static Logger logger = Logger.getLogger(ReportMailSender.class);

	public static Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static int countLines(File aFile) throws IOException {
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(aFile));
			while ((reader.readLine()) != null)
				;
			return reader.getLineNumber();
		} catch (Exception ex) {
			return -1;
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	public void exportCSVReport(String propertiesFile)  {
		

		props = new Properties();
		
		try {

			props.load(new FileInputStream("properties/" + propertiesFile));

			String csvFile = "REPORT_SFDC_FILES_";
			String filePathString = props.getProperty("lookupFolder").trim();
			String dropFolder = props.getProperty("dropFolder").trim();
			
			String[] Geographies = { "CA", "BR", "MX", "CC" };
			String[] FileTypes = { "ORDER", "QUOTE", "CANCEL_ORD", "INVOICE" };
			String filename = "";
			

			String DATE_FORMAT = "yyyyMMdd";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Calendar cal = Calendar.getInstance(); // today
			String dateExt = sdf.format(cal.getTime()).toString() + ".csv";
			csvFile = dropFolder+ csvFile + dateExt;
			logger.info("---------------Report exist?------------" + csvFile);
			
			File fileTemp = new File(csvFile);
			if (fileTemp.exists()) {
				fileTemp.delete();
				logger.info("---------------DELETE Previous Report------------" + csvFile);
			}
			FileWriter writer = new FileWriter(csvFile);
			
			
			String formatedDate = new SimpleDateFormat("yyyyMMdd")
					.format(yesterday());

			for (String Geography : Geographies) {
				CSVUtils.writeLine(writer, Arrays.asList(Geography));
				CSVUtils.writeLine(writer, Arrays.asList("Present Files"));
				try {
					for (String fileType : FileTypes) {
						if (fileType == "QUOTE" && (Geography != "BR"||Geography != "CC"))
							continue;
						String tempPathString = null;
						filename = fileType + "_" + "SFDC" + "_" + Geography
								+ "_" + formatedDate + ".txt";

						tempPathString = filePathString + filename;
						try {
							File f = new File(tempPathString);
							if (f.exists() && !f.isDirectory()) {
								String lastModifiedDate = new SimpleDateFormat(
										"MM-dd-yyyy hh:mm:ss").format(f
										.lastModified());
								int lineCount = countLines(f);
								logger.info(tempPathString + "\t"
										+ lineCount);
								CSVUtils.writeLine(writer, Arrays.asList(
										formatedDate, fileType,
										lastModifiedDate,
										Integer.toString(lineCount)));
							} else {
								logger.error(tempPathString + "\t"
										+ "does not exist");
								CSVUtils.writeLine(writer, Arrays
										.asList(formatedDate, fileType, "", "",
												"False"));

							}
						} catch (Exception e) {
							logger.error(filename + "\t"
									+ "could not be opened");
						}
					}

				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			writer.flush();
			writer.close();
			logger.info("Report File Generated.");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
