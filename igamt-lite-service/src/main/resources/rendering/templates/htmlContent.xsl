<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="includeTOC" select="'true'"/>

    <xsl:template name="displayHtmlContent">
        <xsl:choose>
            <!-- If we need to include the table of content -->
            <xsl:when test="$includeTOC='true'">
                <xsl:element name="div">
                    <xsl:attribute name="id">
                        <xsl:text>sidebar</xsl:text>
                    </xsl:attribute>
                    <xsl:element name="h1">
                        <xsl:text>TABLE OF CONTENT</xsl:text>
                    </xsl:element>
                    <xsl:element name="br"/>
                    <xsl:call-template name="displayTableOfContentSection"/>
                </xsl:element>
                <xsl:element name="div">
                    <xsl:attribute name="id">
                        <xsl:text>main</xsl:text>
                    </xsl:attribute>
                    <xsl:apply-templates select="ConformanceProfile/MetaData"/>
                    <xsl:element name="hr"/>
                    <xsl:element name="a">
                        <xsl:attribute name="id">
                            <xsl:text>top</xsl:text>
                        </xsl:attribute>
                    </xsl:element>
                    <xsl:call-template name="displaySection"/>
                </xsl:element>

                <xsl:element name="script">
                    <xsl:attribute name="type">
                        <xsl:text>text/javascript</xsl:text>
                    </xsl:attribute>
            <xsl:text disable-output-escaping="yes">
                function unhide(divID, btnID) {
					var oLimit = document.querySelector("#sidebar");
					var divs = document.querySelectorAll("div");
					for (var i = 0; i &lt; divs.length; i++) {
					    if (divs[i].id == divID) {
					        divs[i].className = (divs[i].className=='hidden')?'unhidden':'hidden';
					    }
					}
					document.getElementById(btnID).innerHTML = ((document.getElementById(divID).className=='hidden')? "[Show]":"[Hide]");
                }
            </xsl:text>
                </xsl:element>
            </xsl:when>
            <!-- If we don't need to include the table of content -->
            <xsl:otherwise>
                <xsl:element name="div">
                    <xsl:attribute name="id">
                        <xsl:text>notoc</xsl:text>
                    </xsl:attribute>
                    <xsl:call-template name="displaySection" />
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
