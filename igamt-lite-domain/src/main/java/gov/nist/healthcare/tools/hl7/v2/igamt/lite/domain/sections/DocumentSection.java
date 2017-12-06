package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;
import java.util.ArrayList;
import java.util.List;
 
public class DocumentSection {
               
                private String label;
                private int position;
                private SectionData data;
                private List<DocumentSection> children = new ArrayList<DocumentSection>() ;
                
                public DocumentSection() {
					super();
					// TODO Auto-generated constructor stub
				}
               
                public String getLabel() {
                                return label;
                }
                public void setLabel(String label) {
                                this.label = label;
                }
                public int getPosition() {
                                return position;
                }
                public void setPosition(int position) {
                                this.position = position;
                }
                public List<DocumentSection> getChildren() {
                                return children;
                }
                public void setChildren(List<DocumentSection> children) {
                                this.children = children;
                }
                public SectionData getData() {
                                return data;
                }
                public void setData(SectionData data) {
                                this.data = data;
                                
                }
     
}