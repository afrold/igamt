<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="tableOfContentInfoSection.xsl"/>
    <xsl:template name="displayTableOfContentSection">
        <xsl:call-template name="displayTableOfContentInfoSection"/>
        <xsl:for-each select="*">
            <xsl:sort select="@position" data-type="number"></xsl:sort>
            <xsl:choose>
                <xsl:when test="normalize-space(@id)!=''">
                    <xsl:element name="div">
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat(@id, '_toc')"/>
                        </xsl:attribute>
                        <xsl:attribute name="class">unhidden</xsl:attribute>
                        <xsl:for-each select="*">
                            <xsl:sort select="@position" data-type="number"></xsl:sort>
                            <xsl:call-template name="displayTableOfContentSection"/>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                        <xsl:for-each select="*">
                            <xsl:sort select="@position" data-type="number"></xsl:sort>
                            <xsl:call-template name="displayTableOfContentSection"/>
                        </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="normalize-space(@id)!=''">
                <xsl:element name="div">
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat(@id, '_toc')"/>
                    </xsl:attribute>
                    <xsl:attribute name="class">unhidden</xsl:attribute>
                    <xsl:for-each select="*">
                        <xsl:sort select="@position" data-type="number"></xsl:sort>
                        <xsl:call-template name="displayTableOfContentSection"/>
                    </xsl:for-each>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>



        <!--xsl:if test="normalize-space(@id)!=''">
            <xsl:element name="div">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat(@id, '_toc')"/>
                </xsl:attribute>
                <xsl:attribute name="class">unhidden</xsl:attribute>
                <xsl:for-each select="*">
                    <xsl:sort select="@position" data-type="number"></xsl:sort>
                    <xsl:call-template name="displayTableOfContentSection"/>
                </xsl:for-each>
            </xsl:element>
        </xsl:if-->
    </xsl:template>
</xsl:stylesheet>
