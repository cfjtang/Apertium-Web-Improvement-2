<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions">
<xsl:output method="text" encoding="UTF-8" indent="yes"/>

<xsl:strip-space elements="*" />

<xsl:template match = "/dictionary/pardefs">
    <xsl:apply-templates select="pardef[@n=$paradigm]"/>
</xsl:template>

<xsl:template match="/dictionary/pardefs/pardef/e">
	<xsl:apply-templates/>
	<xsl:text> | </xsl:text>
</xsl:template>

<xsl:template match="/dictionary/pardefs/pardef/e/p/l">
	<xsl:choose>
		<xsl:when test = "text() != ''">
			<xsl:value-of select="text()"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>%</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="/dictionary/pardefs/pardef/e/p/r/s">
	<xsl:text> </xsl:text><xsl:value-of select="@n"/>
</xsl:template>


<xsl:template match="text()"/>

</xsl:stylesheet>
