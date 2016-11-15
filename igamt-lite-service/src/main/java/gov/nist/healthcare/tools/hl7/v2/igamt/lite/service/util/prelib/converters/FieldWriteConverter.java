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
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 31, 2015
 */
@WritingConverter
public class FieldWriteConverter implements Converter<Field, DBObject> {

  @Override
  public DBObject convert(Field source) {
    DBObject dbo = new BasicDBObject();
    dbo.put("_id", source.getId());
    dbo.put("itemNo", source.getItemNo());
    dbo.put("min", source.getMin());
    dbo.put("max", source.getMax());
    dbo.put("type", source.getType());
    dbo.put("name", source.getName());
    dbo.put("usage", source.getUsage().value());
    dbo.put("minLength", source.getMinLength());
    dbo.put("maxLength", source.getMaxLength());
    dbo.put("confLength", source.getConfLength());
    dbo.put("tables", source.getTables());
    dbo.put("position", source.getPosition());
    dbo.put("comment", source.getComment());
    dbo.put("text", source.getText());
    dbo.put("datatype", source.getDatatype());
    return dbo;
  }

}
