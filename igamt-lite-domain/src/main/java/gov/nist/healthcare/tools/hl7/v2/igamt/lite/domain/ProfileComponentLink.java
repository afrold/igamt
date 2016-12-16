package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ProfileComponentLink extends AbstractLink implements Cloneable, Comparable<ProfileComponentLink> {
	private String name;

	public ProfileComponentLink() {
		    super();
		  }

	public ProfileComponentLink(String id, String name) {
		    super();
		    this.setId(id);
		    this.name = name;
		  }

	@Override
	public int compareTo(ProfileComponentLink o) {
		// TODO Auto-generated method stub
		return 0;
	}
	 @Override
	  public String toString() {
	    return "ProfileComponentLink [id=" + id + ", name=" + name+ "]";
	  }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ProfileComponentLink clone() {
		ProfileComponentLink clonedLink = new ProfileComponentLink();
	    clonedLink.setName(this.name);
	    clonedLink.setId(this.getId());
	    return clonedLink;
	  }
}
