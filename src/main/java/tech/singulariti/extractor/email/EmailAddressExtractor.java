package tech.singulariti.extractor.email;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAddressExtractor {

  private static final String REGEX_STRING =
      "[a-z0-9!#$%&'*+/=?^_‘{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_‘{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(REGEX_STRING);

  private EmailAddressExtractor() {}

  public static List<String> getEmailAddresses(String content) {
    List<String> found = new ArrayList<>();
    Matcher matcher = EMAIL_PATTERN.matcher(content);
    while (matcher.find()) {
      found.add(matcher.group());
    }

    return found;
  }
}
