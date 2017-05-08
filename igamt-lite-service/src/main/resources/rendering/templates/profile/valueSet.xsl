<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="/rendering/templates/profile/valueSetContent.xsl"/>
    <xsl:import href="/rendering/templates/profile/valueSetMetadata.xsl"/>
    <xsl:template match="ValueSetDefinition" mode="toc">
        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:value-of select="concat('#{',@id,'}')"/>
            </xsl:attribute>
            <xsl:choose>
            	<xsl:when test="@Name!=''">
            		<xsl:value-of select="concat(@BindingIdentifier,' - ',@Name)"/>
            	</xsl:when>
            	<xsl:otherwise>
            		<xsl:value-of select="@BindingIdentifier"/>
            	</xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xsl:template match="ValueSetDefinition">
        <xsl:if test="count(./Text[@Type='DefPreText']) &gt; 0">
            <xsl:call-template name="definitionText">
                <xsl:with-param name="type">
                    <xsl:text>pre</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:element name="br"/>
        <xsl:call-template name="valueSetMetadata"/>
        <xsl:element name="br"/>
        <xsl:element name="span">
        	<xsl:attribute name="class">
        		<xsl:text>contentDiv</xsl:text>
        	</xsl:attribute>
            <xsl:element name="span">
                <xsl:element name="b">
                    <xsl:text>Codes</xsl:text>
                </xsl:element>
            </xsl:element>
            <xsl:element name="table">
                <xsl:attribute name="class">
                    <xsl:text>contentTable</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="summary">
                    <xsl:value-of select="@Description"></xsl:value-of>
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
                        <xsl:text>40%</xsl:text>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="col">
                    <xsl:attribute name="width">
                        <xsl:text>20%</xsl:text>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="thead">
                    <xsl:attribute name="class">
                        <xsl:text>contentThead</xsl:text>
                    </xsl:attribute>
                    <xsl:element name="tr">
                        <xsl:if test="$columnDisplay.valueSet.value = 'true'">
                            <xsl:element name="th">
                                <xsl:text>Value</xsl:text>
                            </xsl:element>
                        </xsl:if>
                        <xsl:if test="$columnDisplay.valueSet.codeSystem = 'true'">
                            <xsl:element name="th">
                                <xsl:text>Code System</xsl:text>
                            </xsl:element>
                        </xsl:if>
                        <xsl:if test="$columnDisplay.valueSet.usage = 'true'">
                            <xsl:element name="th">
                                <xsl:text>Usage</xsl:text>
                            </xsl:element>
                        </xsl:if>
                        <xsl:if test="$columnDisplay.valueSet.description = 'true'">
                            <xsl:element name="th">
                                <xsl:text>Description</xsl:text>
                            </xsl:element>
                        </xsl:if>
                        <xsl:if test="$columnDisplay.valueSet.comment = 'true'">
                            <xsl:element name="th">
                                <xsl:text>Comment</xsl:text>
                            </xsl:element>
                        </xsl:if>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="tbody">
                    <xsl:for-each select="ValueElement">
                        <xsl:sort select="@Value" data-type="number"></xsl:sort>
                        <xsl:call-template name="ValueSetContent"/>
                    </xsl:for-each>
                    <xsl:if test="count(ValueElement) = 0">
                            <xsl:element name="tr">
                                <xsl:attribute name="class">
                                    <xsl:text>contentTr</xsl:text>
                                </xsl:attribute>
                                <xsl:if test="$columnDisplay.valueSet.value = 'true'">
                                    <xsl:element name="td">
                                        <xsl:text></xsl:text>
                                    </xsl:element>
                                </xsl:if>
                                <xsl:if test="$columnDisplay.valueSet.codeSystem = 'true'">
                                    <xsl:element name="td">
                                        <xsl:text></xsl:text>
                                    </xsl:element>
                                </xsl:if>
                                <xsl:if test="$columnDisplay.valueSet.usage = 'true'">
                                    <xsl:element name="td">
                                        <xsl:text></xsl:text>
                                    </xsl:element>
                                </xsl:if>
                                <xsl:if test="$columnDisplay.valueSet.description = 'true'">
                                    <xsl:element name="td">
                                        <xsl:text></xsl:text>
                                    </xsl:element>
                                </xsl:if>
                            </xsl:element>

                    </xsl:if>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:element name="p">
            <xsl:if test="count(./Text[@Type='DefPostText']) &gt; 0">
                <xsl:call-template name="definitionText">
                    <xsl:with-param name="type">
                        <xsl:text>post</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:element>

    </xsl:template>

</xsl:stylesheet>
