<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="/rendering/templates/profile/message.xsl"/>
    <xsl:import href="/rendering/templates/profile/datatype.xsl"/>
    <xsl:import href="/rendering/templates/profile/valueSet.xsl"/>
    <xsl:template name="displayProfileContent">
        <xsl:param name="inlineConstraint"/>
        <xsl:choose>
            <xsl:when test="count(MessageDisplay) &gt; 0">
                <xsl:apply-templates select="Message">
                    <xsl:sort select="@position" data-type="number"></xsl:sort>
                    <xsl:with-param name="inlineConstraint"><xsl:value-of select="$inlineConstraint"/></xsl:with-param>
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
                    <xsl:with-param name="inlineConstraint"><xsl:value-of select="$inlineConstraint"/></xsl:with-param>
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
