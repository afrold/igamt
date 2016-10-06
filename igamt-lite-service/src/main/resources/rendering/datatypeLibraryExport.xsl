<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Include the templates -->
    <xsl:import href="templates/section.xsl"/>
    <xsl:import href="templates/profileContent.xsl"/>
    <xsl:import href="templates/tableOfContentSection.xsl"/>
    <xsl:import href="templates/htmlContent.xsl"/>
    <xsl:import href="templates/wordContent.xsl"/>
    <xsl:import href="templates/style/htmlStyle.xsl"/>
    <xsl:import href="templates/style/wordStyle.xsl"/>
    <xsl:import href="templates/style/globalStyle.xsl"/>
    <xsl:param name="inlineConstraints" select="'false'"/>
    <xsl:param name="includeTOC" select="'false'"/>
    <xsl:param name="targetFormat" select="'html'"/>
    <xsl:param name="documentTitle" select="'Implementation Guide'"/>

    <xsl:output method="html"/>


    <xsl:template match="/">

        <xsl:element name="html">
            <!--xsl:attribute name="xmlns"><xsl:text>http://www.w3.org/1999/xhtml</xsl:text></xsl:attribute-->
            <!-- Content of the head tag -->
            <xsl:element name="head">
                <!--xsl:element name="meta">
                    <xsl:attribute name="http-equiv"><xsl:text>Content-Type</xsl:text></xsl:attribute>
                    <xsl:attribute name="content"><xsl:text>text/html; charset=utf-8</xsl:text></xsl:attribute>
                </xsl:element-->
                <xsl:element name="title">
                    <xsl:value-of select="$documentTitle"/>
                </xsl:element>
                <!-- Style tag to add some CSS -->
                <xsl:element name="style">
                    <xsl:attribute name="type">
                        <xsl:text>text/css</xsl:text>
                    </xsl:attribute>
                    <!-- Add CSS shared by word and html exports -->
                    <xsl:call-template name="globalStyle"/>
                    <!-- Check the target format to include specific style -->
                    <xsl:choose>
                        <xsl:when test="$targetFormat='html'">
                            <!-- Add html specific style -->
                            <xsl:call-template name="htmlStyle"/>
                        </xsl:when>
                        <xsl:when test="$targetFormat='word'">
                            <!-- Add Word specific style-->
                            <xsl:call-template name="wordStyle"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:element>
                <!-- End of the head tag -->
            </xsl:element>
            <!-- Content of the body tag -->
            <xsl:element name="body">
                <xsl:element name="div">
                    <xsl:text>target format : </xsl:text>
                    <xsl:value-of select="$targetFormat"/>
                    <xsl:element name="br"/>
                    <xsl:text>document title : </xsl:text>
                    <xsl:value-of select="$documentTitle"/>
                    <xsl:element name="br"/>
                    <xsl:text>include toc : </xsl:text>
                    <xsl:value-of select="$includeTOC"/>
                    <xsl:element name="br"/>
                    <xsl:text>inline constraints : </xsl:text>
                    <xsl:value-of select="$inlineConstraints"/>
                    <xsl:element name="br"/>
                </xsl:element>
                <!-- Check the target format to include specific content -->
                <xsl:choose>
                    <xsl:when test="$targetFormat='html'">
                        <xsl:call-template name="displayHtmlContent">
                            <xsl:with-param name="includeTOC" select="$includeTOC"/>
                            <xsl:with-param name="inlineConstraint" select="$inlineConstraints"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$targetFormat='word'">
                        <xsl:call-template name="displayWordContent">
                            <xsl:with-param name="includeTOC" select="$includeTOC"/>
                            <xsl:with-param name="inlineConstraint" select="$inlineConstraints"/>
                        </xsl:call-template>
                    </xsl:when>
                </xsl:choose>
                <!-- End of the body tag -->
            </xsl:element>
            <!-- End of the html tag -->
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>





<!--xsl:choose>
<xsl:when test="$targetFormat='html'">

</xsl:when>
<xsl:when test="$targetFormat='word'">

</xsl:when>
</xsl:choose-->
