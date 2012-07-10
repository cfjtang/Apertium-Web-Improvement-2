<?xml version="1.0" encoding="UTF-8"?>
<xslout:stylesheet xmlns:xslout="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xslout:output method="xml" encoding="UTF-8" indent="no"/>
  <xslout:template match = "/">
        <xslout:apply-templates select="*"/>
  </xslout:template>
  <xslout:template match="text()"/>
  <xslout:template match="section">
        <xslout:apply-templates select="*"/>
  </xslout:template>
  <xslout:template match = "e">
    <xslout:if test="i[not(preceding-sibling::*)][count(*)=0][following-sibling::*[1][self::par][count(*)=0][not(following-sibling::*)]]"><xslout:text>
    </xslout:text>
        <xslout:copy-of select="."/>
    </xslout:if>    
  </xslout:template>
</xslout:stylesheet>
