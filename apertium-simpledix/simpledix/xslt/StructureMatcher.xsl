<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xslout="null">
<xsl:output method="text" encoding="UTF-8" indent="yes"/>

<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>

<xsl:strip-space elements="*" />

<xsl:template match="/">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match = "structures">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="structure">
    <xsl:apply-templates mode="structureRoot"/><xsl:text>
</xsl:text>
</xsl:template>    



<xsl:template match = "*" mode = "structureRoot">    
    <xsl:value-of select="name(.)"/>
    <xsl:text>[</xsl:text>
    <xsl:apply-templates mode="structure"/>
    <xsl:for-each select="*">
        <xsl:text>]</xsl:text>
    </xsl:for-each>
</xsl:template>    


<xsl:template match = "*" mode = "structure">
    <xsl:if test="not(preceding-sibling::*)">
        <xsl:value-of select="name(.)"/>
        <xsl:text>[not(preceding-sibling::*)]</xsl:text>
    </xsl:if>
    <xsl:if test="preceding-sibling::*">
        <xsl:text>[following-sibling::*[1][self::</xsl:text>
        <xsl:value-of select="name(.)"/>
        <xsl:text>]</xsl:text>
    </xsl:if>
    <xsl:choose>
        <xsl:when test="count(*)>0">
            <xsl:text>[</xsl:text>
            <xsl:apply-templates select="*" mode="structure"/>
            <xsl:text>]</xsl:text>
        </xsl:when>
        <xsl:otherwise>
            <xsl:text>[count(*)=0]</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="not(following-sibling::*)">
        <xsl:text>[not(following-sibling::*)]</xsl:text>
        <xsl:for-each select="@*">
            <xsl:text>[attribute::</xsl:text>
            <xsl:value-of select="name()"/>
            <xsl:text>="</xsl:text>
            <xsl:value-of select="."/>
            <xsl:text>"]</xsl:text>
        </xsl:for-each>
    </xsl:if>
    <xsl:for-each select="*">
        <xsl:if test="position()!=1">
            <xsl:text>]</xsl:text>
        </xsl:if>
    </xsl:for-each>
</xsl:template>

<xsl:template match = "text()"/>



</xsl:stylesheet>
