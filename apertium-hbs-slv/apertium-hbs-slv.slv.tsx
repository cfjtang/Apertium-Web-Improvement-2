<?xml version="1.0" encoding="UTF-8"?>
<tagger name="slovenian">
  <tagset>
    <def-label name="CNJCOO" closed="true">
      <tags-item tags="cnjcoo"/>
    </def-label>
    <def-label name="CNJSUB" closed="true">
      <tags-item tags="cnjsub"/>
    </def-label>
    <def-label name="PR" closed="true">
      <tags-item tags="pr"/>
    </def-label>
    <def-label name="PART" closed="true">
      <tags-item tags="part"/>
    </def-label>
    <def-label name="PRN" closed="true">
      <tags-item tags="prn.*"/>
    </def-label>
    <def-label name="VBSER" closed="true">
      <tags-item tags="vbser.*"/>
    </def-label>
    <def-label name="ABBR" closed="true">
      <tags-item tags="abbr"/>
    </def-label>
    <def-label name="PAREN" closed="true">
      <tags-item tags="rpar.*"/>
      <tags-item tags="lpar.*"/>
    </def-label>
    <def-label name="NUM">
      <tags-item tags="num"/>
      <tags-item tags="num.*"/>
    </def-label>
    <def-label name="VBLEX">
      <tags-item tags="vblex.*"/>
    </def-label>
    <def-label name="IJ">
      <tags-item tags="ij"/>
    </def-label>
    <def-label name="ABBREV">
      <tags-item tags="abb.*"/>
    </def-label>
    <def-label name="NP">
      <tags-item tags="np.*"/>
    </def-label>
    <def-label name="NOMM">
      <tags-item tags="n.m.*"/>
    </def-label>
    <def-label name="NOMMI">
      <tags-item tags="n.mi.*"/>
    </def-label>
    <def-label name="NOMMA">
      <tags-item tags="n.ma.*"/>
    </def-label>
    <def-label name="NOMF">
      <tags-item tags="n.f.*"/>
    </def-label>
    <def-label name="NOMNT">
      <tags-item tags="n.nt.*"/>
    </def-label>
    <def-label name="NOMXM">
      <tags-item tags="n.*.m.*"/>
    </def-label>
    <def-label name="NOMXMI">
      <tags-item tags="n.*.mi.*"/>
    </def-label>
    <def-label name="NOMXMA">
      <tags-item tags="n.*.ma.*"/>
    </def-label>
    <def-label name="NOMXF">
      <tags-item tags="n.*.f.*"/>
    </def-label>
    <def-label name="NOMXNT">
      <tags-item tags="n.*.nt.*"/>
    </def-label>
    <def-label name="ADJF">
      <tags-item tags="adj.f.*.*"/>
    </def-label>
    <def-label name="ADJM">
      <tags-item tags="adj.m.*.*"/>
    </def-label>
    <def-label name="ADJMA">
      <tags-item tags="adj.ma.*.*"/>
    </def-label>
    <def-label name="ADJMI">
      <tags-item tags="adj.mi.*.*"/>
    </def-label>
    <def-label name="ADJNT">
      <tags-item tags="adj.nt.*.*"/>
    </def-label>
    <def-label name="ADJXF">
      <tags-item tags="adj.*.f.*.*"/>
    </def-label>
    <def-label name="ADJXM">
      <tags-item tags="adj.*.m.*.*"/>
    </def-label>
    <def-label name="ADJXMA">
      <tags-item tags="adj.*.ma.*.*"/>
    </def-label>
    <def-label name="ADJXMI">
      <tags-item tags="adj.*.mi.*.*"/>
    </def-label>
    <def-label name="ADJXNT">
      <tags-item tags="adj.*.nt.*.*"/>
    </def-label>
    <def-label name="ADV">
      <tags-item tags="adv.*"/>
    </def-label>
    <def-label name="ADVEMPTY">
      <tags-item tags="adv"/>
    </def-label>
    <def-label name="DET">
      <tags-item tags="det.*"/>
    </def-label>
  </tagset>
  <forbid>
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
  </forbid>
  <preferences>
    <!-- biti - inf/adj/n: prefer inf -->
    <prefer tags="vbser.inf"/>
    <!-- prefer past.f.sg to past.m.du -->
    <prefer tags="vblex.imperf.past.f.sg"/>
    <prefer tags="vblex.perf.past.f.sg"/>
    <prefer tags="vbser.past.f.sg"/>
  </preferences>
</tagger>
