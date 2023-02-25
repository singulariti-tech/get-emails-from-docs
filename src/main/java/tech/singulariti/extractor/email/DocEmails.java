package tech.singulariti.extractor.email;

import java.util.List;

public class DocEmails {
  private String documentPath;
  private List<String> emails;

  public DocEmails() {}

  public String getDocumentPath() {
    return documentPath;
  }

  public void setDocumentPath(String documentPath) {
    this.documentPath = documentPath;
  }

  public List<String> getEmails() {
    return emails;
  }

  public void setEmails(List<String> emails) {
    this.emails = emails;
  }
}
