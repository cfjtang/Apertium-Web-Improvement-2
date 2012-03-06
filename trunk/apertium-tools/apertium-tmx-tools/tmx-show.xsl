<?xml version="1.0" encoding="UTF-8"?> <!-- -*- nxml -*- -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" encoding="UTF-8"/>

  <!-- Unwrap notrans: -->
  <xsl:template match="apertium-notrans">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- Copy anything else, applying templates
       http://www.xmlplease.com/xsltidentity -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
