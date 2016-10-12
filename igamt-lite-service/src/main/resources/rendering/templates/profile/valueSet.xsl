<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="/rendering/templates/profile/valueSetContent.xsl"/>
    <xsl:template match="ValueSetDefinition" mode="toc">
        <a href="#{@Id}">
            <br></br>
            <xsl:value-of select="@BindingIdentifier" />
            -
            <xsl:value-of select="@Description" />
        </a>
    </xsl:template>

    <xsl:template match="ValueSetDefinition">
        <xsl:if test="@Stability != ''">
            <p><xsl:text>Stability: </xsl:text>
                <xsl:value-of select="@Stability"></xsl:value-of>
            </p>
        </xsl:if>
        <xsl:if test="@Extensibility != ''">
            <p><xsl:text>Extensibility: </xsl:text>
                <xsl:value-of select="@Extensibility"></xsl:value-of></p>
        </xsl:if>
        <xsl:if test="@ContentDefinition != ''">
            <p><xsl:text>Content Definition: </xsl:text>
                <xsl:value-of select="@ContentDefinition"></xsl:value-of></p>
        </xsl:if>
        <xsl:if test="@Oid != ''">
            <p><xsl:text>Oid: </xsl:text>
                <xsl:value-of select="@Oid"></xsl:value-of></p>
        </xsl:if>
        <table width="100%" border="1" cellspacing="0" cellpadding="0">
            <col style="width:15%"></col>
            <col style="width:15%"></col>
            <col style="width:10%"></col>
            <col style="width:60%"></col>
            <thead style="background:#F0F0F0; color:#B21A1C; align:center">
                <tr>
                    <th>
                        Value
                    </th>
                    <th>
                        Code System
                    </th>
                    <th>
                        Usage
                    </th>
                    <th>
                        Description
                    </th>
                </tr>
            </thead>
            <tbody>
                <xsl:for-each select="ValueElement">
                    <xsl:sort select="@Value" />
                    <xsl:call-template name="ValueSetContent">
                        <xsl:with-param name="style" select="'background-color:white;'">
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </tbody>
        </table>
        <!-- <br></br> -->
    </xsl:template>

</xsl:stylesheet>
