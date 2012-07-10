<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xslout="null">
<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>

<xsl:strip-space elements="*" />

<xsl:template match="/">

    <xslout:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xslout:output method="xml" encoding="UTF-8" indent="no"/>
    
    <xslout:template match="/">

        <xslout:copy>
            <xslout:copy-of select="@*"/>
            <xslout:apply-templates/>
        </xslout:copy>

    </xslout:template>

    <xslout:template match="dictionary">

        <xslout:copy>
            <xslout:copy-of select="@*"/>
            <xslout:apply-templates/>
        </xslout:copy>

    </xslout:template>

    <xslout:template match = "alphabet">

        <xslout:copy-of select="."/>
        
    </xslout:template>

    <xslout:template match = "sdefs">

        <xslout:copy-of select="."/>
        
    </xslout:template>
    
    <xslout:template match = "pardefs">

        <xslout:copy>
            <xslout:copy-of select="@*"/>
            <xslout:apply-templates/>
        </xslout:copy>

    </xslout:template>

    <xslout:template match = "pardef">

        <xslout:copy>
            <xslout:copy-of select="@*"/>
            <xslout:choose>
                <xsl:apply-templates select="//paradigm" mode="pardef"/>
                <xslout:otherwise>
<!--
                    <xslout:attribute name = "simple">Default</xslout:attribute>
-->
                </xslout:otherwise>
            </xslout:choose>
        <xslout:copy-of select="*"/>
    </xslout:copy>

    </xslout:template>

    <xslout:template match = "par">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "g">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "a">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "b">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "r">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "l">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "p">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "j">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "re">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "i">
        <xslout:copy-of select= "."/>
    </xslout:template>

    <xslout:template match = "section">
        <xslout:copy>
            <xslout:copy-of select="@*"/>
            <xslout:apply-templates/>
        </xslout:copy>    
    </xslout:template>

    <xslout:template match = "section/e">
        <xslout:copy>
            <xslout:copy-of select="@*"/>
            <xslout:choose>
                <xsl:apply-templates select = "//structure"/>
<!--
                <xsl:apply-templates select="//tag"/>
-->
<!--
                <xsl:apply-templates select="//attribute"/>
-->
<!--
                <xsl:apply-templates select="//paradigm" mode="section"/>
-->
                <xslout:otherwise>
<!--
                    <xslout:attribute name = "simple">Default</xslout:attribute>
-->
                </xslout:otherwise>
            </xslout:choose>
            <xslout:copy-of select = "*"/>
        </xslout:copy>
    </xslout:template>

    <xslout:template match = "comment()">
        <xslout:copy-of select="."/>
    </xslout:template>

    </xslout:stylesheet>
    
</xsl:template>


<xsl:template match="paradigm" mode = "pardef">
    
    <xslout:when>
        <xsl:attribute name = "test">@n='<xsl:value-of select="@n"/>'</xsl:attribute>
        <xslout:attribute name="t">
            <xsl:value-of select="@t"/>
        </xslout:attribute>
        <xslout:attribute name = "fi">
            <xsl:value-of select="@fi"/>
        </xslout:attribute>
    </xslout:when>

</xsl:template>

<xsl:template match="paradigm" mode = "structure">

    <xslout:when>
        <xsl:attribute name = "test">.//par[@n='<xsl:value-of select="@n"/>']</xsl:attribute>
        <xslout:attribute name="t">
            <xsl:value-of select="@t"/>
        </xslout:attribute>
        <xslout:attribute name = "fi">
            <xsl:value-of select="@fi"/>
        </xslout:attribute>
    </xslout:when>
    
</xsl:template>

<xsl:template match = "structures">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="structure">
    <xslout:when>
        <xsl:attribute name = "test"><xsl:apply-templates mode="structureRoot"/></xsl:attribute>
        <xslout:choose>
            <xsl:choose>
                <xsl:when test = "@paradigms">
                     <xsl:for-each select="id(@paradigms)">
                        <xslout:when>
                            <xsl:attribute name = "test">.//par[@n='<xsl:value-of select="@n"/>']</xsl:attribute>
                            <xslout:attribute name="t">
                                <xsl:value-of select="@t"/>
                            </xslout:attribute>
                            <xslout:attribute name = "fi">
                                <xsl:value-of select="@fi"/>
                            </xslout:attribute>
                        </xslout:when>
                    </xsl:for-each>               
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="//paradigms/paradigm">
                        <xslout:when>
                            <xsl:attribute name = "test">.//par[@n='<xsl:value-of select="@n"/>']</xsl:attribute>
                            <xslout:attribute name="t">
                                <xsl:value-of select="@t"/>
                            </xslout:attribute>
                            <xslout:attribute name = "fi">
                                <xsl:value-of select="@fi"/>
                            </xslout:attribute>
                        </xslout:when>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xslout:choose>
    </xslout:when>
</xsl:template>

<xsl:template match = "*" mode = "structureRoot"><xsl:apply-templates mode="structure"/><xsl:for-each select="*"><xsl:if test="position()!=1">]</xsl:if></xsl:for-each></xsl:template>

<xsl:template match = "*" mode = "structure"><xsl:if test="not(preceding-sibling::*)"><xsl:value-of select="name(.)"/>[not(preceding-sibling::*)]</xsl:if><xsl:if test="preceding-sibling::*">[following-sibling::*[1][self::<xsl:value-of select="name(.)"/>]</xsl:if><xsl:choose><xsl:when test="count(*)>0">[<xsl:apply-templates select="*" mode="structure"/>]</xsl:when><xsl:otherwise>[count(*)=0]</xsl:otherwise></xsl:choose><xsl:if test="not(following-sibling::*)">[not(following-sibling::*)]<xsl:for-each select="@*">[attribute::<xsl:value-of select="name()"/>="<xsl:value-of select="."/>"]</xsl:for-each></xsl:if><xsl:for-each select="*"><xsl:if test="position()!=1">]</xsl:if></xsl:for-each></xsl:template>




</xsl:stylesheet>
