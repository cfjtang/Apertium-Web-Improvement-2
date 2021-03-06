<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions">
<xsl:output method="text" encoding="UTF-8" indent="yes"/>

<xsl:strip-space elements="*" />

<xsl:template match = "/paradigms">
    <xsl:apply-templates select="paradigm"/>
</xsl:template>

<xsl:template match="paradigm">
	<xsl:value-of select="@kind"/>
    <xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="text()"></xsl:template>

</xsl:stylesheet>
