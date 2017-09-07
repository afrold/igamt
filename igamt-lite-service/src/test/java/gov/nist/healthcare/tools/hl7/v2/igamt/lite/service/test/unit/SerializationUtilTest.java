package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.IntegrationTestApplicationConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.SerializationUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import static org.junit.Assert.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 * <p/>
 * Created by Maxence Lefort on 1/12/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationConfig.class})
public class SerializationUtilTest {
    @Autowired
    SerializationUtil serializationUtil;

    @Test
    public void testIsShowConfLength(){
        assertTrue(serializationUtil.isShowConfLength("2.8"));
        assertTrue(serializationUtil.isShowConfLength("3.8"));
        assertTrue(serializationUtil.isShowConfLength("3.0"));
        assertTrue(serializationUtil.isShowConfLength("2.8.0"));
        assertTrue(serializationUtil.isShowConfLength("2.5.2"));
        assertTrue(serializationUtil.isShowConfLength("2.8.10"));
        assertFalse(serializationUtil.isShowConfLength("1.8"));
        assertFalse(serializationUtil.isShowConfLength("1.8.2"));
        assertFalse(serializationUtil.isShowConfLength("1.2.0"));
        assertFalse(serializationUtil.isShowConfLength("1.2.9"));
        assertFalse(serializationUtil.isShowConfLength("0.1"));
        assertFalse(serializationUtil.isShowConfLength("0.1.0"));
        assertFalse(serializationUtil.isShowConfLength("0.1.9"));
        assertFalse(serializationUtil.isShowConfLength("2.5.1"));
        assertFalse(serializationUtil.isShowConfLength("2.5.1.0"));
    }
    
    private boolean doTestRemoveBr(String div, int expectedBrs){
    	Document doc = Jsoup.parse(div);
    	Element elementDiv = doc.select("div").first();
    	serializationUtil.removeDoubleBrTag(elementDiv);
    	String html = doc.body().html();
    	return StringUtils.countOccurrencesOf(html, "<br>") == expectedBrs;
    }
    
    @Test
    public void testRemoveDoubleBrTagsDoubleTag(){
    	String div = "<div><br/><br/>paragraph 1<br/>p2<br/><br/></div>";
    	assertTrue(doTestRemoveBr(div,3));
    }
    
    @Test
    public void testRemoveDoubleBrTagsNoDoubleTag(){
    	String div = "<div><br/>paragraph 1</div>";
    	assertTrue(doTestRemoveBr(div,1));
    }
}
