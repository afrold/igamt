package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ConnectApp {

  String url;
  String name;
  int position;

  public int getPosition() {
	return position;
}

public void setPosition(int position) {
	this.position = position;
}

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

  public ConnectApp(String name, String url, int i) {
    this.name = name;
    this.url = url;
    this.position=i;
  }

  public ConnectApp() {
    super();
    // TODO Auto-generated constructor stub
  }



}
