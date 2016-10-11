<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="/rendering/templates/section.xsl"/>
    <xsl:import href="/rendering/templates/profileContent.xsl"/>
    <xsl:template name="displayWordContent">
        <xsl:param name="inlineConstraint"/>
        <xsl:param name="includeTOC"/>

        <xsl:element name="div">
            <xsl:attribute name="id">
                <xsl:text>main</xsl:text>
            </xsl:attribute>
            <xsl:apply-templates select="ConformanceProfile/MetaData" />
            <xsl:element name="hr"/>
        </xsl:element>

        <xsl:call-template name="displayProfileContent">
            <xsl:with-param name="inlineConstraint" select="$inlineConstraint"/>
         </xsl:call-template>
        <xsl:call-template name="displaySection">
            <xsl:with-param name="inlineConstraint" select="$inlineConstraint"/>
            <xsl:with-param name="includeTOC" select="$includeTOC"/>
        </xsl:call-template>
    </xsl:template>
</xsl:stylesheet>
