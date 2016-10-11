<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="/rendering/templates/profile/component.xsl"/>
    <xsl:import href="/rendering/templates/profile/predicateHeader.xsl"/>
    <xsl:import href="/rendering/templates/profile/conformanceStatementHeader.xsl"/>
    <xsl:import href="/rendering/templates/profile/constraint.xsl"/>
    <xsl:template match="Datatype">
        <xsl:param name="inlineConstraints"/>
        <xsl:if test="count(Text[@Type='PurposeAndUse']) &gt; 0">
            <xsl:element name="p">
                <xsl:value-of disable-output-escaping="yes"
                              select="Text[@Type='PurposeAndUse']"/>
            </xsl:element>
        </xsl:if>
        <xsl:value-of select="@Comment"></xsl:value-of>
        <xsl:if test="count(Text[@Type='UsageNote']) &gt; 0">
            <xsl:element name="p">
                <xsl:value-of disable-output-escaping="yes"
                              select="Text[@Type='UsageNote']"/>
            </xsl:element>
        </xsl:if>
        <xsl:element name="p">
            <xsl:element name="table">
                <xsl:attribute name="class">
                    <xsl:text>.contentTable</xsl:text>
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
                        <xsl:text>.contentThead</xsl:text>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="tr">
                    <xsl:element name="th">
                        <xsl:text>Seq</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Element name</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Conf length</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Data type</xsl:text>
                    </xsl:element>
                    <xsl:element name="th">
                        <xsl:text>Usage</xsl:text>
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
                <xsl:element name="tbody">
                    <xsl:for-each select="Component">
                        <xsl:sort select="@Position" data-type="number"></xsl:sort>
                        <xsl:call-template name="component">
                            <xsl:with-param name="style"
                                            select="'background-color:white;text-decoration:normal'">
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:if test="count(./Constraint) &gt; 0">
            <xsl:if test="normalize-space($inlineConstraints) = 'false'">
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
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
        <xsl:if test="count(./Component/Text[@Type='Text']) &gt; 0">
            <xsl:element name="h4">
                <xsl:text>Components Definition Texts</xsl:text>
            </xsl:element>
            <xsl:for-each select="Component">
                <xsl:sort select="@Position" data-type="number"></xsl:sort>
                <xsl:if test="count(./Text[@Type='Text']) &gt; 0">
                    <xsl:element name="p">
                        <xsl:element name="strong">
                            <xsl:value-of disable-output-escaping="yes"
                                          select="concat(../@Name, '-', @Position, ':', @Name)"/>
                        </xsl:element>
                        <xsl:value-of disable-output-escaping="yes" select="./Text[@Type='Text']"/>
                    </xsl:element>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:if test="count(./Text[@Type='Text2']) &gt; 0">
            <xsl:element name="h4">
                <xsl:text>post-definition:</xsl:text>
            </xsl:element>
            <xsl:if test="count(./Text[@Type='Text']) &gt; 0">
                <xsl:element name="p">
                    <xsl:element name="u">
                        <xsl:value-of select="./Text[@Type='Name']"/>
                        <xsl:text>:</xsl:text>
                    </xsl:element>
                    <xsl:value-of disable-output-escaping="yes" select="./Text[@Type='Text']"/>
                </xsl:element>
            </xsl:if>
            <xsl:element name="p">
                <xsl:value-of disable-output-escaping="yes" select="./Text[@Type='Text2']"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="Datatype" mode="toc">
        <a href="#{@id}">
            <xsl:element name="br"/>
            <xsl:text>concat(@Label," - ",@Description)</xsl:text>
        </a>
    </xsl:template>
</xsl:stylesheet>
