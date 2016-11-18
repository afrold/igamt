<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template name="htmlStyle">
        <xsl:text>#sidebar { float:left; width:30%; background:#F0F0F0; overflow: auto; max-height: 100vh; font-family: 'Arial Narrow', sans-serif; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
        <xsl:text>#sidebar a:link { color: #000066; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
        <xsl:text>#sidebar a:visited { color: green; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
        <xsl:text>#sidebar a:hover { color: hotpink; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
        <xsl:text>#sidebar a:active { color: blue; margin-top: 1px; margin-bottom: 1px; }</xsl:text>
        <xsl:text>#main { float:right; width:70%; overflow: auto; max-height: 100vh; }</xsl:text>
        <xsl:text>#notoc { float:right; width:100%; overflow: auto; max-height: 100vh; }</xsl:text>
        <xsl:text>.divh1 { padding-left: 15px; }</xsl:text>
        <xsl:text>.divh2 { padding-left: 30px; }</xsl:text>
        <xsl:text>.divh3 { padding-left: 45px; }</xsl:text>
        <xsl:text>.divh4 { padding-left: 60px; }</xsl:text>
        <xsl:text>.divh5 { padding-left: 75px; }</xsl:text>
        <xsl:text>.divh6 { padding-left:90px; }</xsl:text>
        <xsl:text>.hidden { display: none; }</xsl:text>
        <xsl:text>.unhidden { display:block; }</xsl:text>
        <xsl:text>.btn { float:right; }</xsl:text>
    </xsl:template>

</xsl:stylesheet>
