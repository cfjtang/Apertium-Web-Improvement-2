<?xml version="1.0" encoding="UTF-8"?>
<tagger name="afrikaans">
  <tagset>
    <def-label name="ABB">
  <tags-item tags="abb.*"/>
</def-label>
<def-label name="ADJ">
  <tags-item tags="adj.*"/>
</def-label> 
<def-label name="ADV">
  <tags-item tags="adv.*"/>
</def-label> 
<def-label name="CM">
  <tags-item tags="cm.*"/>
</def-label> 
<def-label name="CNJCOO" closed="true">
  <tags-item tags="cnjcoo"/>
</def-label> 
<def-label name="IJ">
  <tags-item tags="ij.*"/>
</def-label> 
<def-label name="LPAR">
  <tags-item tags="lpar.*"/>
</def-label> 
<def-label name="NOM">
  <tags-item tags="n.*"/>
</def-label> 
<def-label name="NUM" closed="true">
  <tags-item tags="num"/>
</def-label> 
<def-label name="PART">
  <tags-item tags="part.*"/>
</def-label> 
<def-label name="PR" closed="true">
  <tags-item tags="pr"/>
</def-label> 
<def-label name="PART" closed="true">
  <tags-item tags="part"/>
</def-label> 
<def-label name="PRN" closed="true">
  <tags-item tags="prn"/>
</def-label> 
<def-label name="RPAR">
  <tags-item tags="rpar.*"/>
</def-label> 
<def-label name="SENT" closed="true">
  <tags-item tags="sent"/>
</def-label> 
<def-label name="VBLEX">
  <tags-item tags="vblex.*"/>
</def-label> 
<def-label name="VBSER" closed="true">
  <tags-item tags="vbser"/>
</def-label> 
<def-label name="">
  <tags-item tags="rpar.*"/>
</def-label> 
  </tagset>
  <forbid>
    <label-sequence>
      <label-item label="NOM"/>
      <label-item label="PRNPERS"/>
    </label-sequence>
    <label-sequence>
      <label-item label="DEURNOM"/>
      <label-item label="DET"/>
    </label-sequence>
  </forbid>
</tagger>

