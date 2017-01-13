<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:param name="inlineConstraints" select="'false'"></xsl:param>
	<xsl:param name="includeTOC" select="'true'"></xsl:param>

	<xsl:template match="/">

		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<title>Implementation guide</title>
				<style type="text/css">

					.masterDatatypeLabel {
					color:red;
					}
					body,
					html {
					font-family: 'Arial Narrow',
					sans-serif;
					font-size: 10px;
					width:100%;
					overflow:
					auto;
					max-height: 100vh;
					}
				</style>
				<style type="text/css">

					/*!
					* froala_editor v2.2.1 (https://www.froala.com/wysiwyg-editor)
					*
					License https://froala.com/wysiwyg-editor/terms/
					* Copyright
					2014-2016 Froala Labs
					*/
					.clearfix::after{clear:both;display:block;content:""}.fr-view
					table{border:0;border-collapse:collapse;empty-cells:show;max-width:100%}.fr-view
					table.fr-dashed-borders td,.fr-view table.fr-dashed-borders
					th{border-style:dashed}.fr-view table.fr-alternate-rows tbody
					tr:nth-child(2n){background:#f5f5f5}.fr-view table td,.fr-view
					table th{border:1px solid #ddd}.fr-view table td:empty,.fr-view
					table th:empty{height:20px}.fr-view table
					td.fr-highlighted,.fr-view table th.fr-highlighted{border:1px
					double red}.fr-view table td.fr-thick,.fr-view table
					th.fr-thick{border-width:2px}.fr-view table
					th{background:#e6e6e6}.fr-view
					hr{clear:both;user-select:none;-o-user-select:none;-moz-user-select:none;-khtml-user-select:none;-webkit-user-select:none;-ms-user-select:none;page-break-after:always}.fr-view
					.fr-file{position:relative}.fr-view
					.fr-file::after{position:relative;content:"\1F4CE";font-weight:400}.fr-view
					pre{white-space:pre-wrap;word-wrap:break-word}.fr-view
					blockquote{border-left:solid 2px
					#5e35b1;margin-left:0;padding-left:5px;color:#5e35b1}.fr-view
					blockquote blockquote{border-color:#00bcd4;color:#00bcd4}.fr-view
					blockquote blockquote
					blockquote{border-color:#43a047;color:#43a047}.fr-view
					span.fr-emoticon{font-weight:400;font-family:"Apple Color
					Emoji","Segoe UI Emoji",NotoColorEmoji,"Segoe UI Symbol","Android
					Emoji",EmojiSymbols;display:inline;line-height:0}.fr-view
					span.fr-emoticon.fr-emoticon-img{font-size:inherit;height:1em;width:1em;min-height:20px;min-width:20px;display:inline-block;margin:-.2em
					.15em .2em;line-height:normal;vertical-align:middle}.fr-view
					.fr-text-gray{color:#AAA!important}.fr-view
					.fr-text-bordered{border-top:solid 1px #222;border-bottom:solid 1px
					#222;padding:10px 0}.fr-view
					.fr-text-spaced{letter-spacing:1px}.fr-view
					.fr-text-uppercase{text-transform:uppercase}.fr-view
					img{z-index:3;position:relative;overflow:auto;cursor:pointer}.fr-view
					img.fr-dib{margin:auto;display:block;float:none;vertical-align:top;margin-top:5px;margin-bottom:5px}.fr-view
					img.fr-dib.fr-fil{margin:5px auto 5px 0;left:5px}.fr-view
					img.fr-dib.fr-fir{margin:5px 0 5px auto;right:5px}.fr-view
					img.fr-dii{margin:auto;display:inline-block;float:none;margin-top:5px;margin-bottom:5px}.fr-view
					img.fr-dii.fr-fil{margin:5px 10px 5px
					0;left:5px;float:left}.fr-view img.fr-dii.fr-fir{margin:5px 0 5px
					10px;right:5px;float:right}.fr-view
					img.fr-rounded{border-radius:100%;-moz-border-radius:100%;-webkit-border-radius:100%;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box}.fr-view
					img.fr-bordered{border:solid 10px
					#CCC;-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box}.fr-view
					.fr-video{text-align:center;position:relative}.fr-view
					.fr-video>*{-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box;max-width:100%;border:0}.fr-view
					.fr-video.fr-dvb{display:block;clear:both}.fr-view
					.fr-video.fr-dvb.fr-fvl{text-align:left}.fr-view
					.fr-video.fr-dvb.fr-fvr{text-align:right}.fr-view
					.fr-video.fr-dvi{display:inline-block}.fr-view
					.fr-video.fr-dvi.fr-fvl{float:left}.fr-view
					.fr-video.fr-dvi.fr-fvr{float:right}.fr-view
					a.fr-strong{font-weight:700}.fr-view
					a.fr-green{color:green}a.fr-view.fr-strong{font-weight:700}a.fr-view.fr-green{color:green}img.fr-view{z-index:3;position:relative;overflow:auto;cursor:pointer}img.fr-view.fr-dib{margin:auto;display:block;float:none;vertical-align:top;margin-top:5px;margin-bottom:5px}img.fr-view.fr-dib.fr-fil{margin:5px
					auto 5px 0;left:5px}img.fr-view.fr-dib.fr-fir{margin:5px 0 5px
					auto;right:5px}img.fr-view.fr-dii{margin:auto;display:inline-block;float:none;margin-top:5px;margin-bottom:5px}img.fr-view.fr-dii.fr-fil{margin:5px
					10px 5px 0;left:5px;float:left}img.fr-view.fr-dii.fr-fir{margin:5px
					0 5px
					10px;right:5px;float:right}img.fr-view.fr-rounded{border-radius:100%;-moz-border-radius:100%;-webkit-border-radius:100%;-moz-background-clip:padding;-webkit-background-clip:padding-box;background-clip:padding-box}img.fr-view.fr-bordered{border:solid
					10px
					#CCC;-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box}
				</style>
			</head>

			<body style="font-family:Arial Narrow, Arial, sans-serif;">
				<xsl:call-template name="dispProfileContent" />
				<xsl:call-template name="dispSect" />
			</body>
		</html>
	</xsl:template>

	<xsl:template name="dispInfoSect" mode="disp">
		<xsl:if test="name() = 'Section'">
				<u>
					<xsl:choose>
						<xsl:when test="@h &lt; 7 and normalize-space($includeTOC) = 'true'">
							<xsl:element name="{concat('h', @h)}">
								<xsl:if test="@prefix != ''">
								<xsl:value-of select="@prefix" />
								-
								</xsl:if>
								<xsl:if test="@scope = 'MASTER'">
									<xsl:element name="span">
										<xsl:attribute name="class"><xsl:text>masterDatatypeLabel</xsl:text></xsl:attribute>
										<xsl:text>MAS</xsl:text>
									</xsl:element>
									<xsl:element name="span">
										<xsl:text> - </xsl:text>
									</xsl:element>
								</xsl:if>
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
						<xsl:when test="@h &gt; 7 and normalize-space($includeTOC) = 'true'">
							<xsl:element name="h6">
								<xsl:value-of select="@prefix" />
								-
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
						<xsl:when test="@h &lt; 7 and normalize-space($includeTOC) = 'false'">
							<xsl:element name="{concat('h', @h)}">
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
						<xsl:when test="@h &gt; 7 and normalize-space($includeTOC) = 'true'">
							<xsl:element name="h6">
								<xsl:value-of select="@prefix" />
								-
								<xsl:value-of select="@title" />
							</xsl:element>
						</xsl:when>
					</xsl:choose>
				</u>
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
		<p style="font-size:250%; text-align:center">
			<strong>
				<xsl:value-of select="@Name"></xsl:value-of>
			</strong>
		</p>
		<br></br>
		<p style="font-size:200%; text-align:center">
			<xsl:value-of select="@Subtitle"></xsl:value-of>
		</p>
		<br></br>
		<p style="font-size:100%; text-align:center">
			<xsl:value-of select="@Date"></xsl:value-of>
		</p>
		<br></br>
		<br></br>
		<p style="font-size:100%; text-align:center">
			<xsl:text>HL7 version </xsl:text>
			<xsl:value-of select="@HL7Version"></xsl:value-of>
		</p>
		<br></br>
		<p style="font-size:80%; text-align:center">
			<xsl:text>Document version </xsl:text>
			<xsl:value-of select="@DocumentVersion"></xsl:value-of>
		</p>
		<br></br>
		<p style="font-size:80%; text-align:center">
			<xsl:value-of select="@OrgName"></xsl:value-of>
		</p>
		<p style="font-size:62.5; text-align:center">
		</p>
	</xsl:template>

	<xsl:template name="dispSectContent">
		<xsl:value-of disable-output-escaping="yes" select="SectionContent" />
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
			<xsl:attribute name="class"><xsl:value-of select="concat('divh', @h)" /></xsl:attribute>
			<xsl:if test="@prefix != ''">
				<xsl:value-of select="@prefix" />
				-
			</xsl:if>
			<xsl:if test="@scope = 'MASTER'">
				<xsl:element name="span">
					<xsl:attribute name="class"><xsl:text>masterDatatypeLabel</xsl:text></xsl:attribute>
					<xsl:text>MAS</xsl:text>
				</xsl:element>
				<xsl:element name="span">
					<xsl:text> - </xsl:text>
				</xsl:element>
			</xsl:if>
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
			</xsl:choose>
			<br></br>
		</xsl:if>
	</xsl:template>

	<xsl:template match="MessageDisplay">
		<xsl:value-of select="@Comment" />
		<p>
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
		<xsl:if test="count(./Constraint) &gt; 0">
			<xsl:choose>
				<xsl:when test="normalize-space($inlineConstraints) = 'false'">
					<xsl:if test="count(./Constraint[@Type='cs']) &gt; 0">
						<p>
							<strong>
								<u>Conformance statements</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="csheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='cs']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
					<xsl:if test="count(./Constraint[@Type='pre']) &gt; 0">
						<p>
							<strong>
								<u>Conditional predicates</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="preheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='pre']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		</p>
		<xsl:value-of disable-output-escaping="yes"
			select="./Text[@Type='UsageNote']" />
		<br></br>
	</xsl:template>

	<xsl:template name="elt">
		<xsl:param name="style" />
		<tr style="{$style}">
			<td style="width:10%">
				<xsl:value-of select="@Ref" />
			</td>
			<td style="width:20%">
				<xsl:value-of select="@Label" />
			</td>
			<td style="width:20%">
				<xsl:value-of select="@Description" />
			</td>
			<td style="width:10%">
				<xsl:if test="(normalize-space(@Min)!='') and (normalize-space(@Max)!='')">
					[
					<xsl:value-of select="@Min" />
					..
					<xsl:value-of select="@Max" />
					]
				</xsl:if>
			</td>
			<td style="width:10%">
				<xsl:value-of select="@Usage" />
			</td>
			<td style="width:30%">
				<xsl:value-of select="@Comment" />
			</td>
		</tr>
	</xsl:template>


	<xsl:template match="Segment" mode="toc">
		<a href="#{@ID}">
			<br></br>
			<xsl:value-of select="@Name" />
			-
			<xsl:value-of select="@Description" />
		</a>
	</xsl:template>

	<xsl:template match="Segment">
		<xsl:value-of select="@Comment"></xsl:value-of>
		<xsl:if test="count(./Text[@Type='Text1']) &gt; 0">
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text1']" />
			</p>
		</xsl:if>
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
							Concept Domain
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
				<xsl:if test="count(Field//Constraint[@Type='cs']) &gt; 0">
					<p>
						<strong>
							<u>Conformance statements</u>
						</strong>
						<table width="100%" border="1" cellspacing="0" cellpadding="1">
							<xsl:call-template name="csheader"></xsl:call-template>
							<tbody>
								<xsl:for-each select="Field/Constraint[@Type='cs']">
									<xsl:sort select="@Position" data-type="number"></xsl:sort>
									<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
								</xsl:for-each>
							</tbody>
						</table>
					</p>
				</xsl:if>
				<xsl:if test="count(Field//Constraint[@Type='pre']) &gt; 0">
					<p>
						<strong>
							<u>Conditional predicates</u>
						</strong>
						<table width="100%" border="1" cellspacing="0" cellpadding="1">
							<xsl:call-template name="preheader"></xsl:call-template>
							<tbody>
								<xsl:for-each select="Field/Constraint[@Type='pre']">
									<xsl:sort select="@Position" data-type="number"></xsl:sort>
									<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
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
				<xsl:if test="(normalize-space(@Min)!='') and (normalize-space(@Max)!='')">
					[
					<xsl:value-of select="@Min" />
					..
					<xsl:value-of select="@Max" />
					]
				</xsl:if>
			</td>
			<td>
				<xsl:if test="(normalize-space(@MinLength)!='') and (normalize-space(@MaxLength)!='')">
					[
					<xsl:value-of select="@MinLength" />
					..
					<xsl:value-of select="@MaxLength" />
					]
				</xsl:if>
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
				<xsl:apply-templates select="." mode="inlineSgt"></xsl:apply-templates>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="Datatype" mode="toc">
		<a href="#{@ID}">
			<br></br>
			<xsl:value-of select="@Label" />
			-
			<xsl:value-of select="@Description" />
		</a>
	</xsl:template>

	<xsl:template match="Datatype">
		<xsl:if test="count(Text[@Type='PurposeAndUse']) &gt; 0">
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="Text[@Type='PurposeAndUse']" />
			</p>
		</xsl:if>
		<xsl:value-of select="@Comment"></xsl:value-of>
		<xsl:if test="count(Text[@Type='UsageNote']) &gt; 0">
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="Text[@Type='UsageNote']" />
			</p>
		</xsl:if>
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
							Concept Domain
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

	<xsl:if test="count(./Constraint) &gt; 0">
			<xsl:choose>
				<xsl:when test="normalize-space($inlineConstraints) = 'false'">
					<xsl:if test="count(./Constraint[@Type='cs']) &gt; 0">
						<p>
							<strong>
								<u>Conformance statements</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="csheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='cs']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
					<xsl:if test="count(./Constraint[@Type='pre']) &gt; 0">
						<p>
							<strong>
								<u>Conditional predicates</u>
							</strong>
							<table width="100%" border="1" cellspacing="0" cellpadding="1">
								<xsl:call-template name="preheader"></xsl:call-template>
								<tbody>
									<xsl:for-each select="./Constraint[@Type='pre']">
										<xsl:sort select="@Position" data-type="number"></xsl:sort>
										<xsl:apply-templates select="." mode="standalone"></xsl:apply-templates>
									</xsl:for-each>
								</tbody>
							</table>
						</p>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="count(./Component/Text[@Type='Text']) &gt; 0">
			<h4>Components Definition Texts</h4>
			<xsl:for-each select="Component">
				<xsl:sort select="@Position" data-type="number"></xsl:sort>
				<xsl:if test="count(./Text[@Type='Text']) &gt; 0">
					<p>
						<strong>
							<xsl:value-of disable-output-escaping="yes" select="concat(../@Name, '-', @Position, ':', @Name)" />
						</strong>
						<xsl:value-of disable-output-escaping="yes"
							select="./Text[@Type='Text']" />
					</p>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="count(./Text[@Type='Text2']) &gt; 0">
			<h4>
				post-definition:
			</h4>
				<xsl:if test="count(./Text[@Type='Text']) &gt; 0">
					<p>
						<u><xsl:value-of select="./Text[@Type='Name']" />: </u>
						<xsl:value-of disable-output-escaping="yes"
							select="./Text[@Type='Text']" />
					</p>
				</xsl:if>
			<p>
				<xsl:value-of disable-output-escaping="yes"
					select="./Text[@Type='Text2']" />
			</p>
		</xsl:if>
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
				<xsl:if test="(normalize-space(@MinLength)!='') and (normalize-space(@MaxLength)!='')">
					[
					<xsl:value-of select="@MinLength" />
					..
					<xsl:value-of select="@MaxLength" />
					]
				</xsl:if>
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
				<xsl:apply-templates select="." mode="inlineDt"></xsl:apply-templates>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ValueSetDefinition" mode="toc">
		<a href="#{@Id}">
			<br></br>
			<xsl:value-of select="@BindingIdentifier" />
			-
			<xsl:value-of select="@Description" />
		</a>
	</xsl:template>

	<xsl:template match="ValueSetDefinition">
		<xsl:if test="@Stability != ''">
			<p><xsl:text>Stability: </xsl:text>
			<xsl:value-of select="@Stability"></xsl:value-of>
			</p>
		</xsl:if>
		<xsl:if test="@Extensibility != ''">
			<p><xsl:text>Extensibility: </xsl:text>
			<xsl:value-of select="@Extensibility"></xsl:value-of></p>
		</xsl:if>
		<xsl:if test="@ContentDefinition != ''">
			<p><xsl:text>Content Definition: </xsl:text>
			<xsl:value-of select="@ContentDefinition"></xsl:value-of></p>
		</xsl:if>
		<xsl:if test="@Oid != ''">
			<p><xsl:text>Oid: </xsl:text>
			<xsl:value-of select="@Oid"></xsl:value-of></p>
		</xsl:if>
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
						<xsl:call-template name="csheader"></xsl:call-template>
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
						<xsl:call-template name="preheader"></xsl:call-template>
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

	<!-- Conformance statement header -->
	<xsl:template name="csheader">
		<col style="width:10%"></col>
		<col style="width:10%"></col>
		<col style="width:80%"></col>
		<thead>
			<tr style="background:#F0F0F0; color:#B21A1C; align:center">
				<th>
					Id
				</th>
				<th>
					Location
				</th>
				<th>
					Description
				</th>
			</tr>
		</thead>

	</xsl:template>

	<!-- Predicate header -->
	<xsl:template name="preheader">
		<col style="width:10%"></col>
		<col style="width:10%"></col>
		<col style="width:80%"></col>
		<thead style="background:#F0F0F0; color:#B21A1C; align:center">
			<tr>
				<th>
					Location
				</th>
				<th>
					Usage
				</th>
				<th>
					Description
				</th>
			</tr>
		</thead>
	</xsl:template>

	<!-- Parse constraint for inline mode segment -->
	<xsl:template match="Constraint" mode="inlineSgt">
		<xsl:variable name="precolspan" select="4"></xsl:variable>
		<xsl:variable name="cscolspan" select="4"></xsl:variable>
		<xsl:if test="./@Type='pre'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="$precolspan" />
				</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="number($cscolspan)" />	
				</xsl:attribute>
					<xsl:value-of select="@Id" />
					:
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Parse constraint for inline mode datatype -->
	<xsl:template match="Constraint" mode="inlineDt">
		<xsl:variable name="precolspan" select="4"></xsl:variable>
		<xsl:variable name="cscolspan" select="5"></xsl:variable>
		<xsl:if test="./@Type='pre'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="$precolspan" />
				</xsl:attribute>
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="background-color:#E8E8E8;text-decoration:normal">
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<xsl:element name="td">
					<xsl:attribute name="colspan">
				<xsl:value-of select="number($cscolspan)" />	
				</xsl:attribute>
					<xsl:value-of select="@Id" />
					:
					<xsl:value-of select="." />
				</xsl:element>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Parse constraint for standalone mode -->
	<xsl:template match="Constraint" mode="standalone">
		<xsl:if test="./@Type='pre'">
			<tr style="background-color:white;text-decoration:normal">
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="@Usage" />
				</td>
				<td>
					<xsl:value-of select="." />
				</td>
			</tr>

		</xsl:if>
		<xsl:if test="./@Type='cs'">
			<tr style="background-color:white;text-decoration:normal">
				<td>
					<xsl:value-of select="@Id" />
				</td>
				<td>
					<xsl:value-of select="concat(@LocationName, @Location)" />
				</td>
				<td>
					<xsl:value-of select="." />
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>


