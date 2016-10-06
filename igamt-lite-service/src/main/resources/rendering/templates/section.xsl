<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="infoSection.xsl"/>

    <xsl:template name="displaySection">
        <!-- &#xA0; -->
        <xsl:call-template name="displayInfoSection"/>
        <xsl:for-each select="*">
            <xsl:sort select="@position" data-type="number"></xsl:sort>
            <xsl:call-template name="displaySection"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
