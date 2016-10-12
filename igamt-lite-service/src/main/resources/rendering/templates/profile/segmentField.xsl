<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="SegmentField">
        <xsl:param name="inlineConstraint"/>
        <xsl:param name="style" />
        <tr style="{$style}">
            <td>
                <xsl:value-of select="format-number(@Position, '0')" />
            </td>
            <td>
                <xsl:value-of select="@Name" />
            </td>
            <td>
                <xsl:value-of select="@Datatype" />
            </td>
            <td>
                <xsl:value-of select="@Usage" />
            </td>
            <td>
                <xsl:if test="(normalize-space(@Min)!='') and (normalize-space(@Max)!='')">
                    [
                    <xsl:value-of select="@Min" />
                    ..
                    <xsl:value-of select="@Max" />
                    ]
                </xsl:if>
            </td>
            <td>
                <xsl:if test="(normalize-space(@MinLength)!='') and (normalize-space(@MaxLength)!='')">
                    [
                    <xsl:value-of select="@MinLength" />
                    ..
                    <xsl:value-of select="@MaxLength" />
                    ]
                </xsl:if>
            </td>
            <td>
                <xsl:value-of select="@Binding" />
            </td>
            <td>
                <xsl:value-of select="@Comment" />
            </td>
        </tr>
        <xsl:if test="normalize-space($inlineConstraint) = 'true'">
            <xsl:if test="count(Constraint) &gt; 0">
                <xsl:apply-templates select="." mode="inlineSgt"></xsl:apply-templates>
            </xsl:if>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
