<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="definitionText">
        <xsl:param name="type"/>
        <!--xsl:element name="b">
            <xsl:choose>
                <xsl:when test="$type='pre'"><xsl:text>Pre-definition:</xsl:text></xsl:when>
                <xsl:when test="$type='post'"><xsl:text>Post-definition:</xsl:text></xsl:when>
            </xsl:choose>
        </xsl:element-->
        <xsl:element name="p">
            <xsl:choose>
                <xsl:when test="$type='pre'">
                    <xsl:value-of disable-output-escaping="yes"
                                  select="./Text[@Type='DefPreText']" />
                </xsl:when>
                <xsl:when test="$type='post'">
                    <xsl:if test="count(./Text[@Type='Text']) &gt; 0">
                        <xsl:element name="p">
                            <xsl:element name="u">
                                <xsl:value-of select="./Text[@Type='Name']"/>
                                <xsl:text>:</xsl:text>
                            </xsl:element>
                            <xsl:value-of disable-output-escaping="yes" select="./Text[@Type='Text']"/>
                        </xsl:element>
                    </xsl:if>
                    <xsl:value-of disable-output-escaping="yes"
                                  select="./Text[@Type='DefPostText']" />
                </xsl:when>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
