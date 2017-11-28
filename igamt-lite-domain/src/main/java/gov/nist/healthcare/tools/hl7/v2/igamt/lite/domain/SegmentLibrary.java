package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segment-library")
public class SegmentLibrary extends Library implements java.io.Serializable,
    Cloneable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  private Long accountId;

  private String ext;

  private SegmentLibraryMetaData metaData;

  private Constant.SCOPE scope;

  public SegmentLibrary() {
    super();
    type = Constant.SEGMENTLIBRARY;
    sectionPosition=4;
  }

  private Set<SegmentLink> children = new HashSet<SegmentLink>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public Set<SegmentLink> getChildren() {
    return children;
  }

  public void setChildren(Set<SegmentLink> children) {
    this.children = children;
  }

  public Constant.SCOPE getScope() {
    return scope;
  }

  public void setScope(Constant.SCOPE scope) {
    this.scope = scope;
  }

  public void addSegment(SegmentLink seg) {
    children.add(seg);
  }

  public void addSegment(Segment seg) {
    children.add(new SegmentLink(seg.getId(), seg.getName(), null));
  }

  public SegmentLink save(SegmentLink seg) {
    children.add(seg);
    return seg;
  }

  public void delete(SegmentLink sgl) {
    this.children.remove(sgl);
  }

  public SegmentLink findOneSegmentById(String id) {
    System.out.println("ID: " + id);

    if (this.children != null) {
      for (SegmentLink segl : this.children) {
        if (segl.getId().equals(id)) {
          return segl;
        }
      }
    }

    return null;
  }

  public SegmentLink findOne(SegmentLink segl) {
    if (this.children != null) {
      for (SegmentLink segl1 : this.children) {
        if (segl.equals(segl1)) {
          return segl1;
        }
      }
    }

    return null;
  }

  public SegmentLink findOne(String sgId) {
    if (this.children != null) {
      for (SegmentLink segl1 : this.children) {
        if (segl1.getId().equals(sgId)) {
          return segl1;
        }
      }
    }

    return null;
  }

  public SegmentLink findOneByName(String name) {
    if (this.children != null) {
      for (SegmentLink segl : this.children) {
        if (segl.getName().equals(name)) {
          return segl;
        }
      }
    }

    return null;
  }

  public void merge(SegmentLibrary segLib) {
    segLib.getChildren().addAll(segLib.getChildren());
  }

  public SegmentLibraryMetaData getMetaData() {
    return metaData;
  }

  public void setMetaData(SegmentLibraryMetaData metaData) {
    this.metaData = metaData;
  }


  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public boolean contains(String id) {
    if (this.children != null) {
      for (SegmentLink segl : this.children) {
        if (segl.getId().equals(id)) {
          return true;
        }
      }
    }
    return false;
  }

  public SegmentLibrary clone() throws CloneNotSupportedException {
    SegmentLibrary clone = new SegmentLibrary();

    HashSet<SegmentLink> clonedChildren = new HashSet<SegmentLink>();
    for (SegmentLink sl : this.children) {
      clonedChildren.add(sl.clone());
    }
    clone.setChildren(clonedChildren);
    clone.setExt(this.getExt() + "-" + genRand());
    clone.setMetaData(this.getMetaData().clone());
    clone.setScope(this.getScope());
    clone.setSectionContents(this.getSectionContents());
    clone.setSectionDescription(this.getSectionDescription());
    clone.setSectionPosition(this.getSectionPosition());
    clone.setSectionTitle(this.getSectionTitle());
    clone.setType(this.getType());
    return clone;
  }


  private String genRand() {
    return Integer.toString(new Random().nextInt(100));
  }


  public void addSegments(Set<SegmentLink> segs) {
    children.addAll(segs);
  }
}
