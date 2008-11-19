<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" encoding="UTF-8"/>

<xsl:template match="/">
  <xsl:value-of select="string('&#xa;')"/> <!-- \n -->
  <xsl:apply-templates select="dictionary"/> 
</xsl:template>

<xsl:template match="dictionary">
  <dictionary><xsl:value-of select="string('&#xa;')"/><!-- \n -->
  <xsl:apply-templates select="alphabet"/>
  <xsl:apply-templates select="sdefs"/>
  <xsl:apply-templates select="pardefs"/>
  <xsl:apply-templates select="section"/>
  </dictionary><xsl:value-of select="string('&#xa;')"/><!-- \n -->
</xsl:template>

<xsl:template name="alpha" match="alphabet">
  <alphabet>  
  <xsl:apply-templates />
  </alphabet><xsl:value-of select="string('&#xa;')"/> <!-- \n -->
</xsl:template>

<xsl:template match="sdefs">
  <sdefs>
  <xsl:apply-templates />
  </sdefs>
</xsl:template>

<xsl:template match="sdef">
  <xsl:copy-of select="."/>
</xsl:template>
<!-- Since here, a copy from the dictionary -->




<!-- Generates the new paradigms from pardef -->
<xsl:template match="pardefs">
  <xsl:value-of select="string('&#xa;')"/><!-- \n -->
  <pardefs><xsl:value-of select="string('&#xa;')"/><!-- \n -->

  <!-- Copy the dictionary original paradigms -->
  <xsl:apply-templates />
  <xsl:value-of select="string('&#xa;')"/><!-- \n -->


     <!-- Generates the news paradigms -->

      <!-- in variable mypardef we save the pardefs nodes of the dictionary-->
      <xsl:variable name="mypardefs" select="//pardefs"/>

      <xsl:for-each select="$paradigms">
      <!-- Here the body of each new paradigm -->
      <pardef n='{ concat(@name,"__",@prm) }'> <xsl:value-of select="string('&#xa;')"/><!-- \n -->

	 <!-- In the variable $paradigmname we save the name of the basic paradigm for expansion -->
         <xsl:variable name="paradigmname" select="./@name"/>
	 <!-- and in the variable $paradigmprm we save the name of the variable part for expansion -->
         <xsl:variable name="paradigmprm" select="./@prm"/>
	 <xsl:variable name="paradigmprm2" select="./@prm2"/>
	 <xsl:variable name="paradigmprm3" select="./@prm3"/>
	 <xsl:variable name="paradigmprm4" select="./@prm4"/>
	 <xsl:variable name="paradigmprm5" select="./@prm5"/>

         <xsl:call-template name="pardef-generated" select="$mypardefs/pardef[./@n=$paradigmname]">
	     <xsl:with-param name="parametro" select="$paradigmprm"/>
	     <xsl:with-param name="parametro2" select="$paradigmprm2"/>
	     <xsl:with-param name="parametro3" select="$paradigmprm3"/>
	     <xsl:with-param name="parametro4" select="$paradigmprm4"/>
	     <xsl:with-param name="parametro5" select="$paradigmprm5"/>
	     <xsl:with-param name="ambito" select="$mypardefs/pardef[./@n=$paradigmname]"/>
	 </xsl:call-template> 

      </pardef>
      <xsl:value-of select="string('&#xa;')"/><!-- \n -->
    </xsl:for-each> 

  </pardefs><!--<xsl:value-of select="string('&#xa;')"/>--><!-- \n -->
</xsl:template>





<!--
<xsl:template match="pardef">
    <xsl:copy-of select="."/>
</xsl:template>
-->
<xsl:template match="pardef">
    <pardef n='{@n}'> <xsl:value-of select="string('&#xa;')"/><!-- \n -->
    <xsl:apply-templates/>
    </pardef>
    <xsl:value-of select="string('&#xa;')"/><!-- \n -->
</xsl:template>

<xsl:template name='pardef-generated'>
   <xsl:param name="parametro"/>
   <xsl:param name="parametro2"/>
   <xsl:param name="parametro3"/>
   <xsl:param name="parametro4"/>
   <xsl:param name="parametro5"/>
   <xsl:param name="ambito"/>
   <xsl:apply-templates select="$ambito/e">
      <xsl:with-param name="parametro" select="$parametro"/>
      <xsl:with-param name="parametro2" select="$parametro2"/>
      <xsl:with-param name="parametro3" select="$parametro3"/>
      <xsl:with-param name="parametro4" select="$parametro4"/>
      <xsl:with-param name="parametro5" select="$parametro5"/>
   </xsl:apply-templates>
   <xsl:value-of select="string('&#xa;')"/> <!-- \n -->
</xsl:template>



<!-- Copy the rest of the dictionary deleting "prm" atribute from <par> nodes-->
<xsl:template match="section">
   <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="section">
  <xsl:value-of select="string('&#xa;')"/><!-- \n -->
  <section id='{@id}' type='{@type}'>
  <xsl:apply-templates />
  </section><xsl:value-of select="string('&#xa;')"/><!-- \n -->
</xsl:template>



<xsl:template match="e[@lm]"> <!-- with 'lm' atribute-->
<xsl:param name="parametro"/>
<xsl:param name="parametro2"/>
<xsl:param name="parametro3"/>
<xsl:param name="parametro4"/>
<xsl:param name="parametro5"/>
      <xsl:value-of select="string('                        ')"/>
      <e lm='{@lm}'>
         <xsl:apply-templates>
           <xsl:with-param name="parametro" select="$parametro"/>
	   <xsl:with-param name="parametro2" select="$parametro2"/>
	   <xsl:with-param name="parametro3" select="$parametro3"/>
	   <xsl:with-param name="parametro4" select="$parametro4"/>
	   <xsl:with-param name="parametro5" select="$parametro5"/>
         </xsl:apply-templates>
      </e>   <xsl:value-of select="string('&#xa;')"/> <!-- \n -->
</xsl:template>
<xsl:template match="e[@r]">  <!-- with 'r' atribute-->
<xsl:param name="parametro"/>
<xsl:param name="parametro2"/>
<xsl:param name="parametro3"/>
<xsl:param name="parametro4"/>
<xsl:param name="parametro5"/>
      <xsl:value-of select="string('                        ')"/>
      <e r='{@r}'>
         <xsl:apply-templates>
           <xsl:with-param name="parametro" select="$parametro"/>
	   <xsl:with-param name="parametro2" select="$parametro2"/>
	   <xsl:with-param name="parametro3" select="$parametro3"/>
	   <xsl:with-param name="parametro4" select="$parametro4"/>
	   <xsl:with-param name="parametro5" select="$parametro5"/>
         </xsl:apply-templates>
      </e>   <xsl:value-of select="string('&#xa;')"/> <!-- \n -->
</xsl:template>
<xsl:template match="e[not(@*)]">
<xsl:param name="parametro"/>
<xsl:param name="parametro2"/>
<xsl:param name="parametro3"/>
<xsl:param name="parametro4"/>
<xsl:param name="parametro5"/>
      <xsl:value-of select="string('                        ')"/>
      <e>
        <xsl:apply-templates>
           <xsl:with-param name="parametro" select="$parametro"/>
	   <xsl:with-param name="parametro2" select="$parametro2"/>
	   <xsl:with-param name="parametro3" select="$parametro3"/>
	   <xsl:with-param name="parametro4" select="$parametro4"/>
	   <xsl:with-param name="parametro5" select="$parametro5"/>
        </xsl:apply-templates>
      </e>   <xsl:value-of select="string('&#xa;')"/> <!-- \n -->
</xsl:template>


<!-- If the <par> node have "prm" atribute delete it, otherwise copy -->
<!-- 	We have considered user never uses prm2,... if prm hasn't been used. -->
<xsl:template match="par[@prm]">
	<par n='{ concat(@n,"__",@prm) }' />
<!-- <xsl:copy-of select="."/> -->
</xsl:template>

<xsl:template match="par[not(@prm)]">
       <xsl:copy-of select="."/>
</xsl:template>
<!-- ********************************************************** -->


<xsl:template match="i">
<xsl:param name="parametro"/>
<xsl:param name="parametro2"/>
<xsl:param name="parametro3"/>
<xsl:param name="parametro4"/>
<xsl:param name="parametro5"/>
      <i>
        <xsl:apply-templates>
           <xsl:with-param name="parametro" select="$parametro"/>
	   <xsl:with-param name="parametro2" select="$parametro2"/>
	   <xsl:with-param name="parametro3" select="$parametro3"/>
	   <xsl:with-param name="parametro4" select="$parametro4"/>
	   <xsl:with-param name="parametro5" select="$parametro5"/>
        </xsl:apply-templates>
      </i>
</xsl:template>

<xsl:template match="p/l">
<xsl:param name="parametro"/>
<xsl:param name="parametro2"/>
<xsl:param name="parametro3"/>
<xsl:param name="parametro4"/>
<xsl:param name="parametro5"/>
	<l><!--<xsl:copy-of select="."/>-->
		<xsl:apply-templates>
			<xsl:with-param name="parametro" select="$parametro"/>
			<xsl:with-param name="parametro2" select="$parametro2"/>
			<xsl:with-param name="parametro3" select="$parametro3"/>
			<xsl:with-param name="parametro4" select="$parametro4"/>
			<xsl:with-param name="parametro5" select="$parametro5"/>
		</xsl:apply-templates>
	</l>
</xsl:template>

<xsl:template match="p/r">
	<xsl:param name="parametro"/>
	<xsl:param name="parametro2"/>
	<xsl:param name="parametro3"/>
	<xsl:param name="parametro4"/>
	<xsl:param name="parametro5"/>
	<r>
		<xsl:apply-templates>
			<xsl:with-param name="parametro" select="$parametro"/>
			<xsl:with-param name="parametro2" select="$parametro2"/>
			<xsl:with-param name="parametro3" select="$parametro3"/>
			<xsl:with-param name="parametro4" select="$parametro4"/>
			<xsl:with-param name="parametro5" select="$parametro5"/>
		</xsl:apply-templates>
	</r>
</xsl:template>

<xsl:template match="p">
	<xsl:param name="parametro"/>
	<xsl:param name="parametro2"/>
	<xsl:param name="parametro3"/>
	<xsl:param name="parametro4"/>
	<xsl:param name="parametro5"/>
	<p>
		<xsl:apply-templates>
			<xsl:with-param name="parametro" select="$parametro"/>
			<xsl:with-param name="parametro2" select="$parametro2"/>
			<xsl:with-param name="parametro3" select="$parametro3"/>
			<xsl:with-param name="parametro4" select="$parametro4"/>	
			<xsl:with-param name="parametro5" select="$parametro5"/>
		</xsl:apply-templates>	
	</p>
</xsl:template>

<xsl:template match="re|s|a|b|j">
	<xsl:copy-of select="."/>
</xsl:template>


<xsl:template match="prm">
   <xsl:param name="parametro"/>
   <xsl:value-of select="$parametro"/>   
</xsl:template>

<xsl:template match="prm2">
	<xsl:param name="parametro2"/>
	<xsl:value-of select="$parametro2"/>   
</xsl:template>

<xsl:template match="prm3">
	<xsl:param name="parametro3"/>
	<xsl:value-of select="$parametro3"/> 
</xsl:template>

<xsl:template match="prm4">
	<xsl:param name="parametro4"/>
	<xsl:value-of select="$parametro4"/>
</xsl:template>

<xsl:template match="prm5">
	<xsl:param name="parametro5"/>
	<xsl:value-of select="$parametro5"/>
</xsl:template>


</xsl:stylesheet>
