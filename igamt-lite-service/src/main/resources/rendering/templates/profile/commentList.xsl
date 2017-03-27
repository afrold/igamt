<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="CommentList">
        <xsl:element name="h4">
            <xsl:text>Comments</xsl:text>
        </xsl:element>
        <xsl:element name="table">
            <xsl:attribute name="class">
                <xsl:text>contentTable</xsl:text>
            </xsl:attribute>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>33%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>33%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>33%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="thead">
                <xsl:attribute name="class">
                    <xsl:text>contentThead</xsl:text>
                </xsl:attribute>
                <xsl:element name="tr">
                    <xsl:element name="th">
                        <xsl:text>Location</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Date</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Description</xsl:text>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="tbody">
                <xsl:for-each select="Comment">
                    <xsl:element name="tr">
                        <xsl:attribute name="class">
                            <xsl:text>contentTr</xsl:text>
                        </xsl:attribute>
                        <xsl:element name="td">
                            <xsl:value-of select="@Location"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:value-of select="@Date"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:value-of select="@Description"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
