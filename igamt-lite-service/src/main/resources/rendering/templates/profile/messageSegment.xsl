<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="messageSegment">
		<xsl:element name="tr">
			<xsl:attribute name="class">
                <xsl:text>contentTr</xsl:text>
            </xsl:attribute>
			<xsl:element name="td">
				<xsl:value-of select="@Ref" />
			</xsl:element>
			<xsl:element name="td">
				<xsl:value-of select="@Label" />
			</xsl:element>
			<xsl:element name="td">
				<xsl:value-of select="@Description" />
			</xsl:element>
			<xsl:element name="td">
				<xsl:if
					test="(normalize-space(@Min)!='') and (normalize-space(@Max)!='')">
					<xsl:value-of select="concat('[', @Min, '..', @Max, ']')"></xsl:value-of>
				</xsl:if>
			</xsl:element>
			<xsl:element name="td">
				<xsl:if test="(normalize-space(@Usage)!='')">
					<xsl:value-of select="@Usage" />
				</xsl:if>
			</xsl:element>
			<xsl:element name="td">
				<xsl:value-of select="@Comment" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
