<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:include href="/rendering/templates/profile/constraint.xsl"/>

    <xsl:template name="MessageConstraint">
        <xsl:param name="constraintType"/>
        <xsl:if test="count(./Constraints/Constraint[@Type=$constraintType])+count(./MessageGroup/Constraint[@Type=$constraintType]) &gt; 0">
            <xsl:element name="br"/>
            <xsl:element name="span">
                <xsl:element name="b">
                    <xsl:choose>
                        <xsl:when test="$constraintType='cs'">
                            <xsl:text>Conformance Statements</xsl:text>
                        </xsl:when>
                        <xsl:when test="$constraintType='pre'">
                            <xsl:text>Conditional Predicates</xsl:text>
                        </xsl:when>
                    </xsl:choose>
                </xsl:element>
            </xsl:element>
            <xsl:for-each select="Constraints">
                <xsl:if test="count(./Constraint[@Type=$constraintType]) &gt; 0">
                    <xsl:element name="br"/>
                    <xsl:call-template name="Constraint">
                        <xsl:with-param name="title">
                            <xsl:text>&lt;ul&gt;&lt;li&gt;Message:&lt;/li&gt;&lt;/ul&gt;</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="constraintMode">
                            <xsl:text>standalone</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="type">
                            <xsl:text>cs</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="headerLevel">
                            <xsl:text>h5</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
            <xsl:for-each select="MessageGroup">
                <xsl:if test="count(./Constraint[@Type=$constraintType]) &gt; 0">
                    <xsl:element name="br"/>
                    <xsl:call-template name="Constraint">
                        <xsl:with-param name="title">
                            <xsl:text>&lt;ul&gt;&lt;li&gt;Group:&lt;/li&gt;&lt;/ul&gt;</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="constraintMode">
                            <xsl:text>standalone</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="type">
                            <xsl:text>cs</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="headerLevel">
                            <xsl:text>h5</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
