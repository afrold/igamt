package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CoConstraintExportMode;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintUSERColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintUserColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ValueSetData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.CoConstraintDataSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.CoConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;

public class SerializableCoConstraints extends SerializableElement {

	private CoConstraintsTable coConstraintsTable;
	private String segmentName;
	private Map<String, Table> coConstraintValueTableMap;
	private Map<String, Datatype> coConstraintDatatypeMap;
	private CoConstraintExportMode coConstraintExportMode;
	private Boolean greyOutOBX2FlavorColumn;

	private static final int BINDING_IDENTIFIER_MAX_LENGTH = 40;

	public SerializableCoConstraints(CoConstraintsTable coConstraintsTable, String segmentName,
			Map<String, Table> coConstraintValueTableMap, Map<String, Datatype> coConstraintDatatypeMap,
			CoConstraintExportMode coConstraintExportMode, Boolean greyOutOBX2FlavorColumn) {
		super();
		this.coConstraintsTable = coConstraintsTable;
		this.segmentName = segmentName;
		this.coConstraintValueTableMap = coConstraintValueTableMap;
		this.coConstraintDatatypeMap = coConstraintDatatypeMap;
		this.coConstraintExportMode = coConstraintExportMode;
		this.greyOutOBX2FlavorColumn = greyOutOBX2FlavorColumn;
	}

	@Override
	public Element serializeElement() throws SerializationException {
		if (this.coConstraintExportMode == null || this.coConstraintExportMode.equals(CoConstraintExportMode.COMPACT)) {
			return this.generateCoConstraintsTableCompact();
		} else {
			return this.generateCoConstraintsTableVerbose();
		}
	}

	private Element generateCoConstraintsTableCompact() throws CoConstraintSerializationException {
		if (coConstraintsTable != null) {
			try {
				Element coConstraintsElement = new Element("coconstraints");
				Element tableElement = new Element("table");
				tableElement.addAttribute(new Attribute("class", "contentTable"));
				Element thead = new Element("thead");
				thead.addAttribute(new Attribute("class", "contentThead"));
				Element tr = new Element("tr");
				Element th = new Element("th");
				th.addAttribute(new Attribute("class", "ifContentThead"));
				th.appendChild("IF");
				tr.appendChild(th);
				th = new Element("th");
				th.addAttribute(new Attribute("colspan",
						String.valueOf(calSize(coConstraintsTable.getThenColumnDefinitionList()))));
				th.appendChild("THEN");
				tr.appendChild(th);
				th = new Element("th");
				th.addAttribute(new Attribute("colspan",
						String.valueOf(coConstraintsTable.getUserColumnDefinitionList().size())));
				th.appendChild("USER");
				tr.appendChild(th);
				thead.appendChild(tr);
				tr = new Element("tr");
				th = new Element("th");
				th.addAttribute(new Attribute("class", "ifContentThead"));
				th.appendChild(segmentName + "-" + coConstraintsTable.getIfColumnDefinition().getPath());
				tr.appendChild(th);
				for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
						.getThenColumnDefinitionList()) {
					if (segmentName.equals("OBX") && coConstraintColumnDefinition.getPath().equals("2")) {
						Element thThen1 = new Element("th");
						thThen1.appendChild(segmentName + "-" + coConstraintColumnDefinition.getPath() + "(Value)");
						tr.appendChild(thThen1);
						Element thThen2 = new Element("th");
						thThen2.appendChild(segmentName + "-" + coConstraintColumnDefinition.getPath() + "(Flavor)");
						tr.appendChild(thThen2);
					} else {
						Element thThen = new Element("th");
						thThen.appendChild(segmentName + "-" + coConstraintColumnDefinition.getPath());
						tr.appendChild(thThen);
					}
				}
				for (CoConstraintUserColumnDefinition coConstraintColumnDefinition : coConstraintsTable
						.getUserColumnDefinitionList()) {
					Element thUser = new Element("th");
					thUser.appendChild(coConstraintColumnDefinition.getTitle());
					tr.appendChild(thUser);
				}
				thead.appendChild(tr);
				tableElement.appendChild(thead);
				Element tbody = new Element("tbody");
				for (int i = 0; i < coConstraintsTable.getRowSize(); i++) {
					if (coConstraintsTable.getIfColumnData().get(i).getValueData().getValue() != null
							&& !coConstraintsTable.getIfColumnData().get(i).getValueData().getValue().isEmpty()) {
						boolean thenEmpty = true;
						for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
								.getThenColumnDefinitionList()) {
							if (!coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId()).get(i)
									.getValueSets().isEmpty()
									|| coConstraintsTable.getThenMapData() != null
											&& coConstraintsTable.getThenMapData()
													.containsKey(coConstraintColumnDefinition.getId())
											&& (coConstraintsTable.getThenMapData()
													.get(coConstraintColumnDefinition.getId()).get(i).getValueData()
													.getValue()) != null
											&& !(coConstraintsTable.getThenMapData()
													.get(coConstraintColumnDefinition.getId()).get(i).getValueData()
													.getValue()).isEmpty()) {
								thenEmpty = false;
								break;
							}
						}
						if (!thenEmpty) {
							tr = new Element("tr");
							Element td = new Element("td");
							td.appendChild(coConstraintsTable.getIfColumnData().get(i).getValueData().getValue());
							tr.appendChild(td);
							for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
									.getThenColumnDefinitionList()) {
								if (segmentName.equals("OBX") && coConstraintColumnDefinition.getPath().equals("2")) {
									CoConstraintTHENColumnData coConstraintTHENColumnData = coConstraintsTable
											.getThenMapData().get(coConstraintColumnDefinition.getId()).get(i);
									td = new Element("td");
									Element td2 = new Element("td");
									String valueData = null;
									if (coConstraintTHENColumnData.getValueData() != null
											&& coConstraintTHENColumnData.getValueData().getValue() != null
											&& !coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
										valueData = coConstraintTHENColumnData.getValueData().getValue();
									}
									if (valueData == null) {
										td.addAttribute(new Attribute("class", "greyCell"));
									} else {
										td.appendChild(coConstraintTHENColumnData.getValueData().getValue());
									}
									Datatype flavorDatatype = coConstraintDatatypeMap
											.get(coConstraintTHENColumnData.getDatatypeId());
									if (flavorDatatype != null) {
										String flavorLabel = flavorDatatype.getLabel();
										if (valueData != null && flavorLabel.equals(valueData)) {
											if (this.greyOutOBX2FlavorColumn) {
												td2.addAttribute(new Attribute("class", "greyCell"));
											} else {
												td2.appendChild(flavorLabel);
											}
										} else {
											td2.appendChild(flavorLabel);
										}
									}
									tr.appendChild(td);
									tr.appendChild(td2);
								} else {
									CoConstraintTHENColumnData coConstraintTHENColumnData = coConstraintsTable
											.getThenMapData().get(coConstraintColumnDefinition.getId()).get(i);
									td = new Element("td");
									if (coConstraintTHENColumnData.getValueSets().isEmpty()) {
										if (coConstraintTHENColumnData.getValueData() == null
												|| coConstraintTHENColumnData.getValueData().getValue() == null
												|| coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
											td.addAttribute(new Attribute("class", "greyCell"));
										} else {
											String valueSetLabel = coConstraintTHENColumnData.getValueData().getValue();
											while (valueSetLabel.length() > BINDING_IDENTIFIER_MAX_LENGTH) {
												td.appendChild(new Text(
														valueSetLabel.substring(0, BINDING_IDENTIFIER_MAX_LENGTH - 1)));
												td.appendChild(new Element("br"));
												valueSetLabel = valueSetLabel.substring(BINDING_IDENTIFIER_MAX_LENGTH);
											}
											td.appendChild(new Text(valueSetLabel));
										}
									} else {
										ArrayList<String> valueSetsList = new ArrayList<>();
										for (ValueSetData valueSetData : coConstraintTHENColumnData.getValueSets()) {
											Table table = coConstraintValueTableMap.get(valueSetData.getTableId());
											if (table != null) {
												valueSetsList.add(table.getBindingIdentifier());
											}
										}
										String valueSetLabel = StringUtils.join(valueSetsList, ",");
										while (valueSetLabel.length() > BINDING_IDENTIFIER_MAX_LENGTH) {
											td.appendChild(new Text(
													valueSetLabel.substring(0, BINDING_IDENTIFIER_MAX_LENGTH - 1)));
											td.appendChild(new Element("br"));
											valueSetLabel = valueSetLabel.substring(BINDING_IDENTIFIER_MAX_LENGTH);
										}
										td.appendChild(new Text(valueSetLabel));
									}
									tr.appendChild(td);
								}
							}
							for (CoConstraintUserColumnDefinition coConstraintColumnDefinition : coConstraintsTable
									.getUserColumnDefinitionList()) {
								CoConstraintUSERColumnData coConstraintUSERColumnData = coConstraintsTable
										.getUserMapData().get(coConstraintColumnDefinition.getId()).get(i);
								td = new Element("td");
								if (coConstraintUSERColumnData != null && coConstraintUSERColumnData.getText() != null
										&& !coConstraintUSERColumnData.getText().isEmpty()) {
									td.appendChild(coConstraintUSERColumnData.getText());
								} else {
									td.addAttribute(new Attribute("class", "greyCell"));
								}
								tr.appendChild(td);
							}
							tbody.appendChild(tr);
						} else {
							throw new CoConstraintDataSerializationException("THEN", i + 1, "Empty THEN column data");
						}
					} else {
						throw new CoConstraintDataSerializationException("IF", i + 1, "Missing IF column data");
					}
				}
				tableElement.appendChild(tbody);
				coConstraintsElement.appendChild(tableElement);
				return coConstraintsElement;
			} catch (Exception e) {
				throw new CoConstraintSerializationException(e, "Co-Constraints table");
			}
		}
		return null;
	}

	private Element generateCoConstraintsTableVerbose() throws CoConstraintSerializationException {
		if (coConstraintsTable != null) {
			try {
				Element coConstraintsElement = new Element("coconstraints");
				Element tableElement = new Element("table");
				tableElement.addAttribute(new Attribute("class", "contentTable"));
				Element thead = new Element("thead");
				thead.addAttribute(new Attribute("class", "contentThead"));
				Element tr = new Element("tr");
				Element th = new Element("th");
				th.addAttribute(new Attribute("class", "ifContentThead"));
				th.appendChild(
						"IF (" + (segmentName + "-" + coConstraintsTable.getIfColumnDefinition().getPath()) + ")");
				tr.appendChild(th);
				th = new Element("th");
				th.appendChild("Column");
				tr.appendChild(th);
				th = new Element("th");
				th.appendChild("Value");
				tr.appendChild(th);
				thead.appendChild(tr);
				tableElement.appendChild(thead);
				Element tbody = new Element("tbody");
				for (int i = 0; i < coConstraintsTable.getRowSize(); i++) {
					if (coConstraintsTable.getIfColumnData().get(i).getValueData().getValue() != null
							&& !coConstraintsTable.getIfColumnData().get(i).getValueData().getValue().isEmpty()) {
						boolean thenEmpty = true;
						for (CoConstraintColumnDefinition coConstraintColumnDefinition : coConstraintsTable
								.getThenColumnDefinitionList()) {
							if (!coConstraintsTable.getThenMapData().get(coConstraintColumnDefinition.getId()).get(i)
									.getValueSets().isEmpty()
									|| coConstraintsTable.getThenMapData() != null
											&& coConstraintsTable.getThenMapData()
													.containsKey(coConstraintColumnDefinition.getId())
											&& (coConstraintsTable.getThenMapData()
													.get(coConstraintColumnDefinition.getId()).get(i).getValueData()
													.getValue()) != null
											&& !(coConstraintsTable.getThenMapData()
													.get(coConstraintColumnDefinition.getId()).get(i).getValueData()
													.getValue()).isEmpty()) {
								thenEmpty = false;
								break;
							}
						}
						if (!thenEmpty) {
							CoConstaintVerboseRow line = getRow(coConstraintsTable, i);
							if (line.children.size() > 0) {
								Element tr1 = new Element("tr");
								Element td1 = new Element("td");
								Element td2 = new Element("td");
								Element td3 = new Element("td");
								if (coConstraintsTable.getIfColumnData().get(i).getValueData().getValue() != null
										&& !coConstraintsTable.getIfColumnData().get(i).getValueData().getValue()
												.isEmpty()) {
									td1.appendChild(
											coConstraintsTable.getIfColumnData().get(i).getValueData().getValue());
								} else {
									break;
								}
								td1.addAttribute(new Attribute("rowspan", String.valueOf(line.children.size())));
								td1.addAttribute(new Attribute("class", "ifContent"));
								if (line.children.get(0).getType().equals("user")) {
									// td2.addAttribute(new Attribute("class","greenHeader"));
									// td3.addAttribute(new Attribute("class","greenContent"));
								}
								td2.appendChild(line.children.get(0).getKey());
								td2.addAttribute(
										new Attribute("colspan", String.valueOf(line.children.get(0).getColspan())));
								td2.addAttribute(new Attribute("class", "alignCenter"));
								if (line.children.get(0).getColspan() == 1) {
									td3.appendChild(line.children.get(0).getValue());
									tr1.appendChild(td3);
								}
								td3.appendChild(line.children.get(0).getValue());
								tr1.appendChild(td1);
								tr1.appendChild(td2);
								tbody.appendChild(tr1);
								if (line.children.size() > 1) {
									for (int j = 1; j < line.children.size(); j++) {
										Element trtemp = new Element("tr");
										Element tdKey = new Element("td");
										Element tdValue = new Element("td");
										tdKey.addAttribute(new Attribute("colspan",
												String.valueOf(line.children.get(j).getColspan())));
										tdKey.appendChild(line.children.get(j).getKey());
										trtemp.appendChild(tdKey);
										if (line.children.get(j).getType().equals("user")) {
											if (line.children.get(j).getColspan() < 2) {
												trtemp.addAttribute(new Attribute("class", "greenContent"));
											} else {
												trtemp.addAttribute(new Attribute("class", "greenHeader alignCenter"));

											}
											// "greenHeader"
										}
										if (line.children.get(j).getColspan() == 1) {
											tdValue.appendChild(line.children.get(j).getValue());
											trtemp.appendChild(tdValue);

										}
										tbody.appendChild(trtemp);

									}
								}
							}
							// tbody.appendChild(tr);
						} else {
							throw new CoConstraintDataSerializationException("THEN", i + 1, "Empty THEN column data");
						}
					} else {
						throw new CoConstraintDataSerializationException("IF", i + 1, "Missing IF column data");
					}
				}
				tableElement.appendChild(tbody);
				coConstraintsElement.appendChild(tableElement);
				return coConstraintsElement;
			} catch (Exception e) {
				throw new CoConstraintSerializationException(e, "Co-Constraints table");
			}
		}
		return null;
	}

	private CoConstaintVerboseRow getRow(CoConstraintsTable table, int i) {
		CoConstaintVerboseRow line = new CoConstaintVerboseRow();
		if (table.getIfColumnData().get(i) != null) {
			line.setId(table.getIfColumnData().get(i).getValueData().getValue());
		}
		if (!table.getThenColumnDefinitionList().isEmpty()) {
			CoConstraintRow thenheader = new CoConstraintRow();
			thenheader.setType("THEN");
			thenheader.setKey("THEN");
			thenheader.setValue("THEN");
			thenheader.setColspan(2);
			line.children.add(thenheader);
			for (CoConstraintColumnDefinition then : table.getThenColumnDefinitionList()) {
				if (segmentName.equals("OBX") && then.getPath().equals("2")) {
					CoConstraintRow row1 = new CoConstraintRow();
					row1.setKey(segmentName + "-" + then.getPath() + "(Value)");
					row1.setType("then");
					CoConstraintRow row2 = new CoConstraintRow();
					row2.setKey(segmentName + "-" + then.getPath() + "(Flavor)");
					row2.setType("then");
					CoConstraintTHENColumnData coConstraintTHENColumnData = table.getThenMapData().get(then.getId())
							.get(i);
					if (coConstraintTHENColumnData.getValueData() == null
							|| coConstraintTHENColumnData.getValueData().getValue() == null
							|| coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
						//
					} else {
						row1.setValue(coConstraintTHENColumnData.getValueData().getValue());
						line.children.add(row1);
					}
					row2.setValue(coConstraintDatatypeMap.get(coConstraintTHENColumnData.getDatatypeId()).getLabel());
					line.children.add(row2);
				} else {
					CoConstraintRow row = new CoConstraintRow();
					row.setType("then");
					row.setKey(segmentName + "-" + then.getPath());

					CoConstraintTHENColumnData coConstraintTHENColumnData = table.getThenMapData().get(then.getId())
							.get(i);
					if (coConstraintTHENColumnData.getValueSets().isEmpty()) {
						if (coConstraintTHENColumnData.getValueData() == null
								|| coConstraintTHENColumnData.getValueData().getValue() == null
								|| coConstraintTHENColumnData.getValueData().getValue().isEmpty()) {
						} else {
							row.setValue(coConstraintTHENColumnData.getValueData().getValue());
							row.setKey(row.getKey() + "(Value)");
							line.children.add(row);
						}
					} else {
						ArrayList<String> valueSetsList = new ArrayList<>();
						for (ValueSetData valueSetData : coConstraintTHENColumnData.getValueSets()) {
							Table vs = coConstraintValueTableMap.get(valueSetData.getTableId());
							if (vs != null) {
								valueSetsList.add(vs.getBindingIdentifier());
							}
						}
						row.setValue(StringUtils.join(valueSetsList, ","));
						row.setKey(row.getKey() + "(Value Set)");
						line.children.add(row);
					}
				}
			}
		}
		if (table.getUserColumnDefinitionList().size() > 0) {
			CoConstraintRow row = new CoConstraintRow();
			row.setType("user");
			row.setKey("USER");
			row.setValue("USER");
			row.setColspan(2);
			line.children.add(row);
		}
		for (CoConstraintUserColumnDefinition coConstraintColumnDefinition : table.getUserColumnDefinitionList()) {
			CoConstraintRow row = new CoConstraintRow();
			row.setType("user");
			row.setKey(coConstraintColumnDefinition.getTitle());
			CoConstraintUSERColumnData coConstraintUSERColumnData = table.getUserMapData()
					.get(coConstraintColumnDefinition.getId()).get(i);
			if (coConstraintUSERColumnData != null && coConstraintUSERColumnData.getText() != null
					&& !coConstraintUSERColumnData.getText().isEmpty()) {
				row.setValue(coConstraintUSERColumnData.getText());
				line.children.add(row);
			} else {
			}
		}
		return line;
	}

	private int calSize(List<CoConstraintColumnDefinition> thenColumnDefinitionList) {
		int count = 0;
		for (CoConstraintColumnDefinition coConstraintColumnDefinition : thenColumnDefinitionList) {
			if (segmentName.equals("OBX") && coConstraintColumnDefinition.getPath().equals("2")) {
				count = count + 2;
			} else {
				count = count + 1;
			}
		}
		return count;
	}

}
