<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="DateTimeDatatype">
        <xsl:element name="span">
            <xsl:element name="span">
                <xsl:element name="b">
                    <xsl:text>Data Type Definition</xsl:text>
                </xsl:element>
            </xsl:element>
            <xsl:element name="table">
                <xsl:attribute name="class">
                    <xsl:text>contentTable</xsl:text>
                </xsl:attribute>
                <xsl:element name="thead">
                    <xsl:attribute name="class">
                        <xsl:text>contentThead</xsl:text>
                    </xsl:attribute>
                    <xsl:element name="tr">
                        <xsl:element name="th">
                            <xsl:text>YYYY</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>MM</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>DD</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>hh</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>mm</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>ss</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>.ssss</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>+/-ZZZZ</xsl:text>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="tbody">
                    <xsl:element name="tr">
                        <xsl:element name="td">
                            <xsl:if test="@YYYY='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@YYYY"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:if test="@MM='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@MM"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:if test="@DD='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@DD"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:if test="@hh='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@hh"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:if test="@mm='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@mm"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:if test="@ss='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@ss"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:if test="@ssss='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@ssss"/>
                        </xsl:element>
                        <xsl:element name="td">
                            <xsl:if test="@timeZone='Required'">
                                <xsl:attribute name="class">
                                    <xsl:text>requiredDTM</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@timeZone"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
