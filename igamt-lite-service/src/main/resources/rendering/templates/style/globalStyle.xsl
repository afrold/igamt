<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="globalStyle">
        <xsl:text>.masterDatatypeLabel {color:red;}</xsl:text>
        <xsl:text>body,html {font-family: 'Arial Narrow',sans-serif;}</xsl:text>
        <xsl:text>.contentTable { width:100%; border:1; cellspacing:0; cellpadding:1; }</xsl:text>
        <xsl:text>.contentThead { background:#F0F0F0; color:#B21A1C; align:center; }</xsl:text>
        <xsl:text>.elementTr { background-color:white; text-decoration:normal; }</xsl:text>
        <xsl:text>.constraintTr { background-color:#E8E8E8; text-decoration:normal; }</xsl:text>
        <xsl:text>.constraintStandaloneTr { background-color:white; text-decoration:normal; }</xsl:text>
        <xsl:text>.metadata { text-align:center; border:1px solid red; }</xsl:text>
    </xsl:template>

</xsl:stylesheet>
