<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="yes" />
<xsl:template match="/">
<xsl:for-each select="/highscore/player">
<xsl:sort select="score" data-type="number" order="descending"/>

<xsl:value-of select="name"/>|<xsl:value-of select="score"/>|<xsl:value-of select="longitude"/>|<xsl:value-of select="latitude"/>,
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>

