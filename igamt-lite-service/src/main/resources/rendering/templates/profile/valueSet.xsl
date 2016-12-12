<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="/rendering/templates/profile/valueSetContent.xsl"/>
    <xsl:template match="ValueSetDefinition" mode="toc">
        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:value-of select="concat('#{',@id,'}')"/>
            </xsl:attribute>
            <xsl:value-of select="concat(@BindingIdentifier,' - ',@Description)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="ValueSetDefinition">
        <xsl:if test="@Stability != ''">
            <xsl:element name="p">
                <xsl:text>Stability: </xsl:text>
                <xsl:value-of select="@Stability"/>
            </xsl:element>
        </xsl:if>
        <xsl:if test="@Extensibility != ''">
            <xsl:element name="p">
                <xsl:text>Extensibility: </xsl:text>
                <xsl:value-of select="@Extensibility"/>
            </xsl:element>
        </xsl:if>
        <xsl:if test="@ContentDefinition != ''">
            <xsl:element name="p">
                <xsl:text>Content Definition: </xsl:text>
                <xsl:value-of select="@ContentDefinition"/>
            </xsl:element>
        </xsl:if>
        <xsl:if test="@Oid != '' and @Oid != 'UNSPECIFIED'">
            <xsl:element name="p">
                <xsl:text>OID: </xsl:text>
                <xsl:value-of select="@Oid"/>
            </xsl:element>
        </xsl:if>

        <xsl:element name="table">

            <xsl:attribute name="class">
                <xsl:text>contentTable</xsl:text>
            </xsl:attribute>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>15%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>15%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>60%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="thead">
                <xsl:attribute name="class">
                    <xsl:text>contentThead</xsl:text>
                </xsl:attribute>
                <xsl:element name="tr">
                    <xsl:element name="th">
                        <xsl:text>Value</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Code System</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Usage</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Description</xsl:text>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="tbody">
                <xsl:for-each select="ValueElement">
                    <xsl:sort select="@Value" data-type="number"></xsl:sort>
                    <xsl:call-template name="ValueSetContent"/>
                </xsl:for-each>
            </xsl:element>

        </xsl:element>

    </xsl:template>

</xsl:stylesheet>
