<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="utf-8"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		indent="yes" />
	<xsl:param name="inlineConstraints" select="'false'"></xsl:param>
	<xsl:template match="/">

		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>Implementation guide</title>
				<style type="text/css">
					body,
					html {
					font-family: 'Arial Narrow',
					sans-serif;
					}

					/* unvisited link */
					#sidebar a:link {
					color: #000066;
					margin-top:
					1px;
					margin-bottom: 1px;
					/* margin-right: 150px;
					margin-left: 80px;
					*/
					}

					/* visited link */
					#sidebar a:visited {
					color:
					green;
					margin-top: 1px;
					margin-bottom: 1px;
					}

					/* mouse over link */
					#sidebar a:hover {
					color: hotpink;
					margin-top:
					1px;
					margin-bottom:
					1px;
					}

					/* selected link */
					#sidebar a:active {
					color: blue;
					margin-top:
					1px;
					margin-bottom: 1px;
					}
					#main {
					float:right;
					width:100%;
					overflow:
					auto;
					max-height: 100vh;
					}
					.divh1{
					padding-left: 15px;
					}
					.divh2 {
					padding-left: 30px;
					}
					.divh3 {
					padding-left: 45px;
					}
					.divh4 {
					padding-left: 60px;
					}
					.divh5 {
					padding-left:
					75px;
					}
					.divh6 {
					padding-left:
					90px;
					}
					.hidden {
					display: none;
					}
					.unhidden {
					display:
					block;
					}
					.btn {
					float:right;
					}
				</style>
			</head>

			<body style="font-family:Arial Narrow, Arial, sans-serif;">
				<div id="main">
					<xsl:apply-templates select="ConformanceProfile/MetaData" />
					<hr></hr>
					<a name="top"></a>
					<xsl:call-template name="dispSect" />
				</div>
				<script type="text/javascript">
					<xsl:text disable-output-escaping="yes">
					function unhide(divID, btnID) {
					var oLimit = document.querySelector("#sidebar");
					var divs = document.querySelectorAll("div");
					for (var i = 0; i &lt; divs.length; i++) {
					if (divs[i].id == divID) {
					divs[i].className = (divs[i].className=='hidden')?'unhidden':'hidden';
					}
					}
					document.getElementById(btnID).innerHTML = ((document.getElementById(divID).className=='hidden')? "[Show]":"[Hide]");
					}</xsl:text>
				</script>
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
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:element name="h6">
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:otherwise>
					</xsl:choose>
				</u>
			</a>
			<br />
			<xsl:call-template name="dispSectContent" />
			<xsl:call-template name="dispProfileContent" />

		</xsl:if>
	</xsl:template>

	<xsl:template match="MetaData">
		<p style="text-align:center">
			<xsl:element name="img">
				<xsl:attribute name="src">
					<xsl:text>http://hit-2015.nist.gov/docs/hl7Logo.png</xsl:text>
				</xsl:attribute>
			</xsl:element>
		</p>
		<p style="font-size:20px; text-align:center">
			<xsl:value-of select="@Name"></xsl:value-of>
		</p>
		<br></br>
		<p style="font-size:16px; text-align:center">
			<xsl:value-of select="@Subtitle"></xsl:value-of>
		</p>
		<p style="font-size:14px; text-align:center">
			<xsl:value-of select="@Date"></xsl:value-of>
		</p>
		<br></br>
		<br></br>
		<p style="font-size:14px; text-align:center">
			<xsl:text>HL7 version </xsl:text>
			<xsl:value-of select="@HL7Version"></xsl:value-of>
		</p>
		<br></br>
		<p style="font-size:14px; text-align:center">
			<xsl:text>Document version </xsl:text>
			<xsl:value-of select="@DocumentVersion"></xsl:value-of>
		</p>
		<p style="font-size:14px; text-align:center">
			<xsl:value-of select="@OrgName"></xsl:value-of>
		</p>
		<!-- <br></br> <xsl:value-of select="@Ext"></xsl:value-of> <br></br> <xsl:value-of 
			select="@Status"></xsl:value-of> <br></br> <xsl:value-of select="@Topics"></xsl:value-of> 
			<br></br> -->

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
			<xsl:when test="count(Constraints) &gt; 0">
				<xsl:apply-templates select="Constraints">
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="dispSect">
		<!-- &#xA0; -->
		<xsl:call-template name="dispInfoSect" />
		<xsl:for-each select="*">
			<xsl:sort select="@position" data-type="number"></xsl:sort>
			<xsl:call-template name="dispSect" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="tocInfoSect">
		<xsl:if test="name() = 'Section'">
			<a href="#{@id}">
				<xsl:attribute name="class"><xsl:value-of select="concat('divh', @h)" /></xsl:attribute>
				<xsl:value-of select="@title" />
				<xsl:choose>
					<xsl:when test="count(Section) &gt; 0">
						<xsl:element name="a">
							<xsl:attribute name="href">javascript:unhide('<xsl:value-of
								select="concat(@id, '_toc')" />', '<xsl:value-of
								select="concat(@id, '_btn')" />');</xsl:attribute>
							<xsl:element name="div">
								<xsl:attribute name="id"><xsl:value-of
									select="concat(@id, '_btn')" /></xsl:attribute>
								<xsl:attribute name="class">unhidden btn</xsl:attribute>
								[Hide]
							</xsl:element>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</a>
			<br></br>
		</xsl:if>
	</xsl:template>

	<xsl:template name="tocSect">
		<xsl:call-template name="tocInfoSect" />
		<xsl:element name="div">
			<xsl:attribute name="id"><xsl:value-of select="concat(@id, '_toc')" /></xsl:attribute>
			<xsl:attribute name="class">unhidden</xsl:attribute>
			<xsl:for-each select="*">
				<xsl:sort select="@position" data-type="number"></xsl:sort>
				<xsl:call-template name="tocSect" />
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template match="MessageDisplay">
		<xsl:value-of select="Comment" />
		<table width="100%" border="1" cellspacing="0" cellpadding="1">
			<col style="width:10%"></col>
			<col style="width:20%"></col>
			<col style="width:20%"></col>
			<col style="width:10%"></col>
			<col style="width:10%"></col>
			<col style="width:30%"></col>
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
						Cardinality
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
		<xsl:value-of disable-output-escaping="yes"
			select="./Text[@Type='UsageNote']" />
		<br></br>
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
		<xsl:value-of disable-output-escaping="yes"
			select="./Text[@Type='Text1']" />
		<p>
			<table width="100%" border="1" cellspacing="0" cellpadding="1">
				<col style="width:5%"></col>
				<col style="width:15%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:30%"></col>
				<thead style="background:#F0F0F0; color:#B21A1C; align:center">
					<tr>
						<th>
							Seq
						</th>
						<th>
							Element name
						</th>
						<th>
							Data type
						</th>
						<th>
							Usage
						</th>
						<th>
							Cardinality
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
		</p>

		<xsl:choose>
			<xsl:when test="normalize-space($inlineConstraints) = 'false'">
				<xsl:if test="count(Field//Constraint) &gt; 0">
					<br></br>
					<p>
						<table width="100%" border="1" cellspacing="0" cellpadding="1">
							<col style="width:10%"></col>
							<col style="width:10%"></col>
							<col style="width:10%"></col>
							<col style="width:70%"></col>
							<thead>
								<tr style="background:#F0F0F0; color:#B21A1C; align:center">
									<th>
										Id
									</th>
									<th>
										Location
									</th>
									<th>
										Classification/Usage
									</th>
									<th>
										Description
									</th>
								</tr>
							</thead>
							<tbody>
								<xsl:for-each select="Field">
									<xsl:sort select="@Type" data-type="text"></xsl:sort>
									<xsl:sort select="@Position" data-type="number"></xsl:sort>
									<xsl:apply-templates select="." mode="inline"></xsl:apply-templates>
								</xsl:for-each>
							</tbody>
						</table>
					</p>
				</xsl:if>
			</xsl:when>
		</xsl:choose>
		<xsl:value-of disable-output-escaping="yes"
			select="./Text[@Type='Text2']" />

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
					<xsl:value-of disable-output-escaping="yes"
						select="./Text[@Type='Text']" />
				</p>
			</xsl:if>
		</xsl:for-each>
		<br></br>
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
				<xsl:apply-templates select="." mode="inline"></xsl:apply-templates>
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
		<xsl:value-of disable-output-escaping="yes"
			select="Text[@Type='UsageNote']" />
		<p>
			<table width="100%" border="1" cellspacing="0" cellpadding="0">
				<col style="width:5%"></col>
				<col style="width:15%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:10%"></col>
				<col style="width:30%"></col>
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
							Data type
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
		</p>

		<xsl:if test="count(Component//Constraint) &gt; 0">
			<xsl:choose>
				<xsl:when test="normalize-space($inlineConstraints) = 'false'">
					<xsl:if test="count(Component//Constraint[@Type='cs']) &gt; 0">
						<br></br>
						<table width="100%" border="1" cellspacing="0" cellpadding="1">
							<thead>
								<tr style="background:#F0F0F0; color:#B21A1C; align:center">
									<th>
										Id
									</th>
									<th>
										Location
									</th>
									<th>
										Classification/Usage
									</th>
									<th>
										Description
									</th>
								</tr>
							</thead>
							<tbody>
								<xsl:for-each select="Component">
									<xsl:sort select="@Type" data-type="text"></xsl:sort>
									<xsl:sort select="@Position" data-type="number"></xsl:sort>
									<xsl:apply-templates select="." mode="inline"></xsl:apply-templates>
								</xsl:for-each>
							</tbody>
						</table>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:if>

		<xsl:for-each select="Component">
			<xsl:sort select="@Position" data-type="number"></xsl:sort>
			<xsl:call-template name="componentText">
			</xsl:call-template>
			<br></br>
		</xsl:for-each>
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
				<xsl:apply-templates select="." mode="inline"></xsl:apply-templates>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="componentText">
		<xsl:value-of disable-output-escaping="yes"
			select="Text[@Type='Text']" />
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
		<!-- <br></br> -->
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


	<xsl:template match="Constraints">
		<xsl:if test="count(./Constraint) &gt; 0">
			<b>
				<xsl:value-of select="@title" />
			</b>
			<br></br>
			<p>
				<xsl:if test="./@Type='ConditionPredicate'">
					<table width="100%" border="1" cellspacing="0" cellpadding="0">
						<col style="width:10%"></col>
						<col style="width:10%"></col>
						<col style="width:10%"></col>
						<col style="width:70%"></col>
						<thead style="background:#F0F0F0; color:#B21A1C; align:center">
							<tr>
								<th>
									Location
								</th>
								<th>
									Usage
								</th>
								<th colspan='2'>
									Description
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="./Constraint">
								<xsl:sort select="@Position" data-type="number"></xsl:sort>
								<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
							</xsl:for-each>
						</tbody>
					</table>
					<br></br>
				</xsl:if>
				<xsl:if test="./@Type='ConformanceStatement'">
					<table width="100%" border="1" cellspacing="0" cellpadding="0">
						<col style="width:10%"></col>
						<col style="width:10%"></col>
						<col style="width:10%"></col>
						<col style="width:70%"></col>
						<thead style="background:white; color:#B21A1C; align:center">
							<tr>
								<th>
									Id
								</th>
								<th>
									Location
								</th>
								<th>
									Classification
								</th>
								<th>
									Description
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="./Constraint">
								<xsl:sort select="@Position" data-type="number"></xsl:sort>
								<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
							</xsl:for-each>
						</tbody>
					</table>
					<br />
				</xsl:if>
			</p>
		</xsl:if>
	</xsl:template>

	<!-- Parse constraint for inline mode -->
	<xsl:template match="Constraint" mode="inline">
		<xsl:if test="./@Type='pre'">
			<tr style="'background-color:#E8E8E8;text-decoration:normal'">
				<td>
				</td>
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<td colspan='4'>
					<xsl:value-of select="." />
				</td>
			</tr>

		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="'background-color:#E8E8E8;text-decoration:normal'">
				<td>
					<xsl:value-of select="@Id" />
				</td>
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="@Classification" />
				</td>
				<td colspan='4'>
					<xsl:value-of select="." />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Parse constraint for standalone mode -->
	<xsl:template match="Constraint" mode="standalone">
		<xsl:if test="./@Type='pre'">
			<tr style="'background-color:white;text-decoration:normal'">
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<td colspan='2'>
					<xsl:value-of select="." />
				</td>
			</tr>

		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="'background-color:#E8E8E8;text-decoration:normal'">
				<td>
					<xsl:value-of select="@Id" />
				</td>
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="@Classification" />
				</td>
				<td>
					<xsl:value-of select="." />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>


