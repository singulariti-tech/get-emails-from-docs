package tech.singulariti.extractor.email;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OutputGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutputGenerator.class);

  private final String outputPath;

  public OutputGenerator(String path) {
    this.outputPath = path;
  }

  public void printExtractedEmails(List<DocEmails> docs) {
    String finalPath = String.format("%s/%s", this.outputPath, "extracted-emails.txt");
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(finalPath));
        CSVPrinter csvPrinter =
            new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("EMAIL", "DOCUMENT")); ) {
      for (DocEmails doc : docs) {
        List<String> emails = doc.getEmails();
        String docPath = doc.getDocumentPath();
        for (String email : emails) {
          csvPrinter.printRecord(email, docPath);
        }
      }
      csvPrinter.flush();
    } catch (Exception e) {
      LOGGER.error(
          "Failed to generate list of documents from which email could not be extracted", e);
    }
  }

  public void notifyFailedCases(List<Path> failedCases) {
    String finalPath = String.format("%s/%s", this.outputPath, "failed.txt");
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(finalPath));
        CSVPrinter csvPrinter =
            new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("DOCUMENT")); ) {
      for (Path path : failedCases) {
        String fp = path.toString();
        csvPrinter.printRecord(fp);
      }
      csvPrinter.flush();
    } catch (Exception e) {
      LOGGER.error(
          "Failed to generate list of documents from which email could not be extracted", e);
    }
  }
}
