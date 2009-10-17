<?xml version="1.0" encoding="UTF-8"?>
<tagger name="slovenian">
  <tagset>
    <def-label name="CNJCOO" closed="true">
      <tags-item tags="cnjcoo"/>
    </def-label>
    <def-label name="PR" closed="true">
      <tags-item tags="pr"/>
    </def-label>
    <def-label name="PART" closed="true">
      <tags-item tags="part"/>
    </def-label>
    <def-label name="PRN" closed="true">
      <tags-item tags="prn"/>
      <tags-item tags="prn.*"/>
    </def-label>
    <def-label name="VBSERFIN" closed="true">
      <tags-item tags="vbser.pres.*"/>
      <tags-item tags="vbser.fti.*"/>
      <tags-item tags="vbser.past.*"/>
    </def-label>
    <def-label name="VBSER" closed="true">
      <tags-item tags="vbser"/>
      <tags-item tags="vbser.*"/>
    </def-label>
    <def-label name="PAREN" closed="true">
      <tags-item tags="rpar"/>
      <tags-item tags="lpar"/>
    </def-label>
    <def-label name="NUM">
      <tags-item tags="num"/>
      <tags-item tags="num.*"/>
    </def-label>
    <def-label name="VBLEXFIN"> 
      <tags-item tags="vblex.pres.*"/>
      <tags-item tags="vblex.pii.*"/>
    </def-label>
    <def-label name="VBLEX">
      <tags-item tags="vblex"/>
      <tags-item tags="vblex.*"/>
    </def-label>
    <def-label name="IJ">
      <tags-item tags="ij"/>
    </def-label>
    <def-label name="ABBREV">
      <tags-item tags="abb"/>
      <tags-item tags="abb.*"/>
    </def-label>
    <def-label name="NOMSG">
      <tags-item tags="n.*.sg.*"/>
    </def-label>
    <def-label name="NOMPL">
      <tags-item tags="n.*.pl.*"/>
    </def-label>
    <def-label name="NOMDU">
      <tags-item tags="n.*.du.*"/>
    </def-label>
    <def-label name="NOMM">
      <tags-item tags="n.m.*"/>
    </def-label>
    <def-label name="NOMF">
      <tags-item tags="n.f.*"/>
    </def-label>
    <def-label name="NOMNT">
      <tags-item tags="n.nt.*"/>
    </def-label>
    <def-label name="ADJSG">
      <tags-item tags="adj.*.*.sg.*"/>
    </def-label>
    <def-label name="ADJPL">
      <tags-item tags="adj.*.*.pl.*"/>
    </def-label>
    <def-label name="ADJDU">
      <tags-item tags="adj.*.*.du.*"/>
    </def-label>
    <def-label name="ADJF">
      <tags-item tags="adj.*.f.*.*"/>
    </def-label>
    <def-label name="ADJM">
      <tags-item tags="adj.*.m.*.*"/>
    </def-label>
    <def-label name="ADJNT">
      <tags-item tags="adj.*.nt.*.*"/>
    </def-label>
    <def-label name="ADV">
      <tags-item tags="adv"/>
      <tags-item tags="adv.*"/>
    </def-label>
  </tagset>
  <forbid>
    <label-sequence>
      <label-item label="ADJSG"/>
      <label-item label="NOMDU"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJSG"/>
      <label-item label="NOMPL"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJDU"/>
      <label-item label="NOMSG"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJDU"/>
      <label-item label="NOMPL"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJPL"/>
      <label-item label="NOMSG"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJPL"/>
      <label-item label="NOMDU"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJF"/>
      <label-item label="ADJM"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJM"/>
      <label-item label="ADJF"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADV"/>
      <label-item label="NOM"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADV"/>
      <label-item label="ADV"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJNT"/>
      <label-item label="NOMM"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJF"/>
      <label-item label="NOMM"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJNT"/>
      <label-item label="NOMF"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJM"/>
      <label-item label="NOMF"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJM"/>
      <label-item label="NOMNT"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJF"/>
      <label-item label="NOMNT"/>
    </label-sequence>


    <!-- We cannot have two finite verbs in the same sentence. Ex.: Vi ste glavni -->
    <label-sequence>
      <label-item label="VBLEXFIN"/>
      <label-item label="VBSERFIN"/>
    </label-sequence>
    <label-sequence>
      <label-item label="VBLEXFIN"/>
      <label-item label="VBLEXFIN"/>
    </label-sequence>
    <label-sequence>
      <label-item label="VBSERFIN"/>
      <label-item label="VBSERFIN"/>
    </label-sequence>

  </forbid>
</tagger>
