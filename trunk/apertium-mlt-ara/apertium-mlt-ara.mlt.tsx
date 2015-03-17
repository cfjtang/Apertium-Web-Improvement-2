<?xml version="1.0" encoding="utf-8"?>
<tagger name="maltese">
<tagset>

  <def-label name="ABBR">
    <tags-item tags="abbr"/>
  </def-label>
  <def-label name="NEG">
    <tags-item tags="neg"/>
  </def-label>
  <def-label name="VAUX-PP">
    <tags-item tags="vaux.*.pp"/>
    <tags-item tags="vaux.*.pp.*"/>
  </def-label>
  <def-label name="ADJ-COMP">
    <tags-item tags="adj.comp"/>
  </def-label>
  <def-label name="ADJ">
    <tags-item tags="adj.*.*"/>
    <tags-item tags="vblex.*.pp.*.*"/>
    <tags-item tags="vblex.*.pprs.*.*"/>
  </def-label>
  <def-label name="ADV">
    <tags-item tags="adv"/>
    <tags-item tags="adv.*"/>
  </def-label>
  <def-label name="COMMA" closed="true">
    <tags-item tags="cm"/>
  </def-label>
  <def-label name="CNJADV" closed="true">
    <tags-item tags="cnjadv"/>
  </def-label>
  <def-label name="CNJCOO" closed="true">
    <tags-item tags="cnjcoo"/>
  </def-label>
  <def-label name="CNJSUB" closed="true">
    <tags-item tags="cnjsub"/>
  </def-label>

  <def-label name="DET" closed="true">
    <tags-item tags="det.*"/>
  </def-label>
<!--
  <def-label name="DET-DEF" closed="true">
    <tags-item tags="det.def.*"/>
  </def-label>
  <def-label name="DET-DEM" closed="true">
    <tags-item tags="det.dem.*"/>
  </def-label>
  <def-label name="DET-IND" closed="true">
    <tags-item tags="det.ind.*"/>
  </def-label>
  <def-label name="DET-QNT" closed="true">
    <tags-item tags="det.qnt.*"/>
  </def-label>
  <def-label name="DET-ORD" closed="true">
    <tags-item tags="det.ord.*"/>
  </def-label>
-->

  <def-label name="INTERJ" closed="true">
    <tags-item tags="ij"/>
  </def-label>
  <def-label name="N">
    <tags-item tags="n.*.*"/>
    <tags-item tags="np.*"/>
  </def-label>

  <def-label name="N-POSS">
    <tags-item tags="n.*.*.px1pl"/>
    <tags-item tags="n.*.*.px1sg"/>
    <tags-item tags="n.*.*.px2pl"/>
    <tags-item tags="n.*.*.px2sg"/>
    <tags-item tags="n.*.*.px3pl"/>
    <tags-item tags="n.*.*.px3sg_f"/>
    <tags-item tags="n.*.*.px3sg_m"/>
  </def-label>

<!--
  <def-label name="NP">
    <tags-item tags="np.*"/>
  </def-label>
-->

  <def-label name="NUM" closed="true">
    <tags-item tags="num"/>
    <tags-item tags="num.*"/>
  </def-label>
  <def-label name="PREP" closed="true">
    <tags-item tags="pr"/>
  </def-label>
<!--  <def-label name="PRN" closed="true">
    <tags-item tags="prn.*"/>
  </def-label> -->
  <def-label name="PRN-DEF" closed="true">
    <tags-item tags="prn.def.*"/>
  </def-label>
  <def-label name="PRN-DEM" closed="true">
    <tags-item tags="prn.dem.*"/>
  </def-label>
  <def-label name="PRN-IND" closed="true">
    <tags-item tags="prn.ind.*"/>
  </def-label>
  <def-label name="PRN-ITG" closed="true">
    <tags-item tags="prn.itg.*"/>
  </def-label>
  <def-label name="PRN-RECIP" closed="true">
    <tags-item tags="prn.recip.*"/>
  </def-label>
  <def-label name="PRN-REF" closed="true">
    <tags-item tags="prn.ref"/>
    <tags-item tags="prn.ref.*"/>
  </def-label>
  <def-label name="PRNP" closed="true">
    <tags-item tags="prn.p1.*"/>
    <tags-item tags="prn.p2.*"/>
    <tags-item tags="prn.p3.*"/>
  </def-label>


  <def-label name="REL" closed="true">
    <tags-item tags="rel.an.mf.sp"/>
  </def-label>
  <def-label name="VAUX-PL" closed="true">
    <tags-item tags="vaux.*.*.*.*.pl"/>
  </def-label>
  <def-label name="VAUX-SG" closed="true">
    <tags-item tags="vaux.*.*.*.*.sg"/>
  </def-label>
  <def-label name="VBLEX-IMP-SG">
    <tags-item tags="vblex.*.imp.*.*.sg"/>
  </def-label>
  <def-label name="VBLEX-IMP-PL">
    <tags-item tags="vblex.*.imp.*.*.pl"/>
  </def-label>
  <def-label name="VBLEX-PL">
    <tags-item tags="vblex.*.past.*.*.pl"/>
    <tags-item tags="vblex.*.pres.*.*.pl"/>
    <tags-item tags="vblex.*.*.p3.m.pl"/>  <!-- TODO: remove from mt.dix  -->
  </def-label>
  <def-label name="VBLEX-SG">
    <tags-item tags="vblex.*.past.*.*.sg"/>
    <tags-item tags="vblex.*.pres.*.*.sg"/>
    <tags-item tags="vblex.*.*.p3.*.sg.px3sg_m"/>  <!-- TODO: to be removed as well -->
  </def-label>


  <!-- ??? -->
<!--   <def-mult name="adv+neg" closed="true">
    <sequence>
      <tags-item tags="adv"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult> -->
  <def-mult name="pr+adv" closed="true">
    <sequence>
      <tags-item tags="pr"/>
      <tags-item tags="adv"/>
    </sequence>
  </def-mult>
  <def-mult name="pr+det.def.mf.sp" closed="true">
    <sequence>
      <tags-item tags="pr"/>
      <tags-item tags="det.def.mf.sp"/>
    </sequence>
  </def-mult>
  <def-mult name="prn+det" closed="true">
    <sequence>
      <label-item label="PRN"/>
      <label-item label="DET"/>
    </sequence>
  </def-mult>
  <def-mult name="pr+n">
    <sequence>
      <tags-item tags="pr"/>
      <tags-item tags="n.*"/>
    </sequence>
  </def-mult>
  <def-mult name="det.def.mf.sp+n">
    <sequence>
      <tags-item tags="det.def.mf.sp"/>
      <tags-item tags="n.*"/>
    </sequence>
  </def-mult>
  <def-mult name="pr+prnp" closed="true">
    <sequence>
      <tags-item tags="pr"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="pr+prnp+neg" closed="true">
    <sequence>
      <tags-item tags="pr"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vaux.pl+neg" closed="true">
    <sequence>
      <tags-item tags="vaux.*.*.*.*.pl"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vaux.sg+neg" closed="true">
    <sequence>
      <tags-item tags="vaux.*.*.*.*.sg"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>

  <def-mult name="vblex.imp.pl+neg">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.pl"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.imp.sg+neg">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.sg"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>


  <def-mult name="vblex.imp.sg+prnp">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.sg"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.imp.sg+prnp+neg">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.sg"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.imp.sg+prnp+prnp">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.sg"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.imp.sg+prnp+prnp+neg">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.sg"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>

  <def-mult name="vblex.imp.pl+prnp">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.pl"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.imp.pl+prnp+neg">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.pl"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.imp.pl+prnp+prnp">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.pl"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.imp.pl+prnp+prnp+neg">
    <sequence>
      <tags-item tags="vblex.*.imp.*.*.pl"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>


  <def-mult name="vblex.sg+neg">
    <sequence>
      <label-item label="VBLEX-SG"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.sg+prnp">
    <sequence>
      <label-item label="VBLEX-SG"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.sg+prnp+neg">
    <sequence>
      <label-item label="VBLEX-SG"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.sg+prnp+prnp">
    <sequence>
      <label-item label="VBLEX-SG"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.sg+prnp+prnp+neg">
    <sequence>
      <label-item label="VBLEX-SG"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>

  <def-mult name="vblex.pl+neg">
    <sequence>
      <label-item label="VBLEX-PL"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.pl+prnp">
    <sequence>
      <label-item label="VBLEX-PL"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.pl+prnp+neg">
    <sequence>
      <label-item label="VBLEX-PL"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.pl+prnp+prnp">
    <sequence>
      <label-item label="VBLEX-PL"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.pl+prnp+prnp+neg">
    <sequence>
      <label-item label="VBLEX-PL"/>
      <label-item label="PRNP"/>
      <label-item label="PRNP"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>

  <!-- participles are not negated this way, are they? -->
  <def-mult name="vblex.pp.pl+neg">
    <sequence>
      <tags-item tags="vblex.*.pp.pl"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>
  <def-mult name="vblex.pprs+neg">
    <sequence>
      <label-item label="VBLEX-PPRS"/>
      <label-item label="NEG"/>
    </sequence>
  </def-mult>

</tagset>

</tagger>
