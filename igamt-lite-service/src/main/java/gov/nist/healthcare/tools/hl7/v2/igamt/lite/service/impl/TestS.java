package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

public class TestS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String path = "3[1].4[31].5[4]";
		  String reg = "\\[([a-z]||[0-9]||[A-Z])*\\]";
		 String p= path.replaceAll(reg, "");
		  //String[] res= path.replaceAll(reg, "").split("\\.");
		  
//		  for(String s :res){
//			  System.out.println(s);
//		  }

	
		 int index = p.indexOf(".");
			if(index >-1){
				Integer first = Integer.valueOf(p.substring(0, index));
				
			  System.out.println(first);

			}


	}

}
