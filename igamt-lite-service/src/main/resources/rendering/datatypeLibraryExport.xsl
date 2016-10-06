<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Include the templates -->
    <xsl:import href="templates/section.xsl"/>
    <xsl:import href="templates/profileContent.xsl"/>
    <xsl:import href="templates/tableOfContentSection.xsl"/>
    <xsl:import href="templates/htmlContent.xsl"/>
    <xsl:import href="templates/wordContent.xsl"/>
    <xsl:param name="inlineConstraints" select="'false'"/>
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
                    <xsl:text>.masterDatatypeLabel {color:red;}</xsl:text>
                    <xsl:text>body,html {font-family: 'Arial Narrow',sans-serif;}</xsl:text>
                    <!-- Check the target format to include specific style -->
                    <xsl:choose>
                        <xsl:when test="$targetFormat='html'">
                            <!-- Add html specific style -->
                            <xsl:text>#sidebar { float:left; width:30%; background:#F0F0F0; overflow: auto; max-height: 100vh; font-family: 'Arial Narrow', sans-serif; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
                            <xsl:text>#sidebar a:link { color: #000066; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
                            <xsl:text>#sidebar a:visited { color: green; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
                            <xsl:text>#sidebar a:hover { color: hotpink; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
                            <xsl:text>#sidebar a:active { color: blue; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
                            <xsl:text>#main { float:right; width:70%; overflow: auto; max-height: 100vh; }</xsl:text>
                            <xsl:text>#notoc { float:right; width:100%; overflow: auto; max-height: 100vh; }</xsl:text>
                            <xsl:text>.divh1 { padding-left: 15px;}</xsl:text>
                            <xsl:text>.divh2 {padding-left: 30px;}</xsl:text>
                            <xsl:text>.divh3 {padding-left: 45px;}</xsl:text>
                            <xsl:text>.divh4 {padding-left: 60px;}</xsl:text>
                            <xsl:text>.divh5 {padding-left: 75px;}</xsl:text>
                            <xsl:text>.divh6 {padding-left:90px;}</xsl:text>
                            <xsl:text>.hidden {display: none;}</xsl:text>
                            <xsl:text>.unhidden {display:block;}</xsl:text>
                            <xsl:text>.btn {float:right;}</xsl:text>
                        </xsl:when>
                        <xsl:when test="$targetFormat='word'">
                            <!-- Add Word specific style-->
                            <xsl:text>body,html {font-size: 10px;width:100%;overflow:auto;max-height:100vh;}</xsl:text>
                        </xsl:when>
                    </xsl:choose>
                </xsl:element>
                <!-- End of the head tag -->
            </xsl:element>
            <!-- Content of the body tag -->
            <xsl:element name="body">
                <!-- Check the target format to include specific content -->
                <xsl:choose>
                    <xsl:when test="$targetFormat='html'">
                        <xsl:call-template name="displayHtmlContent"/>
                    </xsl:when>
                    <xsl:when test="$targetFormat='word'">
                        <xsl:call-template name="displayWordContent"/>
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
