<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="/rendering/templates/profile/conformanceStatementHeader.xsl"/>
    <xsl:import href="/rendering/templates/profile/predicateHeader.xsl"/>
    <xsl:import href="/rendering/templates/profile/constraintContent.xsl"/>
    <xsl:template name="displayConstraint">
        <xsl:param name="title"/>
        <xsl:param name="type"/>
        <xsl:param name="constraintMode"/>
        <xsl:element name="p">
            <xsl:element name="strong">
                <xsl:element name="u">
                    <xsl:value-of select="$title"/>
                </xsl:element>
            </xsl:element>
            <xsl:element name="table">
                <xsl:attribute name="class">
                    <xsl:text>contentTable</xsl:text>
                </xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$type='cs'">
                        <xsl:call-template name="conformanceStatementHeader"/>
                        <xsl:element name="tbody">
                            <xsl:for-each select="./Constraint[@Type='cs']">
                                <xsl:sort select="@Position" data-type="number"></xsl:sort>
                                <xsl:call-template name="ConstraintContent">
                                    <xsl:with-param name="mode" select="$constraintMode"/>
                                    <xsl:with-param name="type" select="$type"/>
                                </xsl:call-template>
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="$type='pre'">
                        <xsl:call-template name="predicateHeader"/>
                        <xsl:element name="tbody">
                            <xsl:for-each select="./Constraint[@Type='pre']">
                                <xsl:sort select="@Position" data-type="number"></xsl:sort>
                                <xsl:call-template name="ConstraintContent">
                                    <xsl:with-param name="mode" select="$constraintMode"/>
                                    <xsl:with-param name="type" select="$type"/>
                                </xsl:call-template>
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:when>
                </xsl:choose>

            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
