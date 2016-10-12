<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="/rendering/templates/profile/constraint.xsl"/>
    <xsl:import href="/rendering/templates/profile/segmentField.xsl"/>

    <xsl:template match="Segment" mode="toc">
        <a href="#{@ID}">
            <br></br>
            <xsl:value-of select="@Name" />
            -
            <xsl:value-of select="@Description" />
        </a>
    </xsl:template>

    <xsl:template match="Segment">
        <xsl:param name="inlineConstraint"/>
        <xsl:value-of select="@Comment"></xsl:value-of>
        <xsl:if test="count(./Text[@Type='Text1']) &gt; 0">
            <p>
                <xsl:value-of disable-output-escaping="yes"
                              select="./Text[@Type='Text1']" />
            </p>
        </xsl:if>
        <p>
            <table width="100%" border="1" cellspacing="0" cellpadding="1">
                <col style="width:5%"></col>
                <col style="width:15%"></col>
                <col style="width:10%"></col>
                <col style="width:10%"></col>
                <col style="width:10%"></col>
                <col style="width:10%"></col>
                <col style="width:10%"></col>
                <col style="width:30%"></col>
                <thead style="background:#F0F0F0; color:#B21A1C; align:center">
                    <tr>
                        <th>
                            Seq
                        </th>
                        <th>
                            Element name
                        </th>
                        <th>
                            Data type
                        </th>
                        <th>
                            Usage
                        </th>
                        <th>
                            Cardinality
                        </th>
                        <th>
                            Length
                        </th>
                        <th>
                            Concept Domain
                        </th>
                        <th>
                            Comment
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="Field">
                        <xsl:sort select="@Position" data-type="number"></xsl:sort>
                        <xsl:call-template name="SegmentField">
                            <xsl:with-param name="inlineConstraint" select="$inlineConstraint"/>
                            <xsl:with-param name="style"
                                            select="'background-color:white;text-decoration:normal'">
                            </xsl:with-param>
                        </xsl:call-template>

                    </xsl:for-each>
                </tbody>
            </table>
        </p>
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
                <p>
                    <b>
                        <xsl:value-of select="../@Name" />
                        -
                        <xsl:value-of select="./@Position" />
                        &#160;
                        <xsl:value-of select="./@Name" />
                        (
                        <xsl:value-of select="./@Datatype" />
                        )
                    </b>
                    <xsl:value-of disable-output-escaping="yes"
                                  select="./Text[@Type='Text']" />
                </p>
            </xsl:if>
        </xsl:for-each>
        <br></br>
    </xsl:template>

</xsl:stylesheet>
