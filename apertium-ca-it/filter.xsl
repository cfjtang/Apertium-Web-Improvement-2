<?xml version="1.0" encoding="ISO-8859-1"?> <!-- -*- nxml -*- -->
<!--
 Copyright (C) 2005 Universitat d'Alacant / Universidad de Alicante

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 02111-1307, USA.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" encoding="ISO-8859-1"/>
  <xsl:param name="lang"/> <!-- language of the variant being generated -->
  <xsl:param name="side"/> <!-- one of 'left' or 'right' -->

<xsl:template match="alphabet">
  <alphabet><xsl:apply-templates/></alphabet>
</xsl:template>

<xsl:template match="sdefs">
  <sdefs>
    <xsl:apply-templates/>
  </sdefs>
</xsl:template>

<xsl:template match="sdef">
  <sdef n="{./@n}"/>
</xsl:template>

<xsl:template match="pardefs">
  <pardefs>
    <xsl:apply-templates/>
  </pardefs>
</xsl:template>

<xsl:template match="pardef">
  <pardef n="{./@n}">
    <xsl:apply-templates/>
  </pardef>
</xsl:template>

<xsl:template match="e[not(@v)]">
  <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="e[@v]">
  <xsl:choose>
    <xsl:when test="./@v=$lang">
      <xsl:choose>
        <xsl:when test="count(./@r)=0">
          <e>
            <xsl:if test="@lm">
              <xsl:attribute name="lm"><xsl:value-of select="@lm" /></xsl:attribute>
            </xsl:if>
  	    <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e r="{./@r}">
            <xsl:if test="@lm">
              <xsl:attribute name="lm"><xsl:value-of select="@lm" /></xsl:attribute>
            </xsl:if>
  	    <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>    
    </xsl:when>
    <xsl:when test="not($side=string('right')) and not(count(./@v)=0) and not(./@v=$lang) and (./@r=string('RL'))">
    </xsl:when>
    <xsl:when test="not(count(./@v)=0) and not(./@v=$lang)">
      <xsl:choose>
	<xsl:when test="$side=string('left')">
	  <e r="LR">
            <xsl:if test="@lm">
              <xsl:attribute name="lm"><xsl:value-of select="@lm" /></xsl:attribute>
            </xsl:if>
	    <xsl:apply-templates/>
	  </e>
	</xsl:when>
	<xsl:otherwise>
  	  <xsl:if test="not(./@r=string('LR'))">
            <e r="RL">
              <xsl:if test="@lm">
                <xsl:attribute name="lm"><xsl:value-of select="@lm" /></xsl:attribute>
              </xsl:if>
 	      <xsl:apply-templates/>
	    </e>
          </xsl:if>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
	<xsl:when test="not(count(./@r))=0">
	  <e r="{./@r}">
            <xsl:if test="@lm">
              <xsl:attribute name="lm"><xsl:value-of select="@lm" /></xsl:attribute>
            </xsl:if>
	    <xsl:apply-templates/>
	  </e>
	</xsl:when>
	<xsl:otherwise>
	  <e>
            <xsl:if test="@lm">
              <xsl:attribute name="lm"><xsl:value-of select="@lm" /></xsl:attribute>
            </xsl:if>
	    <xsl:apply-templates/>
	  </e>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="p">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="l">
  <l><xsl:apply-templates/></l>
</xsl:template>

<xsl:template match="r">
  <r><xsl:apply-templates/></r>
</xsl:template>

<xsl:template match="s">
  <s n="{./@n}"/>
</xsl:template>

<xsl:template match="b">
  <b/>
</xsl:template>

<xsl:template match="j">
  <j/>
</xsl:template>

<xsl:template match="a">
  <a/>
</xsl:template>

<xsl:template match="re">
  <re><xsl:apply-templates/></re>
</xsl:template>

<xsl:template match="section">
  <section id="{./@id}" type="{./@type}">
    <xsl:apply-templates/>
  </section>
</xsl:template>

<xsl:template match="i">
  <i>
    <xsl:apply-templates/>
  </i>
</xsl:template>

<xsl:template match="g"><g><xsl:apply-templates/></g></xsl:template>

<xsl:template match="par">
  <par n="{./@n}"/>
</xsl:template>

<xsl:template match="dictionary">
<dictionary>
  <xsl:apply-templates/>
</dictionary>
</xsl:template>


</xsl:stylesheet>
