<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:include href="/rendering/templates/profile/messageSegment.xsl"/>
    <xsl:include href="/rendering/templates/profile/messageConstraint.xsl"/>
    <xsl:include href="/rendering/templates/profile/messageSegmentsOrGroups.xsl"/>
    <xsl:include href="/rendering/templates/profile/valueSetBindingList.xsl"/>
    <xsl:include href="/rendering/templates/profile/commentList.xsl"/>
    <xsl:template match="Message">
        <xsl:if test="count(./Text[@Type='DefPreText']) &gt; 0">
            <xsl:call-template name="definitionText">
                <xsl:with-param name="type">
                    <xsl:text>pre</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:element name="br"/>
        </xsl:if>
        <xsl:element name="span">
            <xsl:element name="b">
                <xsl:text>Conformance Profile Definition</xsl:text>
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
                    <xsl:if test="$columnDisplay.message.segment = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Segment</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.message.flavor = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Flavor</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.message.name = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Element name</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.message.cardinality = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Cardinality</xsl:text>
                        </xsl:element>
                    </xsl:if>
                    <xsl:if test="$columnDisplay.message.usage = 'true'">
                        <xsl:element name="th">
                            <xsl:text>Usage</xsl:text>
                        </xsl:element>
                    </xsl:if>
                </xsl:element>
            </xsl:element>
            <xsl:element name="tbody">
                <xsl:call-template name="displayMessageSegmentsOrGroups"/>
            </xsl:element>
        </xsl:element>
        <xsl:call-template name="MessageConstraint">
            <xsl:with-param name="constraintType">
                <xsl:text>cs</xsl:text>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="MessageConstraint">
            <xsl:with-param name="constraintType">
                <xsl:text>pre</xsl:text>
            </xsl:with-param>
        </xsl:call-template>
        <xsl:apply-templates select="./ValueSetBindingList"/>
        <xsl:if test="$columnDisplay.message.comment = 'true'">
            <xsl:apply-templates select="./CommentList"/>
        </xsl:if>
        <xsl:if test="count(./Text[@Type='DefPostText']) &gt; 0">
            <xsl:element name="br"/>
            <xsl:call-template name="definitionText">
                <xsl:with-param name="type">
                    <xsl:text>post</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="count(Text[@Type='UsageNote']) &gt; 0">
            <xsl:element name="br"/>
            <xsl:element name="span">
                <xsl:element name="span">
                    <xsl:element name="b">
                        <xsl:text>Usage note: </xsl:text>
                    </xsl:element>
                </xsl:element>
                <xsl:value-of disable-output-escaping="yes" select="Text[@Type='UsageNote']"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
