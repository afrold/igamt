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
				<title>Conformance profile</title>
			</head>

			<body style="font-family:Arial Narrow, Arial, sans-serif;">
				<a name="top"></a>
				<br></br>
				<a href="#messages">Messages definifion</a>
				<br></br>

				<a href="#segments">Segments definition</a>
				<br></br>
				<a href="#datatypes">Datatypes definition</a>
				<br></br>
				<a href="#valuesets">Value sets</a>
				<br></br>

				<!-- <xsl:value-of select="$inlineConstraints" /> -->

				<h2>
					<u>Messages definition</u>
				</h2>
				<a name="messages"></a>
				<xsl:apply-templates select="ConformanceProfile/MessagesDisplay">
				</xsl:apply-templates>
				<a name="segments"></a>

				<h2>
					<u>Segments definition</u>
				</h2>
				<xsl:apply-templates select="ConformanceProfile/Segments">
				</xsl:apply-templates>
				<a name="datatypes"></a>
				<h2>
					<u>Datatypes definition</u>
				</h2>
				<xsl:apply-templates select="ConformanceProfile/Datatypes">
				</xsl:apply-templates>
				<h2>
					<u>Value sets definition</u>
				</h2>
				<a name="valuesets"></a>
				<xsl:apply-templates select="ConformanceProfile/Tables">
					<xsl:sort select="@Id"></xsl:sort>
				</xsl:apply-templates>
			</body>
		</html>

	</xsl:template>
	<xsl:template match="MessageDisplay">
		<h3 style="page-break-before: always">
			Message Definition:
			<xsl:value-of select="@Description" />
			<br/>
			Profile:
			<xsl:value-of select="@StructID" />
		</h3>
		<table width="1000" border="1" cellspacing="0" cellpadding="1">
			<thead>
				<tr style="background:#0033CC; color:white">
					<th>
						Segment
					</th>
					<th>
						Card.
					</th>
					<th>
						Local Card.
					</th>
					<th>
						Usage
					</th>
					<th>
						Local Usage
					</th>
					<th>
						Comment
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="Elt">
					<xsl:if test="@Diff = 'del'">
						<xsl:call-template name="elt">
							<xsl:with-param name="style"
								select="'background-color:red;text-decoration:line-through'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'add'">
						<xsl:call-template name="elt">
							<xsl:with-param name="style" select="'background-color:green;'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'edit'">
						<xsl:call-template name="elt">
							<xsl:with-param name="style" select="'background-color:yellow;'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>

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
				[
				<xsl:value-of select="@Min" />
				..
				<xsl:value-of select="@Max" />
				]
			</td>
			<td>
			</td>
			<td>
				<xsl:value-of select="@Usage" />
			</td>
			<td></td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
	</xsl:template>


	<xsl:template match="Segment">
		<h3 style="page-break-before: always">
			<xsl:value-of select="@Name" />
			:
			<xsl:value-of select="@Description" />
		</h3>

		<!-- <span style="background-color:#FFFF00"> <xsl:value-of select="./Text[@Type='Text1']" 
			/> </span> -->
		<xsl:value-of select="./Text[@Type='Text1']" />
		<table width="1000" border="1" cellspacing="0" cellpadding="1">
			<thead style="background:#0033CC;color:white;align:center">
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
						STD Usage
					</th>
					<th>
						Local Usage
					</th>
					<th>
						STD card.
					</th>
					<th>
						Local card.
					</th>
					<th>
						Len
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
					<xsl:if test="@Diff = 'del'">
						<xsl:call-template name="field">
							<xsl:with-param name="style"
								select="'background-color:red;text-decoration:line-through'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'add'">
						<xsl:call-template name="field">
							<xsl:with-param name="style" select="'background-color:green;'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'edit'">
						<xsl:call-template name="field">
							<xsl:with-param name="style" select="'background-color:yellow;'">
							</xsl:with-param>
						</xsl:call-template>

					</xsl:if>

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
			<td></td>
			<td>
				[
				<xsl:value-of select="@Min" />
				..
				<xsl:value-of select="@Max" />
				]
			</td>
			<td></td>
			<td>
				[
				<xsl:value-of select="@MinLength" />
				..
				<xsl:value-of select="@MaxLength" />
				]
			</td>
			<td>
				<xsl:value-of select="@Table" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
		<xsl:if test="normalize-space($inlineConstraints) = 'true'">

			<xsl:if test="count(Constraint) &gt; 0">
				<tr>
					<td></td>
					<td colspan="2" style="background:#C0C0C0">
						<xsl:value-of select="./Constraint/@Type" />
					</td>
					<td colspan="7">
						<xsl:value-of select="./Constraint" />
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="Datatype">
		<h3 style="page-break-before: always">
			<xsl:value-of select="@Label" />
			:
			<xsl:value-of select="@Description" />
		</h3>

		<table width="1000" border="1" cellspacing="0" cellpadding="0">
			<thead style="background:#0033CC;color:white;align:center">
				<tr>
					<th>
						Seq
					</th>
					<th>
						Element name
					</th>
					<th>
						Conf len
					</th>
					<th>
						DT
					</th>
					<th>
						Usage
					</th>
					<th>
						Len
					</th>
					<th>
						Table
					</th>
					<th>
						Comment
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="Component">
					<xsl:sort select="@Position" data-type="number"></xsl:sort>
					<xsl:if test="@Diff = 'del'">
						<xsl:call-template name="component">
							<xsl:with-param name="style"
								select="'background-color:red;text-decoration:line-through'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'add'">
						<xsl:call-template name="component">
							<xsl:with-param name="style" select="'background-color:green;'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'edit'">
						<xsl:call-template name="component">
							<xsl:with-param name="style" select="'background-color:yellow;'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
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
			<td></td>
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
				<xsl:value-of select="@Table" />
			</td>
			<td>
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
		<xsl:if test="node()">
			<xsl:choose>
				<xsl:when test="normalize-space($inlineConstraints) = 'true'">
					<tr>
						<td></td>
						<td colspan="2" style="background:#C0C0C0">
							<xsl:value-of select="./Constraint/@Type" />
						</td>
						<td colspan="5">
							<xsl:value-of select="./Constraint" />
						</td>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<td colspan="8" style="background:#C0C0C0" />
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="TableDefinition">
		<h3 style="page-break-before: always">
			<xsl:value-of select="@Id" />
			:
			<xsl:value-of select="@Name" />
		</h3>
		<table width="100%" border="1" cellspacing="0" cellpadding="0">
			<col style="width:15%"></col>
			<col style="width:15%"></col>
			<col style="width:70%"></col>
			<thead style="background:#0033CC; color:white; align:center">
				<tr>
					<th>
						Value
					</th>
					<th>
						Codesys
					</th>
					<th>
						Description
					</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="TableElement">
					<xsl:sort select="@Code" />
					<xsl:if test="@Diff = 'del'">
						<xsl:call-template name="tableElement">
							<xsl:with-param name="style"
								select="'background-color:red;text-decoration:line-through'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'add'">
						<xsl:call-template name="tableElement">
							<xsl:with-param name="style" select="'background-color:green;'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@Diff = 'edit'">
						<xsl:call-template name="tableElement">
							<xsl:with-param name="style" select="'background-color:yellow;'">
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</xsl:for-each>
			</tbody>
		</table>
		<br></br>
		<a href="#top">Link to top</a>
	</xsl:template>

	<xsl:template name="tableElement">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td>
				<xsl:value-of select="@Code" />
			</td>
			<td>
				<xsl:value-of select="@Codesys" />
			</td>
			<td>
				<xsl:value-of select="@DisplayName" />
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>


