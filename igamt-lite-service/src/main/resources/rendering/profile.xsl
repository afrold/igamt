<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xhtml" /> <!-- note: use xhtml output to force closing of "link" tag -->
	<xsl:template match="/">
		<html>
			<head>
				<title>Profile</title>
				<link href="style1.css" rel="stylesheet" type="text/css" />
			</head>
			<body>
				<a name="top"></a>
				<a href="#segments">Segments</a>
				<a href="#fields">Fields</a>
				<a href="#datatypes">Datatypes</a>
				<a href="#components">Components</a>
				<!-- <a href="#top">link to top</a> -->
				<!-- <h2>Segments</h2> <a name="Segments"></a> <table border="1"> <tr> 
					<th>ID</th> <th>Name</th> </tr> <xsl:for-each select="ConformanceProfile/Segments/Segment"> 
					<tr> <td> <xsl:value-of select="@ID" /> </td> <td> <xsl:value-of select="@Name" 
					/> </td> </tr> </xsl:for-each> </table> -->
				<h2>Fields</h2>
				<a name="fields"></a>
				<table border="1">
					<tr>
						<th>Name</th>
						<th>Usage</th>
						<th>Datatype</th>
						<th>MinLength</th>
						<th>MaxLength</th>
						<th>Min</th>
						<th>Max</th>
						<th>ItemNo</th>
					</tr>
					<xsl:for-each select="ConformanceProfile/Segments/Segment/Field">
						<xsl:choose>
							<!-- <xsl:when test="@Usage = $usage"> -->
							<xsl:when test="@Usage = 'R'">
								<tr class="R">
									<td>
										<xsl:value-of select="@Name" />
									</td>
									<td>
										<xsl:value-of select="@Usage" />
									</td>
									<td>
										<xsl:value-of select="@Datatype" />
									</td>
									<td>
										<xsl:value-of select="@MinLength" />
									</td>
									<td>
										<xsl:value-of select="@MaxLength" />
									</td>
									<td>
										<xsl:value-of select="@Min" />
									</td>
									<td>
										<xsl:value-of select="@Max" />
									</td>
									<td>
										<xsl:value-of select="@ItemNo" />
									</td>
								</tr>
							</xsl:when>
							<xsl:otherwise>
								<tr class="O">
									<td>
										<xsl:value-of select="@Name" />
									</td>
									<td>
										<xsl:value-of select="@Usage" />
									</td>
									<td>
										<xsl:value-of select="@Datatype" />
									</td>
									<td>
										<xsl:value-of select="@MinLength" />
									</td>
									<td>
										<xsl:value-of select="@MaxLength" />
									</td>
									<td>
										<xsl:value-of select="@Min" />
									</td>
									<td>
										<xsl:value-of select="@Max" />
									</td>
									<td>
										<xsl:value-of select="@ItemNo" />
									</td>
								</tr>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</table>
				<h2>Datatypes</h2>
				<a name="datatypes"></a>
				<table border="1">
					<tr>
						<th>ID</th>
						<th>Name</th>
						<th>Description</th>
					</tr>
					<xsl:for-each select="ConformanceProfile/Datatypes/Datatype">
						<tr>
							<td>
								<xsl:value-of select="@ID" />
							</td>
							<td>
								<xsl:value-of select="@Name" />
							</td>
							<td>
								<xsl:value-of select="@Description" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<a href="#top">link to top</a>
				<h2>Components</h2>
				<a name="components"></a>
				<table border="1">
					<tr>
						<th>Name</th>
						<th>Usage</th>
						<th>Datatype</th>
						<th>MinLength</th>
						<th>MaxLength</th>
					</tr>
					<xsl:for-each select="ConformanceProfile/Datatypes/Datatype/Component">
						<tr>
							<td>
								<xsl:value-of select="@Name" />
							</td>
							<td>
								<xsl:value-of select="@Usage" />
							</td>
							<td>
								<xsl:value-of select="@Datatype" />
							</td>
							<td>
								<xsl:value-of select="@MinLength" />
							</td>
							<td>
								<xsl:value-of select="@MaxLength" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet> 