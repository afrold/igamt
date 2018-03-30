package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ConnectApp {

  String url;
  String name;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ConnectApp(String name, String url) {
    this.name = name;
    this.url = url;
  }

  public ConnectApp() {
    super();
    // TODO Auto-generated constructor stub
  }



}
