<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="displayMasterDatatypeLabel" select="'false'"></xsl:param>
    <xsl:template name="displayTableOfContentInfoSection">
        <xsl:if test="name() = 'Section'">
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:value-of select="concat('#',@id)"/>
                </xsl:attribute>
                <xsl:attribute name="class">
                    <xsl:value-of select="concat('divh', @h)"/>
                </xsl:attribute>
                <xsl:if test="@prefix != ''">
                    <xsl:value-of select="concat(@prefix,' - ')"/>
                </xsl:if>
                <xsl:if test="$displayMasterDatatypeLabel='true'">
                    <xsl:if test="@scope = 'MASTER'">
                        <xsl:element name="span">
                            <xsl:attribute name="class">
                                <xsl:text>masterDatatypeLabel</xsl:text>
                            </xsl:attribute>
                            <xsl:text>MAS</xsl:text>
                        </xsl:element>
                        <xsl:element name="span">
                            <xsl:text> - </xsl:text>
                        </xsl:element>
                    </xsl:if>
                </xsl:if>
                <xsl:value-of select="@title"/>
            </xsl:element>
            <xsl:choose>
                <xsl:when test="count(Section) &gt; 0">
                    <xsl:element name="div">
                        <xsl:attribute name="id">
                            <xsl:value-of
                                    select="concat(@id, '_btn')"/>
                        </xsl:attribute>
                        <xsl:attribute name="class">unhidden btn</xsl:attribute>
                        <xsl:element name="a">
                            <xsl:attribute name="href">javascript:unhide('<xsl:value-of
                                select="concat(@id, '_toc')"/>', '<xsl:value-of
                                select="concat(@id, '_txt')"/>');
                            </xsl:attribute>
                            <xsl:element name="span">
                                <xsl:attribute name="id">
                                    <xsl:value-of
                                            select="concat(@id, '_txt')"/>
                                </xsl:attribute>
                                <xsl:text>
                                    [Hide]
                                </xsl:text>
                            </xsl:element>
                        </xsl:element>
                    </xsl:element>
                </xsl:when>
            </xsl:choose>
            <xsl:element name="br"/>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
