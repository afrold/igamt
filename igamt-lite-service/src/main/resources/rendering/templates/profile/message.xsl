<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:include href="/rendering/templates/profile/element.xsl"/>
    <xsl:include href="/rendering/templates/profile/constraint.xsl"/>
    <xsl:template match="MessageDisplay">
        <xsl:value-of select="@Comment"/>
        <xsl:if test="count(Text[@Type='UsageNote']) &gt; 0">
            <xsl:element name="p">
                <xsl:element name="h4"><xsl:text>Definition text</xsl:text></xsl:element>
                <xsl:value-of disable-output-escaping="yes"
                              select="Text[@Type='UsageNote']"/>
            </xsl:element>
        </xsl:if>
        <xsl:element name="p">
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
                        <xsl:text>20%</xsl:text>
                    </xsl:attribute>
                </xsl:element>
                <xsl:element name="col">
                    <xsl:attribute name="width">
                        <xsl:text>20%</xsl:text>
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
                            <xsl:text>Segment</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Flavor</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Element name</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Cardinality</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Usage</xsl:text>
                        </xsl:element>
                        <xsl:element name="th">
                            <xsl:text>Description/Comments</xsl:text>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="tbody">
                    <xsl:for-each select="Elt">
                        <xsl:call-template name="element">
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:element>
            </xsl:element>
            <xsl:if test="count(./Constraint[@Type='cs'])+count(./Elt/Constraint[@Type='cs']) &gt; 0">
                <xsl:element name="p">
                    <xsl:text>Conformance statements</xsl:text>
                </xsl:element>

                <xsl:call-template name="Constraint">
                    <xsl:with-param name="title">
                        <xsl:text>Message:</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="constraintMode">
                        <xsl:text>standalone</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="type">
                        <xsl:text>cs</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>

                <xsl:for-each select="Elt">
                    <xsl:if test="count(./Constraint[@Type='cs']) &gt; 0">
                        <xsl:call-template name="Constraint">
                            <xsl:with-param name="title">
                                <xsl:text>Group:</xsl:text>
                            </xsl:with-param>
                            <xsl:with-param name="constraintMode">
                                <xsl:text>standalone</xsl:text>
                            </xsl:with-param>
                            <xsl:with-param name="type">
                                <xsl:text>cs</xsl:text>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>
            <xsl:if test="count(./Constraint[@Type='pre'])+count(./Elt/Constraint[@Type='pre']) &gt; 0">
                <xsl:element name="p">
                    <xsl:text>Conditional predicates</xsl:text>
                </xsl:element>

                <xsl:call-template name="Constraint">
                    <xsl:with-param name="title">
                        <xsl:text>Message:</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="constraintMode">
                        <xsl:text>standalone</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="type">
                        <xsl:text>pre</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>

                <xsl:for-each select="Elt">
                    <xsl:if test="count(./Constraint[@Type='pre']) &gt; 0">
                        <xsl:call-template name="Constraint">
                            <xsl:with-param name="title">
                                <xsl:text>Group:</xsl:text>
                            </xsl:with-param>
                            <xsl:with-param name="constraintMode">
                                <xsl:text>standalone</xsl:text>
                            </xsl:with-param>
                            <xsl:with-param name="type">
                                <xsl:text>pre</xsl:text>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>
        </xsl:element>
        <xsl:value-of disable-output-escaping="yes"
                      select="./Text[@Type='UsageNote']"/>
        <br></br>
    </xsl:template>
</xsl:stylesheet>
