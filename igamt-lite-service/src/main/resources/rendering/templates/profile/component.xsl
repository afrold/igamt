<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="component">
        <xsl:param name="style" />
        <xsl:param name="showConfLength" />
        <xsl:element name="tr">
            <xsl:attribute name="style">
                <xsl:value-of select="$style"/>
            </xsl:attribute>
            <xsl:element name="td">
                <xsl:value-of select="format-number(@Position, '0')" />
            </xsl:element>
            <xsl:element name="td">
                <xsl:value-of select="@Name" />
            </xsl:element>
            <xsl:element name="td">
                <xsl:if test="@ConfLength!='' and @ConfLength!='0'">
                    <xsl:value-of select="@ConfLength" />
                </xsl:if>
            </xsl:element>
            <xsl:element name="td">
                <xsl:value-of select="@Datatype" />
            </xsl:element>
            <xsl:element name="td">
                <xsl:value-of select="@Usage" />
            </xsl:element>
            <xsl:if test="$showConfLength='true'">
                <xsl:element name="td">
                    <xsl:if test="(normalize-space(@MinLength)!='') and (normalize-space(@MaxLength)!='') and (normalize-space(@MinLength)!='0') and (normalize-space(@MaxLength)!='0')">
                        [
                        <xsl:value-of select="@MinLength" />
                        ..
                        <xsl:value-of select="@MaxLength" />
                        ]
                    </xsl:if>
                </xsl:element>
            </xsl:if>
            <xsl:element name="td">
                <xsl:value-of select="@Binding" />
            </xsl:element>
            <xsl:element name="td">
                <xsl:value-of select="@Comment" />
            </xsl:element>
        </xsl:element>

        <xsl:if test="normalize-space($inlineConstraints) = 'true'">
            <xsl:if test="count(Constraint) &gt; 0">
                <xsl:apply-templates select="." mode="inlineDt"></xsl:apply-templates>
            </xsl:if>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
