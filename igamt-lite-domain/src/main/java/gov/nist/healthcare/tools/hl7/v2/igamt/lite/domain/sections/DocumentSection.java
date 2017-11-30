package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;
import java.util.ArrayList;
import java.util.List;
 
public class DocumentSection<T extends SectionData> {
               
                private String label;
                private int position;
                private T data;
                private List<DocumentSection<? extends SectionData>> children = new ArrayList<DocumentSection<? extends SectionData>>() ;
                
                public DocumentSection() {
					super();
					// TODO Auto-generated constructor stub
				}
				private DocumentSection<SectionData> parent;
               
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
                public List<DocumentSection<? extends SectionData>> getChildren() {
                                return children;
                }
                public void setChildren(List<DocumentSection<? extends SectionData>> children) {
                                this.children = children;
                }
                public T getData() {
                                return data;
                }
                public void setData(T data) {
                                this.data = data;
                                
                }
                public void addChild(DocumentSection<? extends SectionData > child){
                		this.children.add(child);
                       
                	
                }
}