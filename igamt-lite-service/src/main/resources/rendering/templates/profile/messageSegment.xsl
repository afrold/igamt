<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="MessageSegment">
		<xsl:element name="tr">
			<xsl:attribute name="class">
                <xsl:text>contentTr</xsl:text>
            </xsl:attribute>
			<xsl:if test="$columnDisplay.message.segment = 'true'">
				<xsl:element name="td">
					<xsl:value-of select="@Ref" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="$columnDisplay.message.flavor = 'true'">
				<xsl:element name="td">
					<xsl:value-of select="@Label" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="$columnDisplay.message.name = 'true'">
				<xsl:element name="td">
					<xsl:value-of select="@Description" />
				</xsl:element>
			</xsl:if>

			<xsl:if test="@Ref!=']'">
				<xsl:if test="$columnDisplay.message.cardinality = 'true'">
					<xsl:element name="td">
						<xsl:if
							test="(normalize-space(@Min)!='') and (normalize-space(@Max)!='') and (normalize-space(@Min)!='0' or normalize-space(@Max)!='0')">
							<xsl:value-of select="concat('[', @Min, '..', @Max, ']')"></xsl:value-of>
						</xsl:if>
					</xsl:element>
				</xsl:if>
				<xsl:if test="$columnDisplay.message.usage = 'true'">
					<xsl:element name="td">
						<xsl:if test="(normalize-space(@Usage)!='')">
							<xsl:value-of select="@Usage" />
						</xsl:if>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="@Ref=']'">
				<!-- Do not display cardinality and usage for the end of a segment -->
				<xsl:if test="$columnDisplay.message.cardinality = 'true'">
					<xsl:element name="td"><xsl:attribute name="class"><xsl:text>greyCell</xsl:text></xsl:attribute></xsl:element>
				</xsl:if>
				<xsl:if test="$columnDisplay.message.usage = 'true'">
					<xsl:element name="td"><xsl:attribute name="class"><xsl:text>greyCell</xsl:text></xsl:attribute></xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:if test="$columnDisplay.message.comment = 'true'">
				<xsl:element name="td">
					<xsl:value-of select="@Comment" />
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
