<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="utf-8"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		indent="yes" />
	<xsl:param name="inlineConstraints" select="'true'"></xsl:param>
	<xsl:template match="/">

		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>Implementation guide</title>
			</head>

			<body style="font-family:Arial Narrow, Arial, sans-serif;">
				<a name="top"></a>

				<xsl:call-template name="tocSect" />

				<!-- <h1> <a href="#msgInfra">Messages infrastructure</a> </h1> <h2> 
					<a href="#messages">Conformance Profiles</a> </h2> <xsl:apply-templates select="ConformanceProfile/MessagesDisplay" 
					mode="toc"> <xsl:sort select="@Position"></xsl:sort> </xsl:apply-templates> 
					<br></br> <h2> <a href="#segments">Segments and fields descriptions</a> </h2> 
					<xsl:apply-templates select="ConformanceProfile/Segments" mode="toc"> <xsl:sort 
					select="@Label" data-type="text"></xsl:sort> </xsl:apply-templates> <br></br> 
					<h2> <a href="#datatypes">Datatypes</a> </h2> <xsl:apply-templates select="ConformanceProfile/Datatypes" 
					mode="toc"> <xsl:sort select="@Label" data-type="text"></xsl:sort> </xsl:apply-templates> 
					<br></br> <h2> <a href="#valuesets">Value sets</a> </h2> <xsl:apply-templates 
					select="ConformanceProfile/ValueSets" mode="toc"> <xsl:sort select="@BindingIdentifier"></xsl:sort> 
					</xsl:apply-templates> <br></br> -->
				<hr></hr>

				<!-- <xsl:value-of select="$inlineConstraints" /> -->

				<xsl:call-template name="dispSect" />

				<!-- <h2> <u>Messages infrastructure</u> </h2> <a name="msgInfra"></a> 
					<h3> <u>Conformance Profiles</u> </h3> <a name="messages"></a> <xsl:apply-templates 
					select="ConformanceProfile/MessagesDisplay"> </xsl:apply-templates> <a name="segments"></a> 
					<h3> <u>Segments and fields descriptions</u> </h3> <xsl:apply-templates select="ConformanceProfile/Segments"> 
					<xsl:sort select="@Name"></xsl:sort> </xsl:apply-templates> <a name="datatypes"></a> 
					<h3> <u>Datatypes</u> </h3> <xsl:apply-templates select="ConformanceProfile/Datatypes"> 
					<xsl:sort select="@ID"></xsl:sort> </xsl:apply-templates> <h3> <u>Value sets</u> 
					</h3> <a name="valuesets"></a> <xsl:apply-templates select="ConformanceProfile/ValueSets"> 
					<xsl:sort select="@BindingIdentifier"></xsl:sort> </xsl:apply-templates> -->
			</body>
		</html>
	</xsl:template>
	<xsl:template name="dispInfoSect" mode="disp">
		<xsl:if test="name() = 'Section'">
			<a id="{@id}" name="{@id}">
				<u>
					<xsl:choose>
						<xsl:when test="@h &lt; 7">
							<xsl:element name="{concat('h', @h)}">
								<xsl:value-of select="@prefix" />
								-
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:element name="h6">
								<xsl:value-of select="@prefix" />
								-
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:otherwise>
					</xsl:choose>
				</u>
			</a>
			<br />
			<xsl:call-template name="dispSectContent" />
			<xsl:call-template name="dispProfileContent" />
			<a href="#top">Link to top</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="dispSectContent">
		<xsl:value-of disable-output-escaping="yes" select="SectionContent" />
		<!--<xsl:copy-of select="node()" /> -->
	</xsl:template>

	<xsl:template name="dispProfileContent">
		<xsl:choose>
			<xsl:when test="count(MessageDisplay) &gt; 0">
				<xsl:apply-templates select="MessageDisplay">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="count(Segment) &gt; 0">
				<xsl:apply-templates select="Segment">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="count(Datatype) &gt; 0">
				<xsl:apply-templates select="Datatype">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="count(ValueSetDefinition) &gt; 0">
				<xsl:apply-templates select="ValueSetDefinition">
					<xsl:sort select="@position" data-type="number"></xsl:sort>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="dispSect">
		<xsl:call-template name="dispInfoSect" />
		<xsl:for-each select="*">
			<xsl:sort select="@position"></xsl:sort>
			<xsl:call-template name="dispSect" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="tocInfoSect">
		<xsl:if test="name() = 'Section'">
			<a href="#{@id}">
				<xsl:choose>
					<xsl:when test="@h &lt; 7">
						<xsl:element name="{concat('h', @h)}">
							<xsl:value-of select="@prefix" />
							-
							<xsl:value-of select="@title" />
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="h6">
							<xsl:value-of select="@prefix" />
							-
							<xsl:value-of select="@title" />
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="tocSect">
		<xsl:call-template name="tocInfoSect" />
		<xsl:for-each select="*">
			<xsl:sort select="@position"></xsl:sort>
			<xsl:call-template name="tocSect" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="MessageDisplay" mode="toc">
		<a href="#{@ID}">
			<br></br>
			<xsl:value-of select="@Position" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="@Name" />
			-
			<xsl:value-of select="@Description" />
		</a>
	</xsl:template>


	<xsl:template match="MessageDisplay">
		<h4 style="page-break-before: always">
			<a id="{@id}" name="{@id}"></a>
			<b>
				<xsl:value-of select="@prefix" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="@Name" />
				-
				<xsl:value-of select="@Description" />
			</b>
		</h4>
		<!-- <xsl:text>Comment:</xsl:text> -->
		<xsl:value-of select="Comment" />
		<table width="1000" border="1" cellspacing="0" cellpadding="1">
			<thead style="background:#F0F0F0; color:#B21A1C; align:center">
				<tr>
					<th>
						Segment
					</th>
					<th>
						Flavor
					</th>
					<th>
						Element name
					</th>
					<th>
						Card.
					</th>
					<th>
						Usage
					</th>
					<th>
						Description/Comments
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="Elt">
					<xsl:call-template name="elt">
						<xsl:with-param name="style"
							select="'background-color:white;text-decoration:normal'">
						</xsl:with-param>
					</xsl:call-template>

				</xsl:for-each>
			</tbody>
		</table>
		<xsl:value-of select="Text[@Type='UsageNote']" />
		<br></br>
		<a href="#top">Link to top</a>
	</xsl:template>

	<xsl:template name="elt">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td>
				<xsl:value-of select="@Ref" />
			</td>
			<td>
				<xsl:value-of select="@Label" />
			</td>
			<td>
				<xsl:value-of select="@Description" />
			</td>
			<td>
				[
				<xsl:value-of select="@Min" />
				..
				<xsl:value-of select="@Max" />
				]
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
	</xsl:template>


	<xsl:template match="Segment" mode="toc">
		<a href="#{@ID}">
			<br></br>
			<xsl:value-of select="@Position" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="@Name" />
			-
			<xsl:value-of select="@Description" />
		</a>
	</xsl:template>

	<xsl:template match="Segment">
		<h4 style="page-break-before: always">
			<a id="{@id}" name="{@id}"></a>
			<xsl:value-of select="@prefix" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@Name" />
			-
			<xsl:value-of select="@Description" />
		</h4>

		<xsl:value-of select="./Text[@Type='Text1']" />

		<table width="1000" border="1" cellspacing="0" cellpadding="1">
			<thead style="background:#F0F0F0; color:#B21A1C; align:center">
				<tr>
					<th>
						Seq
					</th>
					<th>
						Element name
					</th>
					<th>
						DT
					</th>
					<th>
						Usage
					</th>
					<th>
						Card.
					</th>
					<th>
						Length
					</th>
					<th>
						Value Set
					</th>
					<th>
						Comment
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="Field">
					<xsl:sort select="@Position" data-type="number"></xsl:sort>
					<xsl:call-template name="field">
						<xsl:with-param name="style"
							select="'background-color:white;text-decoration:normal'">
						</xsl:with-param>
					</xsl:call-template>

				</xsl:for-each>
			</tbody>
		</table>

		<xsl:choose>
			<xsl:when test="normalize-space($inlineConstraints) = 'false'">
				<xsl:if test="count(Field//Constraint) &gt; 0">
					<br></br>
					<table width="1000" border="1" cellspacing="0" cellpadding="1">
						<thead>
							<tr style="background:#C0C0C0">
								<th>
									Type
								</th>
								<th>
									Constraints
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="Field">
								<xsl:sort select="@Position" data-type="number"></xsl:sort>
								<xsl:if test="node()">
									<tr>
										<td>
											<xsl:value-of select="./Constraint/@Type" />
										</td>
										<td>
											<xsl:value-of select="./Constraint" />
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
						</tbody>
					</table>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
		<br></br>
		<xsl:copy-of select="Text[@Type='Text2']" />

		<xsl:for-each select="Field">
			<xsl:sort select="@Position" data-type="number"></xsl:sort>
			<xsl:if test="count(Text) &gt; 0">

				<p>
					<b>
						<xsl:value-of select="../@Name" />
						-
						<xsl:value-of select="./@Position" />
						&#160;
						<xsl:value-of select="./@Name" />
						(
						<xsl:value-of select="./@Datatype" />
						)
					</b>
					<xsl:copy-of select="./Text[@Type='Text']" />
				</p>
			</xsl:if>
		</xsl:for-each>
		<br></br>
		<a href="#top">Link to top</a>
	</xsl:template>

	<xsl:template name="field">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td>
				<xsl:value-of select="format-number(@Position, '0')" />
			</td>
			<td>
				<xsl:value-of select="@Name" />
			</td>
			<td>
				<xsl:value-of select="@Datatype" />
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				[
				<xsl:value-of select="@Min" />
				..
				<xsl:value-of select="@Max" />
				]
			</td>
			<td>
				[
				<xsl:value-of select="@MinLength" />
				..
				<xsl:value-of select="@MaxLength" />
				]
			</td>
			<td>
				<xsl:value-of select="@Binding" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
		<xsl:if test="normalize-space($inlineConstraints) = 'true'">

			<xsl:if test="count(Constraint) &gt; 0">
				<tr>
					<td></td>
					<td style="background:#C0C0C0">
						<xsl:value-of select="./Constraint/@Type" />
					</td>
					<td colspan="9">
						<xsl:value-of select="./Constraint" />
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>


	<xsl:template match="Datatype" mode="toc">
		<a href="#{@ID}">
			<br></br>
			<xsl:value-of select="@Position" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="@Label" />
			-
			<xsl:value-of select="@Description" />
		</a>
	</xsl:template>

	<xsl:template match="Datatype">
		<h4 style="page-break-before: always">
			<a id="{@id}" name="{@id}">
				<u>
					<xsl:value-of select="@prefix" />
					<xsl:text> </xsl:text>
					<xsl:value-of select="@Label" />
					-
					<xsl:value-of select="@Description" />
				</u>
			</a>
		</h4>

		<table width="1000" border="1" cellspacing="0" cellpadding="0">
			<thead style="background:#F0F0F0; color:#B21A1C; align:center">
				<tr>
					<th>
						Seq
					</th>
					<th>
						Element name
					</th>
					<th>
						Conf length
					</th>
					<th>
						DT
					</th>
					<th>
						Usage
					</th>
					<th>
						Length
					</th>
					<th>
						Value set
					</th>
					<th>
						Comment
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="Component">
					<xsl:sort select="@Position" data-type="number"></xsl:sort>
					<xsl:call-template name="component">
						<xsl:with-param name="style"
							select="'background-color:white;text-decoration:normal'">
						</xsl:with-param>
					</xsl:call-template>

				</xsl:for-each>
			</tbody>
		</table>

		<xsl:if test="count(Field//Constraint) &gt; 0">
			<xsl:choose>
				<xsl:when test="normalize-space($inlineConstraints) = 'false'">
					<table width="1000" border="1" cellspacing="0" cellpadding="1">
						<thead>
							<tr style="background:#C0C0C0">
								<th>
									Type
								</th>
								<th>
									Constraints
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="Field">
								<xsl:sort select="@Position" data-type="number"></xsl:sort>
								<xsl:if test="node()">
									<tr>
										<td>
											<xsl:value-of select="./Constraint/@Type" />
										</td>
										<td>
											<xsl:value-of select="./Constraint" />
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
						</tbody>
					</table>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<br></br>
		<xsl:value-of select="Text[@Type='UsageNote']" />
		<br></br>
		<a href="#top">Link to top</a>
	</xsl:template>

	<xsl:template name="component">
		<xsl:param name="style" />
		<tr style="{$style}">

			<td>
				<xsl:value-of select="format-number(@Position, '0')" />
			</td>
			<td>
				<xsl:value-of select="@Name" />
			</td>
			<td>
				<xsl:value-of select="@ConfLength" />
			</td>
			<td>
				<xsl:value-of select="@Datatype" />
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				[
				<xsl:value-of select="@MinLength" />
				..
				<xsl:value-of select="@MaxLength" />
				]
			</td>
			<td>
				<xsl:value-of select="@Binding" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>

		<xsl:if test="normalize-space($inlineConstraints) = 'true'">

			<xsl:if test="count(Constraint) &gt; 0">
				<tr>
					<td></td>
					<td style="background:#C0C0C0">
						<xsl:value-of select="./Constraint/@Type" />
					</td>
					<td colspan="8">
						<xsl:value-of select="./Constraint" />
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
		<!-- <xsl:if test="node()"> <xsl:choose> <xsl:when test="normalize-space($inlineConstraints) 
			= 'true'"> <tr> <td></td> <td style="background:#C0C0C0"> <xsl:value-of select="./Constraint/@Type" 
			/> </td> <td colspan="6" style="background:#C0C0C0"> <xsl:value-of select="./Constraint" 
			/> </td> </tr> </xsl:when> <xsl:otherwise> <tr> <td colspan="8" style="background:#C0C0C0" 
			/> </tr> </xsl:otherwise> </xsl:choose> </xsl:if> -->
	</xsl:template>

	<xsl:template match="ValueSetDefinition" mode="toc">
		<a href="#{@Id}">
			<br></br>
			<xsl:value-of select="@Position" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="@BindingIdentifier" />
			-
			<xsl:value-of select="@Description" />
		</a>
	</xsl:template>

	<xsl:template match="ValueSetDefinition">
		<h4 style="page-break-before:auto">
			<a id="{@id}" name="{@id}"></a>
			<xsl:value-of select="@prefix" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@BindingIdentifier" />
			-
			<xsl:value-of select="@Name" />
		</h4>
		<xsl:text>Oid: </xsl:text>
		<xsl:value-of select="@Oid"></xsl:value-of>
		<table width="100%" border="1" cellspacing="0" cellpadding="0">
			<col style="width:15%"></col>
			<col style="width:15%"></col>
			<col style="width:10%"></col>
			<col style="width:60%"></col>
			<thead style="background:#F0F0F0; color:#B21A1C; align:center">
				<tr>
					<th>
						Value
					</th>
					<th>
						Code System
					</th>
					<th>
						Usage
					</th>
					<th>
						Description
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="ValueElement">
					<xsl:sort select="@Value" />
					<xsl:call-template name="ValueElement">
						<xsl:with-param name="style" select="'background-color:white;'">
						</xsl:with-param>
					</xsl:call-template>
				</xsl:for-each>
			</tbody>
		</table>
		<br></br>
		<a href="#top">Link to top</a>
	</xsl:template>

	<xsl:template name="ValueElement">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td>
				<xsl:value-of select="@Value" />
			</td>
			<td>
				<xsl:value-of select="@CodeSystem" />
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td>
				<xsl:value-of select="@Label" />
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>


