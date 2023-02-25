package tech.singulariti.extractor.email;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  private final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>();
  private final ContentReader READER = new ContentReader();

  {
    SUPPORTED_EXTENSIONS.add("doc");
    SUPPORTED_EXTENSIONS.add("docx");
    SUPPORTED_EXTENSIONS.add("pdf");
    SUPPORTED_EXTENSIONS.add("rtf");
    SUPPORTED_EXTENSIONS.add("txt");
  }

  public Application() {}

  public static void main(String[] args) {
    Options options = new Options();

    Option pathOption =
        Option.builder("s")
            .longOpt("sourcePath")
            .argName("s")
            .hasArg()
            .required(true)
            .desc("Path to folder with documents")
            .build();

    Option savePathOption =
        Option.builder("o")
            .longOpt("outputPath")
            .argName("o")
            .hasArg()
            .required(false)
            .desc(
                "Path to folder where output will be saved. Not mandatory. Source path will be used if not provided.")
            .build();

    options.addOption(pathOption);
    options.addOption(savePathOption);

    CommandLine cmd;
    CommandLineParser parser = new DefaultParser();
    HelpFormatter helper = new HelpFormatter();

    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption("s")) {
        String sourcePath = cmd.getOptionValue("s");
        String outputPath = cmd.getOptionValue("o");

        if (outputPath == null || StringUtils.isEmpty(outputPath)) {
          outputPath = sourcePath;
        }

        Application application = new Application();

        // validate paths
        boolean validIP = application.validPath(sourcePath, false);
        if (!validIP) {
          System.out.println("ERROR: Source path provided does not exist or is not readable");
          System.exit(0);
        }

        boolean validOP = application.validPath(outputPath, true);
        if (!validOP) {
          System.out.println(
              "ERROR: Output path provided does not exist or is not readable or writable");
          System.exit(0);
        }

        application.extractEmailAddresses(sourcePath, outputPath);
      } else {
        System.out.println(String.format("ERROR: Required parameters missing"));

        helper.printHelp(
            "java -jar get-email-from-docs-1.0.jar -p <path-to-directory-with-docs>", options);
        System.exit(0);
      }
    } catch (ParseException e) {
      System.out.println(String.format("ERROR: %s", e.getMessage()));

      helper.printHelp(
          "java -jar get-email-from-docs-1.0.jar -p <path-to-directory-with-docs>", options);
      System.exit(0);
    }
  }

  public boolean validPath(String path, boolean write) {
    File inputDir = new File(path);
    boolean basic = inputDir.exists() && inputDir.canRead();
    // defaults to writable
    boolean advanced = true;
    if (write) {
      advanced = inputDir.canWrite();
    }

    return basic && advanced;
  }

  public void extractEmailAddresses(String sourcePath, String outputPath) {
    OutputGenerator output = new OutputGenerator(outputPath);

    System.out.println(
        String.format(
            "Extracting emails from docs (%s) in %s",
            String.join(",", SUPPORTED_EXTENSIONS), sourcePath));
    // get all supported files
    List<Path> failed = new ArrayList<>();
    List<DocEmails> extracted = new ArrayList<>();

    try (Stream<Path> stream = Files.walk(Paths.get(sourcePath))) {
      List<Path> filtered =
          stream
              .filter(
                  p -> {
                    try {
                      File f = p.toFile();
                      if (!Files.isSymbolicLink(p) && f.exists() && f.isFile() && !f.isHidden()) {
                        String ext = FilenameUtils.getExtension(p.toFile().getName());
                        return SUPPORTED_EXTENSIONS.contains(ext);
                      }
                    } catch (Exception e) {
                    }
                    return false;
                  })
              .collect(Collectors.toList());

      for (Path p : filtered) {
        File file = p.toFile();
        if (file.exists()) {
          Optional<String> contentWrapper = READER.getContent(file);
          if (contentWrapper.isPresent()) {
            String content = contentWrapper.get();
            List<String> emails = EmailAddressExtractor.getEmailAddresses(content);
            if (!emails.isEmpty()) {
              DocEmails de = new DocEmails();
              de.setDocumentPath(file.getPath());
              de.setEmails(emails);
              extracted.add(de);
            } else {
              failed.add(p);
            }
          }
        }
      }

    } catch (Exception ex) {
      LOGGER.error("", ex);
    }

    // save to excel
    if (!extracted.isEmpty()) {
      output.printExtractedEmails(extracted);
    }

    // if fails add document name path to another sheet
    if (!failed.isEmpty()) {
      output.notifyFailedCases(failed);
    }

    String msg =
        String.format(
            "Extraction complete. Successfully extracted emails may be found in 'extracted-emails.txt'. Failures will be listed in 'failed.txt'.");
    System.out.println(msg);
  }

  public List<String> getEmailAddresses(String content) {
    List<String> eas = EmailAddressExtractor.getEmailAddresses(content);
    return eas;
  }
}
