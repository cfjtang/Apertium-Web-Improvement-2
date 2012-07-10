<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions">
<xsl:output method="text" encoding="UTF-8" indent="yes"/>

<xsl:strip-space elements="*" />

<xsl:template match = "/splitter/paradigms">
    <xsl:apply-templates select="paradigm[@n=$paradigm]"/>
</xsl:template>

<xsl:template match="/splitter/paradigms/paradigm">
<xsl:param name="rootOfWord">
<xsl:value-of select="substring($word, 0, string-length($word)- string-length(@idForm) +1)"/>
</xsl:param>
<xsl:value-of select="$rootOfWord"/><xsl:text>
</xsl:text>
<xsl:value-of select="$word"/><xsl:text>
</xsl:text>
<xsl:for-each select="flex">
<xsl:value-of select="$rootOfWord"/><xsl:value-of select="@form"/><xsl:text>
</xsl:text>
</xsl:for-each>
</xsl:template>

<xsl:template match="text()"></xsl:template>

</xsl:stylesheet>
