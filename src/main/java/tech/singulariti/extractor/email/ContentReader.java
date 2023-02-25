package tech.singulariti.extractor.email;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Optional;

public class ContentReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentReader.class);

  private final AutoDetectParser parser = new AutoDetectParser(new DefaultDetector());

  public ContentReader() {}

  public Optional<String> getContent(File file) {
    String content = null;

    try {
      Metadata metadata = new Metadata();
      ParseContext context = new ParseContext();
      URL url = file.toURI().toURL();

      OutputStream outputstream = new ByteArrayOutputStream();
      InputStream input = TikaInputStream.get(url, metadata);
      ContentHandler handler = new BodyContentHandler(outputstream);
      parser.parse(input, handler, metadata, context);
      input.close();

      content = outputstream.toString();
    } catch (Exception e) {
      LOGGER.error(String.format("Could not extract content from doc: %s", file.getName()), e);
      content = null;
    }

    return Optional.ofNullable(content);
  }
}
