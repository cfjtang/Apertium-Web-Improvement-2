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
      <tags-item tags="prn.*"/>
    </def-label>
    <def-label name="SENT" closed="true">
      <tags-item tags="sent"/>
    </def-label>
    <def-label name="VBSER" closed="true">
      <tags-item tags="vbser.*"/>
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
    <def-label name="NOMS">
      <tags-item tags="n.*.s.*"/>
    </def-label>
    <def-label name="NOMP">
      <tags-item tags="n.*.p.*"/>
    </def-label>
    <def-label name="NOMD">
      <tags-item tags="n.*.d.*"/>
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
    <def-label name="ADJS">
      <tags-item tags="adj.*.*.s.*"/>
    </def-label>
    <def-label name="ADJP">
      <tags-item tags="adj.*.*.p.*"/>
    </def-label>
    <def-label name="ADJD">
      <tags-item tags="adj.*.*.d.*"/>
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
      <tags-item tags="adv.*"/>
    </def-label>
    <def-label name="CM">
      <tags-item tags="cm.*"/>
    </def-label>
  </tagset>
  <forbid>
    <label-sequence>
      <label-item label="ADJS"/>
      <label-item label="NOMD"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJS"/>
      <label-item label="NOMP"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJD"/>
      <label-item label="NOMS"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJD"/>
      <label-item label="NOMP"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJP"/>
      <label-item label="NOMS"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJP"/>
      <label-item label="NOMD"/>
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
  </forbid>
</tagger>
