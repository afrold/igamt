<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="/rendering/templates/sectionContent.xsl"/>
    <xsl:import href="/rendering/templates/profileContent.xsl"/>

    <xsl:template name="displayInfoSection" mode="disp">
        <xsl:param name="includeTOC" select="'true'"></xsl:param>
        <xsl:param name="inlineConstraint" select="'true'"></xsl:param>
        <xsl:param name="target" select="'html'"></xsl:param>
        <xsl:if test="name() = 'Section'">
            <xsl:element name="u">
                <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:choose>
                    <xsl:when test="@h &lt; 7 and normalize-space($includeTOC) = 'true'">
                        <xsl:element name="{concat('h', @h)}">
                            <xsl:if test="@prefix != '' and @target = 'html' ">
                                <xsl:value-of select="@prefix"/>
                                -
                            </xsl:if>
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
                            <xsl:value-of select="@title"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@h &gt; 7 and normalize-space($includeTOC) = 'true'">
                        <xsl:element name="h6">
                            <xsl:value-of select="@prefix"/>
                            -
                            <xsl:value-of select="@title"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@h &lt; 7 and normalize-space($includeTOC) = 'false'">
                        <xsl:element name="{concat('h', @h)}">
                            <xsl:value-of select="@title"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="@h &gt; 7 and normalize-space($includeTOC) = 'true'">
                        <xsl:element name="h6">
                            <xsl:value-of select="@prefix"/>
                            -
                            <xsl:value-of select="@title"/>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>
            </xsl:element>
            <xsl:element name="br"/>
            <xsl:call-template name="displaySectionContent"/>
            <xsl:call-template name="displayProfileContent">
                <xsl:with-param name="inlineConstraint" select="$inlineConstraint"/>
            </xsl:call-template>

        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
