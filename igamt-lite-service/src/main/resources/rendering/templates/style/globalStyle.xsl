<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="globalStyle">
        <xsl:text>.masterDatatypeLabel {color:red;}</xsl:text>
        <xsl:text>html {font-family: 'Arial Narrow',sans-serif;}</xsl:text>
        <xsl:text>.contentTable { width:100%; border-spacing: 0px;border-collapse: separate; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px ; border-left-width: 1px; }</xsl:text>
        <xsl:text>.contentThead { background:#F0F0F0; color:#B21A1C; align:center; }</xsl:text>
        <xsl:text>.contentTr { background-color:white; text-decoration:normal; }</xsl:text>
        <xsl:text>.constraintTr { background-color:#E8E8E8; text-decoration:normal; }</xsl:text>
        <xsl:text>.metadata { text-align:center; }</xsl:text>
        <xsl:text>.contentTable td, .contentTable th {border: 1px solid black;}</xsl:text>
    </xsl:template>

</xsl:stylesheet>
