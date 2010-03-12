<?xml version="1.0" encoding="UTF-8"?> <!-- -*- nxml -*- -->
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
  <xsl:output method="xml" encoding="UTF-8"/>
  <xsl:param name="left"/> <!-- value to choose on the left -->
  <xsl:param name="right"/> <!-- value to choose on the right -->
  <xsl:param name="side"/> <!-- which side is in use -->

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

<xsl:template match="e">
  <xsl:choose>
    <xsl:when test="not(./@vl) and not(./@vr)">
      <xsl:copy-of select="."/>
    </xsl:when>
    <!-- these are not the sides you are looking for -->
    <xsl:when test="not(count(./@vl)=0) and not(count(./@vr)=0) and not(./@vl=$left) and not(./@vr=$right)">
    </xsl:when>
    <xsl:when test="not(count(./@r)=0) and ./@r=string('LR') and not(count(./@vr)=0) and not(./@vr=$right) and $side=string('left')">
    </xsl:when>
    <xsl:when test="not(count(./@r)=0) and ./@r=string('RL') and not(count(./@vl)=0) and not(./@vl=$left) and $side=string('right')">
    </xsl:when>
    <xsl:when test="count(./@r)=0 and ./@vl=$left and ./@vr=$right">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e>
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="count(./@r)=0 and ./@vr=$right and count(./@vl)=0">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e>
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="count(./@r)=0 and ./@vl=$left and count(./@vr)=0">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e>
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="count(./@r)=0 and not(count(./@vr)=0) and not(./@vr=$right) and count(./@vl)=0">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="count(./@r)=0 and not(count(./@vl)=0) and not(./@vl=$left) and count(./@vr)=0">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}" r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}" r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}" r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="count(./@r)=0 and ./@vl=$left and not(./@vr=$right)">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
<!--
    <xsl:when test="not(count(./@vl)=0) and not(./@vl=$left) and ./@vr=$right and ./@r=string('RL')">
    </xsl:when>
    <xsl:when test="not(count(./@vr)=0) and not(./@vr=$right) and ./@vl=$left and ./@r=string('LR')">
    </xsl:when>
-->
    <xsl:when test="./@vl=$left and not(count(./@r)=0) and ./@r=string('LR')">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}" r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}" r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}" r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e r="LR">
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="./@vr=$right and not(count(./@r)=0) and ./@r=string('RL')">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}" r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e r="RL">
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
	<xsl:when test="not(count(./@r))=0">
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}" r="{./@r}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}" r="{./@r}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}" r="{./@r}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e r="{./@r}">
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
	</xsl:when>
	<xsl:otherwise>
      <xsl:choose>
        <xsl:when test="not(count(./@slr)=0) and not(count(./@srl)=0)">
          <e slr="{./@slr}" srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="not(count(./@slr)=0) and count(./@srl)=0">
          <e slr="{./@slr}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:when test="count(./@slr)=0 and not(count(./@srl)=0)">
          <e srl="{./@srl}">
            <xsl:apply-templates/>
          </e>
        </xsl:when>
        <xsl:otherwise>
          <e>
            <xsl:apply-templates/>
          </e>
        </xsl:otherwise>
      </xsl:choose>
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

