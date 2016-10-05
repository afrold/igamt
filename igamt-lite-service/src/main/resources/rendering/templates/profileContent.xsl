<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template name="displayProfileContent">
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
</xsl:stylesheet>