<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="ProfileComponent" mode="toc">
        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:value-of select="concat('#{',@id,'}')"/>
            </xsl:attribute>
            <xsl:element name="br"/>
            <xsl:value-of select="@Name"/>
        </xsl:element>
    </xsl:template>


    <xsl:template match="ProfileComponent">
        <xsl:if test="@Description!=''">
            <xsl:element name="p">
                <xsl:value-of select="@Description"/>
            </xsl:element>
        </xsl:if>
        <xsl:element name="table">
            <xsl:attribute name="class">
                <xsl:text>contentTable</xsl:text>
            </xsl:attribute>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
                </xsl:attribute>
            </xsl:element>
            <xsl:element name="col">
                <xsl:attribute name="width">
                    <xsl:text>10%</xsl:text>
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
                    <xsl:if test="$columnDisplay.profileComponent.name = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Name</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.usage = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Usage</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.cardinality = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Cardinality</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.length = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Length</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.conformanceLength = 'true'">
                        <xsl:element name="th">
                            <xsl:text>ConfLength</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.dataType = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Data Type</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.valueSet = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Value Set/Single Code</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.definitionText = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Definition Text</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.profileComponent.comment = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Comment</xsl:text>
                        </xsl:element>
                    </xsl:if>
                </xsl:element>
                <xsl:element name="tbody">
                    <xsl:for-each select="./SubProfileComponent">
                        <xsl:element name="tr">
                            <xsl:element name="td">
                                <xsl:value-of select="@Path"/>
                            </xsl:element>
                            <xsl:if test="$columnDisplay.profileComponent.name = 'true'">
                                <xsl:element name="td">
                                    <xsl:value-of select="@Name"/>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.usage = 'true'">
                                <xsl:element name="td">
                                    <xsl:value-of select="@Usage"/>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.cardinality = 'true'">
                                <xsl:element name="td">
                                    <xsl:if test="(normalize-space(@Min)!='') or (normalize-space(@Max)!='')">
                                        <xsl:value-of select="concat('[',@Min,'..',@Max,']')"/>
                                    </xsl:if>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.length = 'true'">
                                <xsl:element name="td">
                                    <xsl:if test="(normalize-space(@MinLength)!='') or (normalize-space(@MaxLength)!='')">
                                        <xsl:value-of
                                                select="concat('[',@MinLength,'..',@MaxLength,']')"/>
                                    </xsl:if>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.conformanceLength = 'true'">
                                <xsl:element name="td">
                                    <xsl:if test="(normalize-space(@ConfLength)!='') and (normalize-space(@ConfLength)!='0')">
                                        <xsl:value-of select="@ConfLength"/>
                                    </xsl:if>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.dataType = 'true'">
                                <xsl:element name="td">
                                    <xsl:value-of select="@Datatype"/>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.valueSet = 'true'">
                                <xsl:element name="td">
                                    <xsl:value-of select="@ValueSet"/>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.definitionText = 'true'">
                                <xsl:element name="td">
                                    <xsl:value-of disable-output-escaping="yes" select="@DefinitionText"/>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="$columnDisplay.profileComponent.comment = 'true'">
                                <xsl:element name="td">
                                    <xsl:value-of select="@Comment"/>
                                </xsl:element>
                            </xsl:if>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
