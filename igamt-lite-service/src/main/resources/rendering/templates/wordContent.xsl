<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="/rendering/templates/section.xsl"/>
    <xsl:import href="/rendering/templates/profileContent.xsl"/>
    <xsl:template name="displayWordContent">
        <xsl:param name="inlineConstraint"/>
        <xsl:param name="includeTOC"/>
        <xsl:call-template name="displayProfileContent">
            <xsl:with-param name="inlineConstraint" select="$inlineConstraint"/>
        </xsl:call-template>
        <xsl:call-template name="displaySection">
            <xsl:with-param name="inlineConstraint" select="$inlineConstraint"/>
            <xsl:with-param name="includeTOC" select="$includeTOC"/>
            <xsl:with-param name="target" select="'word'"/>
        </xsl:call-template>
    </xsl:template>
</xsl:stylesheet>
