<?xml version="1.0" encoding="UTF-8"?> <!-- -*- nxml -*- -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" encoding="UTF-8"/>

  <xsl:param name="src"/>
  <xsl:param name="trg"/>


  <xsl:template match="@xml:lang">
    <xsl:choose>
      <xsl:when test=".=string($src)">
        <xsl:attribute name="xml:lang"><xsl:value-of select="$trg"/></xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="xml:lang"><xsl:value-of select="."/></xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="tuv">
    <xsl:choose>
      <xsl:when test="./@xml:lang=string($src)">
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
        <apertium-notrans>
          <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
          </xsl:copy>
        </apertium-notrans>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Copy anything else, applying templates
       http://www.xmlplease.com/xsltidentity -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
