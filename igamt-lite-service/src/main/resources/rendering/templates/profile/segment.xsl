<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="/rendering/templates/profile/constraint.xsl"/>
    <xsl:import href="/rendering/templates/profile/segmentField.xsl"/>

    <xsl:template match="Segment" mode="toc">
        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:value-of select="concat('#{',@id,'}')"/>
            </xsl:attribute>
            <xsl:element name="br"/>
            <xsl:value-of select="concat(@Name,' - ',@Description)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="Segment">
        <xsl:param name="inlineConstraint"/>
        <xsl:value-of select="@Comment"/>
        <xsl:if test="count(./Text[@Type='Text1']) &gt; 0">
            <xsl:element name="p">
                <xsl:value-of disable-output-escaping="yes"
                              select="./Text[@Type='Text1']" />
            </xsl:element>
        </xsl:if>
        <xsl:element name="p">
            <xsl:element name="table">
                <xsl:attribute name="class">
                    <xsl:text>contentTable</xsl:text>
                </xsl:attribute>
                <xsl:element name="col">
                    <xsl:attribute name="width">
                        <xsl:text>5%</xsl:text>
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
                        <xsl:text>30%</xsl:text>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="thead">
                    <xsl:attribute name="class">
                        <xsl:text>contentThead</xsl:text>
                    </xsl:attribute>
                    <xsl:element name="tr">
                        <xsl:element name="th">
                            <xsl:text>Seq</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Element name</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Data type</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Usage</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Cardinality</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Length</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Concept Domain</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Comment</xsl:text>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="tbody">
                    <xsl:for-each select="Field">
                        <xsl:sort select="@Position" data-type="number"></xsl:sort>
                        <xsl:call-template name="SegmentField">
                            <xsl:with-param name="inlineConstraint" select="$inlineConstraint"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:element>
            </xsl:element>
        </xsl:element>

        <xsl:if test="count(Field//Constraint) &gt; 0">
            <xsl:if test="normalize-space($inlineConstraint) = 'false'">
                <xsl:call-template name="Constraint">
                    <xsl:with-param name="title">
                        <xsl:text>Conformance statements</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="constraintMode">
                        <xsl:text>standalone</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="type">
                        <xsl:text>cs</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="constraintPath">
                        <xsl:text>Field//Constraint[@Type='cs']</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="Constraint">
                    <xsl:with-param name="title">
                        <xsl:text>Conditional predicates</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="constraintMode">
                        <xsl:text>standalone</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="type">
                        <xsl:text>pre</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="constraintPath">
                        <xsl:text>Field//Constraint[@Type='pre']</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>

        <xsl:value-of disable-output-escaping="yes"
                      select="./Text[@Type='Text2']" />

        <xsl:for-each select="Field">
            <xsl:sort select="@Position" data-type="number"></xsl:sort>
            <xsl:if test="count(Text) &gt; 0">
                <xsl:element name="p">
                    <xsl:element name="b">
                        <xsl:value-of select="concat(../@Name,' - ',./@Position,' ',./@Name,'(',./@Datatype,')')" />
                    </xsl:element>
                    <xsl:value-of disable-output-escaping="yes" select="./Text[@Type='Text']" />
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
        <xsl:element name="br"/>
    </xsl:template>

</xsl:stylesheet>
