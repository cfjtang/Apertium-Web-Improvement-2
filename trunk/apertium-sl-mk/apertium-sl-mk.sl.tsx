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
    <def-label name="NOMM">
      <tags-item tags="n.m.*"/>
    </def-label>
    <def-label name="NOMF">
      <tags-item tags="n.f.*"/>
    </def-label>
    <def-label name="NOMNT">
      <tags-item tags="n.nt.*"/>
    </def-label>
    <def-label name="ABBREV">
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
  </tagset>
  <forbid>
    <label-sequence>
      <label-item label="CNJCOO"/>
      <label-item label="CNJCOO"/>
    </label-sequence>
  </forbid>
</tagger>
