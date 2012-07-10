<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions">
<xsl:output method="text" encoding="UTF-8" indent="yes"/>

<xsl:strip-space elements="*" />

<xsl:template match="/splitter/paradigms/paradigm">
<xsl:if test="@idForm=substring($word, string-length($word)- string-length(@idForm) +1)"> <!-- ends-with(@idForm, $word) !-->
<xsl:value-of select="@n"/><xsl:text>
</xsl:text>
<xsl:value-of select="@desc"/><xsl:text>
</xsl:text><xsl:value-of select="@kind"/><xsl:text>
</xsl:text>
</xsl:if>
</xsl:template>

<xsl:template match="text()"></xsl:template>

</xsl:stylesheet>
