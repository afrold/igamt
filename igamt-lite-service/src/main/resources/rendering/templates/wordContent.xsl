<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="section.xsl"/>
    <xsl:import href="profileContent.xsl"/>
    <xsl:template name="displayWordContent">
        <xsl:call-template name="displayProfileContent" />
        <xsl:call-template name="displaySection" />
    </xsl:template>
</xsl:stylesheet>
