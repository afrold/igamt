<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="ValueSetContent">
        <xsl:param name="style" />
        <tr style="{$style}">
            <td>
                <xsl:value-of select="@Value" />
            </td>
            <td>
                <xsl:value-of select="@CodeSystem" />
            </td>
            <td>
                <xsl:value-of select="@Usage" />
            </td>
            <td>
                <xsl:value-of select="@Label" />
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
