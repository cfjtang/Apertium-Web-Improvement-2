<?xml version="1.0" encoding="iso-8859-1"?>
<tagger name="euskera">

<tagset>

  <def-label name="ezanedin" closed="true">
    <tags-item lemma="ezan" tags="ADL.*"/>
    <tags-item lemma="edin" tags="ADL.*"/>
    <tags-item lemma="edin" tags="vbsint.*"/>
    <tags-item lemma="ezan" tags="vbsint.*"/>
  </def-label>
   <def-label name="ukan" closed="true">
    <tags-item lemma="ukan" tags="vbsint.*"/>
  </def-label> 
  <def-label name="izan" closed="true">
    <tags-item lemma="izan" tags="vbsint.*"/>
    <tags-item tags="ADL.*"/> <!--treure, provisional tagger -->
  </def-label>
  <def-label name="egin" closed="true"><!--hil egin da -->
    <tags-item lemma="egin" tags="vblex.pp"/>
    <tags-item lemma="egin" tags="vblex.ger"/>
    <tags-item lemma="egin" tags="vblex.pfut"/>
  </def-label> 
 <def-label name="hori" closed="true">
    <tags-item lemma="hori" tags="adj.izo"/>
  </def-label> 

 <def-label name="dagoeneko" closed="true"><!-- EXPERIMENT!!!!!!!!!!! Adverbis lexicalitzats, vull que els trii sempre-->
    <tags-item lemma="dagoeneko" tags="adv"/>
  </def-label> 
 
 <def-label name="zergatik" closed="true"><!-- EXPERIMENT!!!!!!!!!!!-->
    <tags-item lemma="zergatik" tags="adv.itg"/>
  </def-label> 
 
<def-label name="gutxienez" closed="true"><!-- EXPERIMENT!!!!!!!!!!!-->
    <tags-item lemma="gutxienez" tags="adv"/>
    <tags-item lemma="horregatik" tags="cnjloc"/>
    <tags-item lemma="arteraino" tags="spost"/>
  </def-label> 


 <def-label name="dena" closed="true"><!-- EXPERIMENT!!!!!!!!!!!-->
    <tags-item lemma="dena" tags="prn.ind.sg"/>
  </def-label> 


<def-label name="KO" closed="true">
    <tags-item lemma="ko" tags="post"/>
    <tags-item tags="post.ko"/>
  </def-label> 
<def-label name="KO_EN" closed="true">
    <tags-item lemma="ko" tags="post"/>
    <tags-item tags="post.ko"/>
    <tags-item lemma="en" tags="post"/>
  </def-label> 

<def-label name="POST" closed="true">
    <tags-item tags="post"/>
</def-label>

<def-label name="SPOST">
    <tags-item tags="spost"/>
  </def-label>




  <def-label name="PP">
    <tags-item tags="vblex.pp"/>
  </def-label> 




 <def-label name="PPper">
    <tags-item tags="vbper.pp"/>
  </def-label>

 <def-label name="PADV">
    <tags-item tags="vblex.padv"/>
  </def-label> 


   <def-label name="GER">
    <tags-item tags="vblex.ger"/>
   </def-label>


   <def-label name="GERper">
     <tags-item tags="vbper.ger"/>
   </def-label>


   <def-label name="INF">
    <tags-item tags="vblex.inf"/>
   </def-label>




<!--   <def-label name="INFper">
    <tags-item tags="vbper.inf"/>
   </def-label> -->
   <def-label name="PFUT">
    <tags-item tags="vblex.pfut"/>
   </def-label>
   <def-label name="PFUTper">
    <tags-item tags="vbper.pfut"/>
   </def-label>


   <def-label name="VBIZEN">
    <tags-item tags="vblex.izen"/>
  </def-label>




<def-label name="VBSINT"><!--la resta de verbs sintètics, fora dels que poden ser auxiliars (ukan, izan, ezan, edin) -->
    <tags-item tags="vbsint.*"/>
  </def-label>

<def-label name="VBPER"><!--la resta de temps, per ara tots junts -->
    <tags-item tags="vbper.*"/>
  </def-label>
<def-label name="altreverbs">
    <tags-item tags="ATZ.ADI.*"/>
    <tags-item tags="ATZ.vblex.*"/>
    <tags-item tags="ADI.*"/>
  </def-label>


<!-- <def-label name="NOMA">
    <tags-item lemma="*a" tags="n"/>
  </def-label> 


<def-mult name="NOMA_DET">
    <sequence>
      <label-item label="NOMA"/>
      <tags-item tags="det.art.sg"/>
    </sequence>
  </def-mult>
<def-mult name="NOMA_KOEN">
    <sequence>
      <label-item label="NOMA"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="NOMA_POST">
    <sequence>
      <label-item label="NOMA"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult> -->




  <def-label name="NOM">
    <tags-item tags="n"/> 
 </def-label>


 <def-label name="NP">
    <tags-item tags="n.acr"/> 
    <tags-item tags="np.*"/> 
  </def-label>

<!-- <def-label name="NP">
    <tags-item tags="np.*"/>
  </def-label> -->

<def-label name="DETART" closed="true">
    <tags-item tags="det.art.*"/>
  </def-label>

<def-label name="DETDEM" closed="true">
    <tags-item tags="det.dem.*"/>
  </def-label>


<def-label name="DETI" closed="true">
    <tags-item lemma="bat" tags="num.sg"/>
    <tags-item tags="det.ind.*"/>
    <tags-item tags="det.itg.*"/>
    <tags-item tags="ATZ.DET.BAN"/>
  </def-label>


<def-label name="DETINDIZL" closed="true">
    <tags-item tags="det.indizl.*"/>
  </def-label>

<!--
<def-label name="ADJIZOA">
    <tags-item lemma="*a" tags="adj.izo"/>
  </def-label> -->

<!--<def-label name="ADJIZLA">
    <tags-item lemma="*a" tags="adj.izl"/>
  </def-label> -->

<!--
<def-mult name="ADJIZOA_DET">
    <sequence>
      <label-item label="ADJIZOA"/>
      <tags-item tags="det.art.sg"/>
    </sequence>
  </def-mult> -->


<def-label name="ADJIZO">
    <tags-item tags="adj.izo"/>
  </def-label>




<def-label name="ADJIZL">
    <tags-item tags="adj.izl"/>
    <tags-item tags="adj.izl.rom"/>
  </def-label>



<def-label name="PRNTN" closed="true">
    <tags-item tags="prn.tn.*"/>
    <tags-item tags="prn.pers.*"/>
    <tags-item tags="prn.ind.*"/>
    <tags-item tags="prn.itg.*"/>
    <tags-item tags="prn.rec"/>

  </def-label>



<def-label name="NUM" closed="true">
    <tags-item tags="num.*"/>
    <tags-item tags="num"/>
  </def-label>


<def-label name="PREADV">
    <tags-item tags="preadv"/>
  </def-label>


<def-label name="ADV">
    <tags-item tags="adv"/>
  </def-label>



<def-label name="ADVITG">
    <tags-item tags="adv.itg"/>
    <tags-item tags="adv.itg.*"/>
  </def-label>


<def-label name="ADVGEN">
    <tags-item tags="adv.gen"/>
  </def-label>

 <def-label name="BA" closed="true"><!--categoria a part, perquè va davant el verb, i les altres cnjsub van darrera -->
    <tags-item lemma="ba" tags="cnjsub"/>
  </def-label>

 <def-label name="CNJSUBS" closed="true">
    <tags-item tags="cnjsub"/>
    <tags-item tags="LOT.*"/>
  </def-label>
  <def-label name="CNJCOORD" closed="true">
    <tags-item tags="cnjcoo"/>
  </def-label>
  <def-label name="CNJADV">
    <tags-item tags="cnjadv"/>
    <tags-item tags="scnjadv"/>
  </def-label>
  <def-label name="CNJLOC">
    <tags-item tags="cnjloc"/>
  </def-label>


 <def-label name="GRA" closed="true">
    <tags-item tags="GRA.*"/>
    <tags-item tags="gra.*"/>
  </def-label>



  <def-label name="INTERJ">
    <tags-item tags="ij"/>
  </def-label>


  <def-label name="PART" closed="true">
    <tags-item tags="part"/>
  </def-label>

 <!-- <def-label name="RELADV" closed="true">
    <tags-item tags="rel.adv"/>
  </def-label> -->



<def-label name="REL" closed="true">
    <tags-item tags="rel"/>
  </def-label>
<def-label name="PRT" closed="true">
    <tags-item tags="PRT.*"/>
    <tags-item tags="PRT"/>
  </def-label>
<def-label name="BST" closed="true">
    <tags-item tags="BST"/>
  </def-label>


<def-mult name="DETERG" closed="true"><!--amb les noves multis, aquesta ja no s'aplica (28/08/2008) -->
    <sequence>
      <tags-item tags="det.art.sg"/>
      <tags-item lemma="k" tags="post"/>
    </sequence>
  </def-mult>


<def-mult name="DETEN" closed="true"><!--per a  casar-lo amb 'en' superlatiu -->
    <sequence>
      <tags-item tags="det.art.pl"/>
      <tags-item lemma="en" tags="post"/>
    </sequence>
  </def-mult>

<def-mult name="BANA" closed="true">
    <sequence>
      <tags-item lemma="bat" tags="num.sg"/>
      <tags-item lemma="na" tags="ATZ.DET.BAN"/>
    </sequence>
  </def-mult>

<def-mult name="BANAKA" closed="true">
    <sequence>
      <tags-item lemma="bat" tags="num.sg"/>
      <tags-item lemma="na" tags="ATZ.DET.BAN"/>
      <tags-item lemma="ka" tags="ATZ.ADB.ADOARR"/>
    </sequence>
  </def-mult>

<def-mult name="SPOSTKO">
    <sequence>
      <label-item label="SPOST"/>
      <label-item label="KO"/>
    </sequence>
  </def-mult>

<def-mult name="VERBREL_EDIN"><!--esta def-mult antes que la siguiente, para que aquí entren solo ezan/edin + rel -->
    <sequence>
      <label-item label="ezanedin"/>
      <label-item label="REL"/>
    </sequence>
  </def-mult>

<def-mult name="VERBREL">
    <sequence>
      <label-item label="VBSINT"/>
      <label-item label="REL"/>
    </sequence>
  </def-mult>

<def-mult name="VERBREL_POST_EDIN">
    <sequence>
      <label-item label="ezanedin"/>
      <label-item label="REL"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VERBREL_POST">
    <sequence>
      <label-item label="VBSINT"/>
      <label-item label="REL"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VERBREL_ART_EDIN"><!-- zena, dena -->
    <sequence>
      <label-item label="ezanedin"/>
      <label-item label="REL"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="VERBREL_ART"><!-- zena, dena -->
    <sequence>
      <label-item label="VBSINT"/>
      <label-item label="REL"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="VERBREL_ART_KO_EDIN">
    <sequence>
      <label-item label="ezanedin"/>
      <label-item label="REL"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
    </sequence>
  </def-mult>
<def-mult name="VERBREL_ART_KO"><!-- dagoeneko -->
    <sequence>
      <label-item label="VBSINT"/>
      <label-item label="REL"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
    </sequence>
  </def-mult>
<def-mult name="VERBREL_ART_POST_EDIN">
    <sequence>
      <label-item label="ezanedin"/>
      <label-item label="REL"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VERBREL_ART_POST">
    <sequence>
      <label-item label="VBSINT"/>
      <label-item label="REL"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VERB_CNJADV_EDIN">
    <sequence>
      <label-item label="ezanedin"/>
      <label-item label="CNJADV"/>
    </sequence>
  </def-mult>
<def-mult name="VERB_CNJADV">
    <sequence>
      <label-item label="VBSINT"/>
      <label-item label="CNJADV"/>
    </sequence>
  </def-mult>
<def-mult name="VERB_CNJSUBS_EDIN">
    <sequence>
      <label-item label="ezanedin"/>
      <label-item label="CNJSUBS"/>
    </sequence>
  </def-mult>
<def-mult name="VERB_CNJSUBS">
    <sequence>
      <label-item label="VBSINT"/>
      <label-item label="CNJSUBS"/>
    </sequence>
  </def-mult>
<!--
<def-mult name="PRGEN"><!-*-perquè 'zuen' = VERBREL/PRGEN  -*-> 
    <sequence>
      <tags-item tags="prn.pers.*"/>
      <tags-item lemma="en" tags="post"/>
    </sequence>
  </def-mult> -->



<!--les noves mults:-->



<def-mult name="PP_DET">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="PP_GRA">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="gra.*"/>
    </sequence>
  </def-mult>
<def-mult name="PP_KOEN">
    <sequence>
      <label-item label="PP"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="PP_POST">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="PP_KO_POST">
    <sequence>
      <label-item label="PP"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="PP_KO_POST_DET">
    <sequence>
      <label-item label="PP"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="PP_POST_DET">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="PP_POST_DET_POST">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="PP_DET_KOEN">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="PP_DET_KO_POST">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="PP_DET_POST">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="PP_DET_POST_DET">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="PP_DET_POST_DET_POST">
    <sequence>
      <label-item label="PP"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>



<def-mult name="PADV_KOEN">
    <sequence>
      <label-item label="PADV"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>


   <def-mult name="GER_GRA">
     <sequence>
       <tags-item tags="vblex.ger"/>
       <tags-item tags="gra.comp"/>
    </sequence>
   </def-mult>



<def-mult name="VBIZEN_DET">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_KOEN">
    <sequence>
      <label-item label="VBIZEN"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_POST">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_KO_POST">
    <sequence>
      <label-item label="VBIZEN"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_KO_POST_DET">
    <sequence>
      <label-item label="VBIZEN"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_POST_DET">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_POST_DET_POST">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_DET_KOEN">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_DET_KO_POST">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_DET_POST">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_DET_POST_DET">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="VBIZEN_DET_POST_DET_POST">
    <sequence>
      <label-item label="VBIZEN"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>





<def-mult name="NOM_DET"><!--per a casar-lo amb adjectius en -ko? -->
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="det.art.*"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="NOMKOEN"><!--per a casar-lo amb adjectius en -ko? -->
    <sequence>
      <label-item label="NOM"/>
      <label-item label="KO_EN"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>

<def-mult name="NOM_POST"><!--afegit 28/09/07 -->
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="post"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="NOMKO_POST"><!--per a casar-lo amb adjectius en -ko? -->
    <sequence>
      <label-item label="NOM"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="NOMKO_POST_DET"><!--per a casar-lo amb adjectius en -ko? -->
    <sequence>
      <label-item label="NOM"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="NOM_POST_DET">
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="NOM_POST_DET_POST">
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>

<def-mult name="NOM_DET_KOEN">
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO_EN"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="NOM_DET_KO_POST">
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="NOM_DET_POST">
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="NOM_DET_POST_DET">
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="NOM_DET_POST_DET_POST">
    <sequence>
      <label-item label="NOM"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
    <sequence>
      <label-item label="NP"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>





<def-mult name="ADJIZO_DET">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_KOEN">
    <sequence>
      <label-item label="ADJIZO"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_KO_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_KO_POST_DET">
    <sequence>
      <label-item label="ADJIZO"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_POST_DET">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_POST_DET_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_DET_KOEN">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_DET_KO_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_DET_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_DET_POST_DET">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_DET_POST_DET_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>

<def-mult name="ADJIZO_GRA">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="gra.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_GRA_DET">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="gra.*"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_GRA_DET_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="gra.*"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_GRA_KOEN">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="gra.*"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZO_GRA_POST">
    <sequence>
      <label-item label="ADJIZO"/>
      <tags-item tags="gra.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>




<def-mult name="ADJIZL_DET">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_KOEN">
    <sequence>
      <label-item label="ADJIZL"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_KO_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_KO_POST_DET">
    <sequence>
      <label-item label="ADJIZL"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_POST_DET">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_POST_DET_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_DET_KOEN">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_DET_KO_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_DET_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_DET_POST_DET">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_DET_POST_DET_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>

<def-mult name="ADJIZL_GRA">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="gra.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_GRA_DET">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="gra.*"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_GRA_DET_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="gra.*"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_GRA_KOEN">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="gra.*"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>
<def-mult name="ADJIZL_GRA_POST">
    <sequence>
      <label-item label="ADJIZL"/>
      <tags-item tags="gra.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>


<def-mult name="PRNTN_KOEN" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <label-item label="KO_EN"/>
    </sequence>
</def-mult>
<def-mult name="PRNTN_KOEN_DET" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <label-item label="KO_EN"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="PRNTN_POST" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="PRNTN_POST_DET" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="PRNTN_POST_DET_POST" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="PRNTN_POST_DET_POST_DET" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>

<def-mult name="PRNTN_POST_POST" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="PRNTN_POST_POST_KO" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <label-item label="KO"/>
    </sequence>
</def-mult>
<def-mult name="PRNTN_POST_POST_POST" closed="true">
    <sequence>
      <label-item label="PRNTN"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>




<def-mult name="NUM_DET" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="NUM_KOEN" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <label-item label="KO_EN"/>
    </sequence>
</def-mult>
<def-mult name="NUM_POST" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="NUM_KO_POST" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="NUM_KO_POST_DET" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="NUM_DET_KOEN" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO_EN"/>
    </sequence>
</def-mult>
<def-mult name="NUM_DET_KO_POST" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="det.art.*"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="NUM_DET_POST" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="NUM_POST_DET" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="NUM_POST_DET_POST" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>

<def-mult name="NUM_DET_POST_DET" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="NUM_DET_POST_DET_POST" closed="true">
    <sequence>
      <label-item label="NUM"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>



<def-mult name="ADV_KOEN">
    <sequence>
      <tags-item tags="adv"/>
      <label-item label="KO_EN"/>
    </sequence>
</def-mult>

<def-mult name="ADV_POST">
    <sequence>
      <tags-item tags="adv"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="ADV_DET">
    <sequence>
      <tags-item tags="adv"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="ADV_POST_DET">
    <sequence>
      <tags-item tags="adv"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="ADV_DET_POST">
    <sequence>
      <tags-item tags="adv"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="ADV_POST_DET_POST">
    <sequence>
      <tags-item tags="adv"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="ADV_KO_POST">
    <sequence>
      <tags-item tags="adv"/>
      <label-item label="KO"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>

<def-mult name="ADV_GRA">
    <sequence>
      <tags-item tags="adv"/>
      <tags-item tags="gra.*"/>
    </sequence>
</def-mult>
<def-mult name="ADV_GRA_POST">
    <sequence>
      <tags-item tags="adv"/>
      <tags-item tags="gra.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>




<def-mult name="ADVITG_KOEN">
    <sequence>
      <label-item label="ADVITG"/>
      <label-item label="KO_EN"/>
    </sequence>
</def-mult>
<def-mult name="ADVITG_POST">
    <sequence>
      <label-item label="ADVITG"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="ADVITG_POST_DET">
    <sequence>
      <label-item label="ADVITG"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
</def-mult>
<def-mult name="ADVITG_POST_DET_POST">
    <sequence>
      <label-item label="ADVITG"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>
<def-mult name="ADVITG_POST_POST">
    <sequence>
      <label-item label="ADVITG"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
</def-mult>




<def-mult name="DETART_KOEN" closed="true">
    <sequence>
      <label-item label="DETART"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>

<def-mult name="DETART_POST" closed="true">
    <sequence>
      <label-item label="DETART"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETART_POST_ART" closed="true">
    <sequence>
      <label-item label="DETART"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="DETART_POST_ART_POST" closed="true">
    <sequence>
      <label-item label="DETART"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>

<def-mult name="DETART_POST_POST" closed="true">
    <sequence>
      <label-item label="DETART"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETART_POST_POST_KO" closed="true">
    <sequence>
      <label-item label="DETART"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <label-item label="KO"/>
    </sequence>
  </def-mult>
<def-mult name="DETART_POST_POST_POST" closed="true">
    <sequence>
      <label-item label="DETART"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>





<def-mult name="DETDEM_KOEN" closed="true">
    <sequence>
      <label-item label="DETDEM"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>

<def-mult name="DETDEM_POST" closed="true">
    <sequence>
      <label-item label="DETDEM"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETDEM_POST_ART" closed="true">
    <sequence>
      <label-item label="DETDEM"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="DETDEM_POST_ART_POST" closed="true">
    <sequence>
      <label-item label="DETDEM"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>

<def-mult name="DETDEM_POST_POST" closed="true">
    <sequence>
      <label-item label="DETDEM"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETDEM_POST_POST_KO" closed="true">
    <sequence>
      <label-item label="DETDEM"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <label-item label="KO"/>
    </sequence>
  </def-mult>
<def-mult name="DETDEM_POST_POST_POST" closed="true">
    <sequence>
      <label-item label="DETDEM"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>



<def-mult name="DETI_KOEN" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>

<def-mult name="DETI_POST" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETI_ART_POST" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETI_POST_ART" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="DETI_POST_ART_POST" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>

<def-mult name="DETI_POST_POST" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETI_POST_POST_KO" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <label-item label="KO"/>
    </sequence>
  </def-mult>
<def-mult name="DETI_POST_POST_POST" closed="true">
    <sequence>
      <label-item label="DETI"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>



<def-mult name="DETINDIZL_KOEN" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <label-item label="KO_EN"/>
    </sequence>
  </def-mult>

<def-mult name="DETINDIZL_POST" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETINDIZL_ART_POST" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETINDIZL_POST_ART" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
    </sequence>
  </def-mult>
<def-mult name="DETINDIZL_POST_ART_POST" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETINDIZL_POST_ART_POST_POST" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="det.art.*"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETINDIZL_POST_POST" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>
<def-mult name="DETINDIZL_POST_POST_KO" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <label-item label="KO"/>
    </sequence>
  </def-mult>
<def-mult name="DETINDIZL_POST_POST_POST" closed="true">
    <sequence>
      <label-item label="DETINDIZL"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
      <tags-item tags="post"/>
    </sequence>
  </def-mult>


</tagset>

 <forbid>
    <label-sequence>
      <label-item label="NUM"/>
      <label-item label="INF"/>
    </label-sequence>
    <label-sequence>
      <label-item label="DETINDIZL"/>
      <label-item label="INF"/>
    </label-sequence>
  <!--  <label-sequence>
      <label-item label="ADJIZLA"/>
      <label-item label="INF"/>
    </label-sequence> -->
    <label-sequence>
      <label-item label="ADJIZL"/>
      <label-item label="INF"/>
    </label-sequence>
  <!--  <label-sequence>
      <label-item label="ADJIZLA"/>
      <label-item label="DET"/>
    </label-sequence> -->
    <label-sequence>
      <label-item label="ADJIZL"/>
      <label-item label="DETART"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJIZL"/>
      <label-item label="DETDEM"/>
    </label-sequence>
    <label-sequence>
      <label-item label="ADJIZL"/>
      <label-item label="DETI"/>
    </label-sequence>
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="DETART"/>
    </label-sequence> 
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="DETDEM"/>
    </label-sequence> 
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="DETI"/>
    </label-sequence> 
   <!-- <label-sequence>
      <label-item label="INF"/>
      <label-item label="ADJIZOA"/>
    </label-sequence>  
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="ADJIZOA_DET"/>
    </label-sequence> -->
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="ADJIZO"/>
    </label-sequence>  
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="NOM"/>
    </label-sequence> 
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="NOMA"/>
    </label-sequence>
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="NOMA_DET"/>
    </label-sequence>
    <label-sequence><!--joan den -->
      <label-item label="INF"/>
      <label-item label="VERBREL"/>
    </label-sequence>
    <label-sequence><!--joan da -->
      <label-item label="INF"/>
      <label-item label="VBSINT"/>
    </label-sequence>
    <label-sequence><!--joan da -->
      <label-item label="INF"/>
      <label-item label="ukan"/>
    </label-sequence>
    <label-sequence><!--joan da -->
      <label-item label="INF"/>
      <label-item label="izan"/>
    </label-sequence>
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="SENT"/>
    </label-sequence>
    <label-sequence>
      <label-item label="VERBREL"/>
      <label-item label="SENT"/>
    </label-sequence>
    <label-sequence>
      <label-item label="GRA"/>
      <label-item label="SPOST"/>
    </label-sequence> 
    <label-sequence>
      <label-item label="GRA"/>
      <label-item label="ADVGEN"/>
    </label-sequence> 
    <label-sequence>
      <label-item label="INF"/>
      <label-item label="GRA"/>
    </label-sequence> 
    <label-sequence><!--? correcte? (etorri zuen) -->
      <label-item label="PP"/>
      <label-item label="PRGEN"/>
    </label-sequence> 
  <!--  <label-sequence>
      <label-item label="SENT"/>
      <label-item label="ADJIZOA"/>
    </label-sequence> -->
    <label-sequence><!--. Ezker abertzalea -->
      <label-item label="SENT"/>
      <label-item label="ADJIZO"/>
    </label-sequence>
    <label-sequence><!--ui no sé, només una prova -->
      <label-item label="NOMA"/>
      <label-item label="NOMA"/>
    </label-sequence>
 <!--   <label-sequence>
      <label-item label="NOMA_DET"/>
      <label-item label="NOMA"/>
    </label-sequence> -->
  <!--  <label-sequence>
      <label-item label="NOMA"/>
      <label-item label="NOMA_DET"/>
    </label-sequence>
    <label-sequence>
      <label-item label="NOMA"/>
      <label-item label="NOM"/>
    </label-sequence>
    <label-sequence>
      <label-item label="NOMA"/>
      <label-item label="NOMKO"/>
    </label-sequence>
    <label-sequence>
      <label-item label="NOMA"/>
      <label-item label="NOM_POST"/>
    </label-sequence> -->
    <label-sequence>
      <label-item label="NOMA"/>
      <label-item label="SENT"/>
    </label-sequence>
 <!--   <label-sequence><!-*-belagarriak dira -*->
      <label-item label="DETERG"/>
      <label-item label="izan"/> 
    </label-sequence> -->
    <label-sequence>
      <label-item label="NOM_POST"/><!--unerik beroena -->
      <label-item label="ADJIZO_DET_POST_DET"/>
    </label-sequence>
    <label-sequence>
      <label-item label="NOM"/><!--ez du erantzun ere -->
      <label-item label="ADV"/>
    </label-sequence>
    <label-sequence>
      <label-item label="VERBREL_ART_KO"/><!--atxilotuta daude dagoeneko. -->
      <label-item label="SENT"/>
    </label-sequence>

<!--    <label-sequence>
      <label-item label="PRT"/>
      <label-item label="NOM"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PRT"/>
      <label-item label="NOMA"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PRT"/>
      <label-item label="NOMA_DET"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PRT"/>
      <label-item label="NOMKO"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PRT"/>
      <label-item label="NOM_POST"/>
    </label-sequence> -->
    <label-sequence>
      <label-item label="PP"/>
      <label-item label="ezanedin"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PP"/>
      <label-item label="VERBREL_EDIN"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PP"/>
      <label-item label="VERBREL_POST_EDIN"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PP"/>
      <label-item label="VERB_CNJADV_EDIN"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PP"/>
      <label-item label="VERB_CNJSUBS_EDIN"/>
    </label-sequence>
    <label-sequence>
      <label-item label="PPper"/><!-- ????? eraman dezaten-->
      <label-item label="ezanedin"/>
    </label-sequence>
    <label-sequence><!-- , izugarrizko arriskuak -->
      <label-item label="CM"/>
      <label-item label="ADJIZO_POST"/>
    </label-sequence>
    <label-sequence><!-- , izugarrizko arriskuak -->
      <label-item label="CM"/>
      <label-item label="ADJIZO_KOEN"/>
    </label-sequence>


 </forbid>

 <enforce-rules>
   <enforce-after label="hori">
      <label-set>
        <label-item label="DETART"/>
      </label-set>
    </enforce-after>
   
   <enforce-after label="PRT">
      <label-set>
        <label-item label="ukan"/>
        <label-item label="izan"/>
        <label-item label="VBSINT"/>
      </label-set>
    </enforce-after>

     <enforce-after label="INF">
      <label-set>
        <label-item label="ezanedin"/>
        <label-item label="VERBREL_EDIN"/>
        <label-item label="VERBREL_POST_EDIN"/>
        <label-item label="VERBREL_ART_EDIN"/>
        <label-item label="VERBREL_ART_KO_EDIN"/>
        <label-item label="VERBREL_ART_POST_EDIN"/>
        <label-item label="VERB_CNJADV_EDIN"/>
        <label-item label="VERB_CNJSUBS_EDIN"/>
      </label-set>
    </enforce-after>

 </enforce-rules>

  <preferences><!--nago, etc. -->
   <prefer tags="vbsint.pri.*"/>
   <prefer tags="vbsint.pii.*"/>
   <prefer tags="vbper.pri.*"/>
   <prefer tags="vbper.pii.*"/>
   <prefer tags="np.*"/>
  </preferences>

</tagger>
