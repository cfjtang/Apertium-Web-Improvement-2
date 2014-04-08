
DELIMITERS = "<.>" "<!>" "<?>" "<...>" "<¶>";

SETS




# I define end of clause and beginning of clause in a way so that the file
# may be read both by the CG-2 and the vislcg formalisms.

LIST BOS = (>>>) (<s>);
LIST EOS = (<<<) (</s>);

# CG3 doesn´t function without >>> and <<< !

# Tags declared as single-membered LISTs 
# ======================================

# Parts of speech
# ---------------

LIST N = n ;
LIST A = a ;
LIST Adv = adv ;
LIST V = v ;
LIST Pron = pron ;

LIST CS = cs ;
LIST CC = cc ;
SET CC-CS = CC OR CS ;
LIST Po = po ;
LIST Pr = pr ;
LIST Pcle = pcle ;
LIST Num = num ;
LIST Interj = interj ;
LIST ABBR = abbr ;
LIST ACR = acr ;

LIST CLB = clb ;
LIST LEFT = left ;
LIST RIGHT = right ;
LIST WEB = web ;
LIST QMARK = """ ; # add " for syntax colouring.
LIST PPUNCT = punct ;
SET PUNCT = PPUNCT - QMARK ;

LIST COMMA = "," ;
LIST ¶ = ¶;

#LIST V* = V* ;

#!! * Sets for POS sub-categories
# ------------------

LIST Pers = pers ;
LIST Dem = dem ;
LIST Interr = interr ;
LIST Indef = indef ;

LIST Recipr = recipr ;
LIST Refl = refl ;
LIST Rel = rel ;
 
#LIST Adp = Adp ;

LIST Coll = coll ;
LIST NomAg = nomag ;
LIST G3 = g3 ;
LIST Prop = prop ;

#!! * Sets for Semantic tags
# -------------

LIST Sem_Ani = sem_ani ;
LIST Sem_Date = sem_date ;
LIST Sem_Fem = sem_fem ;
LIST Sem_Group = sem_group ;
LIST Sem_Hum = sem_hum ;
LIST Sem_Mal = sem_mal ;
LIST Sem_Measr = sem_measr ;
LIST Sem_Money = sem_money ;
LIST Sem_Obj = sem_obj ;
LIST Sem_Org = sem_org ;
LIST Sem_Plc = sem_plc ;
LIST Sem_Sur = sem_sur ;
LIST Sem_Time = sem_time ;
LIST Sem_Year = sem_year ;
SET FIRSTNAME = (prop sem_fem) OR (prop sem_mal) ;

LIST TIME-N-SET = (n sem_time) ;
LIST NOT-TIME = "dulvi" "lohpi" "vuorru" ;
SET TIME-N = TIME-N-SET - NOT-TIME ;


LIST HUMAN = sem_hum sem_mal sem_fem sem_sur pers numag der_numag ;

#!! * Sets for Morphosyntactic properties
# --------------------------

LIST Nom = nom ;
LIST Acc = acc ;
LIST Gen = gen ;
LIST Ill = ill ;
LIST Loc = loc ;
LIST Com = com ;
LIST Ess = ess ;
LIST Ine = ine ;
LIST Ela = ela ;

LIST LOC = loc ine ela ;

LIST Sg = sg ;
LIST Du = du ;
LIST Pl = pl ;

LIST RCmpnd = rcmpnd ;

LIST Cmpnd = cmpnd ;
 
LIST SgNomCmp = sgnomcmp ;
 
LIST SgGenCmp = sggencmp ;

LIST ShCmp = shcmp ;

LIST PxSg1 = pxsg1 ;
LIST PxSg2 = pxsg2 ;
LIST PxSg3 = pxsg3 ;
 
LIST PxDu1 = pxdu1 ;
LIST PxDu2 = pxdu2 ;
LIST PxDu3 = pxdu3 ;
 
LIST PxPl1 = pxpl1 ;
LIST PxPl2 = pxpl2 ;
LIST PxPl3 = pxpl3 ;

LIST Comp = comp ;
LIST Superl = superl ;

LIST Attr = attr ;
LIST Ord = ord ;
 
LIST Qst = qst ;
 
# The ("ge" Pcle) etc. are used in Apertium
LIST Foc_ge = foc_ge ("ge" pcle) ;
LIST Foc_gen = foc_gen ("gen" pcle) ;
LIST Foc_ges = foc_ges ("ges" pcle) ;
LIST Foc_gis = foc_gis ("gis" pcle) ;
LIST Foc_naj = foc_naj ("naj" pcle) ;
LIST Foc_ba = foc_ba ("ba" pcle) ;
LIST Foc_be = foc_be ("be" pcle) ;
LIST Foc_hal = foc_hal ("hal" pcle) ;
LIST Foc_han = foc_han ("han" pcle) ;
LIST Foc_bat = foc_bat ("bat" pcle) ;
LIST Foc_son = foc_son ("son" pcle) ;

LIST IV = iv ;
LIST TV = tv ;

LIST Der_Pass = der_passl der_passs ;

LIST Prt = prt;
LIST Prs = prs ;
 

LIST Ind = ind ;
LIST Pot = pot ;
LIST Cond = cond ;

LIST Imprt = imprt ;
LIST ImprtII = imprtii ;

LIST Sg1 = sg1 ;
LIST Sg2 = sg2 ;
LIST Sg3 = sg3 ;

LIST Du1 = du1 ;
LIST Du2 = du2 ;
LIST Du3 = du3 ;

LIST Pl1 = pl1 ;
LIST Pl2 = pl2 ;
LIST Pl3 = pl3 ;

LIST Inf = inf ;
LIST ConNeg = conneg ;
LIST Neg = neg ;

LIST PrfPrc = prfprc ;
LIST VGen = vgen ;

LIST PrsPrc = prsprc ;
 

LIST Ger = ger ;
LIST Sup = sup ;
LIST Actio = actio ;

LIST Der_PassL = der_passl ;
LIST Der_NomAg = der_nomag ;
LIST Actor = nomag der_nomag ;
LIST VAbess = vabess ;

# Derivation
# ----------

LIST Der_adda = der_adda ;
LIST Der_ahtti = der_ahtti ;
LIST Der_alla = der_alla ;
LIST Der_asti = der_asti ;
LIST Der_easti = der_easti ;
LIST Der_d = der_d ;
LIST Der_NomAct = der_nomact ;
LIST Der_eamoš = der_eamoš ;
LIST Der_amoš = der_amoš ;
LIST Der_eapmi = der_eapmi ;
LIST Der_geahtes = der_geahtes ;
LIST Der_gielat = der_gielat ;
LIST !better: = !better: ;
LIST Der_NuA = der_nua ;

LIST Der_h = der_h ;
LIST Der_heapmi = der_heapmi ;
LIST Der_hudda = der_hudda ;
LIST Der_huhtti = der_huhtti ;
LIST Der_huvva = der_huvva ;
LIST Der_halla = der_halla ;
LIST Der_j = der_j ;
LIST Der_l = der_l ;
LIST Der_laš = der_laš ;
LIST Der_las = der_las ;
LIST Der_hat = der_hat ;

LIST Der_meahttun = der_meahttun ;
LIST Der_muš = der_muš ;
LIST Der_n = der_n ;
LIST Der_st = der_st ;
LIST Der_stuvva = der_stuvva ;
LIST Der_upmi = der_upmi ;
LIST Der_supmi = der_supmi ;
LIST Der_vuohta = der_vuohta ;
LIST Der_goahti = der_goahti ;

LIST Der_lágan = der_lágan ;
LIST Der_lágán = der_lágán ;
LIST Der_lágaš = der_lágaš ;
LIST Der_jagáš = der_jagáš ;
LIST Der_jahkásaš = der_jahkásaš ;
LIST Der_diibmosaš = der_diibmosaš ;

LIST Der_Dimin = der_dimin ;
LIST Der_viđá = der_viđá ;
LIST Der_viđi = der_viđi ;
LIST Der_veara = der_veara ;
LIST Der_duohke = der_duohke ;
LIST Der_duohkai = der_duohkai ;
LIST Der_vuolle = der_vuolle ;
LIST Der_vuollai = der_vuollai ;
LIST Der_vuolde = der_vuolde ;

# Syntactic tags
# --------------

LIST @+FAUXV = @+FAUXV ;
LIST @+FMAINV = @+FMAINV ;
LIST @-FAUXV = @-FAUXV ;
 
LIST @-FMAINV = @-FMAINV ;
LIST @-FSUBJ→ = @-FSUBJ→ ;
LIST @-F←OBJ = @-F←OBJ ;
LIST @-FOBJ→ = @-FOBJ→ ;
LIST @SPRED←OBJ = @SPRED←OBJ ;
LIST @-FADVL = @-FADVL ;

SET FOBJ = @-F←OBJ OR @-FOBJ→ ; 
SET FMAINV = @-FMAINV OR @+FMAINV OR (v @←obj) OR (v @n←) OR (v @a←) OR (v @←subj) OR (v @←advl) ;
SET FAUXV = @-FAUXV OR @+FAUXV ;

LIST @ADVL = @ADVL @ADVL→ @ADVL← @←ADVL @→ADVL @-F←ADVL ;
LIST @→ADVL = @→ADVL ;
LIST @ADVL← = @ADVL← ;
LIST @←ADVL = @←ADVL ;
LIST @ADVL→ = @ADVL→ ;
LIST @ADVL→CS = @ADVL→CS ;

LIST ←hab→ = ←hab→ ;
LIST ←ext→ = ←ext→ ;

 
LIST HAB-V-TAGS = pl3 sg3 conneg prfprc inf ;
 
LIST @→N = @→N ;

LIST @N← = @N← ;
 

LIST @→A = @→A ;

LIST @P← = @P← ;

LIST @→P = @→P ;

LIST @HNOUN = @HNOUN ;
LIST @INTERJ = @INTERJ ;
 
LIST @→Num = @→Num;

LIST @Pron← = @Pron← ;
LIST @→Pron = @→Pron ;

LIST @Num← = @Num← ;

LIST @OBJ = @OBJ ;
LIST @←OBJ = @←OBJ ;
LIST @OBJ→ = @OBJ→ ;
LIST @OPRED = @OPRED ;
LIST @←OPRED = @←OPRED ;
LIST @OPRED→ = @OPRED→ ;
LIST @PCLE = @PCLE ;
LIST @COMP-CS← = @COMP-CS← ;
 
LIST @SPRED = @SPRED ;
LIST @←SPRED = @←SPRED ;
LIST @SPRED→ = @SPRED→ ;
LIST @SUBJ = @SUBJ ;
LIST @←SUBJ = @←SUBJ ;
LIST @SUBJ→ = @SUBJ→ ;
SET SUBJ = @←SUBJ OR @SUBJ→ OR @SUBJ ;
SET SPRED = @←SPRED OR @SPRED→ OR @SPRED ;
SET OPRED = @←OPRED OR @OPRED→ OR @OPRED ;

LIST @PPRED = @PPRED ;
 
LIST @APP = @APP ;
LIST @APP-N← = @APP-N← ;
LIST @APP-Pron← = @APP-Pron← ;
LIST @APP→Pron = @APP→Pron ;
LIST @APP-Num← = @APP-Num← ;
LIST @APP-ADVL← = @APP-ADVL← ;
LIST @VOC = @VOC ;

LIST @CVP = @CVP ;
LIST @CNP = @CNP ;

SET OBJ = (@←obj) OR (@obj→) OR (@obj) OR (@-f←obj) OR (@-fobj→) ;
LIST ←OBJ = @-F←OBJ @←OBJ ;
LIST OBJ→ = @OBJ→ @-FOBJ→ ;

SET ←OBJ-OTHERS = ←OBJ OR (gen) OR (nom) OR (ess) OR (loc) OR (adv) OR (ine) OR (ela) ;
SET OBJ→-OTHERS = OBJ→ OR (gen) OR (nom) OR (ess) OR (loc) OR (adv) OR (ine) OR (ela) ;

SET SYN-V = V + SUBJ OR OBJ + V OR @ADVL + V OR (v @n←) OR (v @a←) OR V + SPRED OR (v @comp-cs←) ;


LIST @X = @X ;


LIST OKTA = "akta" "okta";
LIST go = "go" ;
 

# Initials
# --------

LIST INITIAL = "a" "b" "c" "d" "e" "f" "g" "h" "i" "j" "k" "l" "m"
"n" "o" "p" "q" "r" "s" "t" "u" "v" "w" "x" "y" "z"
"á" "æ" "ø" "å" "ö" "ä" ;

LIST CAP-INITIAL = "A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "M"
"N" "O" "P" "Q" "R" "S" "T" "U" "V" "W" "X" "Y" "Z"
"Á" "Æ" "Ø" "Å" "Ö" "Ä" ;

# Tag sets
# ========
  SETS
# ========


# Word or not
# -----------
LIST WORD = n a adv v pron cs cc po pr interj pcle num abbr acr \? ;
 # any word

SET REALWORD = WORD - Num - Ord ;

SET REALWORD-NOTABBR = WORD - Num - Ord - ABBR ;

SET WORD-NOTDE = WORD - ("de") ; 

# Verb sets
# ---------

# Verbs and their complements
# - - - - - - - - - - - - - -

SET NOT-VERB = WORD - V ;

# Finiteness and mood
# - - - - - - - - - -

SET V-IND-FIN = Prs OR Prt ;
 # Problem: "In boahtán" is an invisible indicative

SET V-MOOD = Ind OR Pot OR Imprt OR ImprtII OR Cond OR (neg sup) ;

LIST GC = ("gč") ;

SET VFIN = GC OR V-MOOD - ConNeg ;

SET VFIN-POS = V-MOOD - ConNeg - Neg ;

SET VFIN-NOT-IMP = VFIN - Imprt ;

SET VFIN-NOT-NEG = VFIN - Neg ;
  # this might be to strict, besides, "iige" can be written "ii ge"

SET NOT-PRFPRC = WORD - PrfPrc ;

# Person
# - - - - 

LIST V-SG1 = (v ind prs sg1) (v ind prt sg1) (v cond prs sg1) 
(v cond prt sg1) (v pot prs sg1) (v neg ind sg1) ;
 
LIST V-SG2 = (v ind prs sg2) (v ind prt sg2) (v cond prs sg2) 
(v cond prt sg2) (v pot prs sg2) (v neg ind sg2) ;
 
LIST V-SG3 = (v ind prs sg3) (v ind prt sg3) (v cond prs sg3) 
(v cond prt sg3) (v pot prs sg3) (v neg ind sg3) ;
 
LIST V-DU1 = (v ind prs du1) (v ind prt du1) (v cond prs du1) 
	 (v cond prt du1) (v pot prs du1) (v neg ind du1) ;
 
LIST V-DU2 = (v ind prs du2) (v ind prt du2) (v cond prs du2) 
	 (v cond prt du2) (v pot prs du2) (v neg ind du2) ;
 
LIST V-DU3 = (v ind prs du3) (v ind prt du3) (v cond prs du3) 
	 (v cond prt du3) (v pot prs du3) (v neg ind du3) ;
 
LIST V-PL1 = (v ind prs pl1) (v ind prt pl1) (v cond prs pl1) 
(v cond prt pl1) (v pot prs pl1) (v neg ind pl1) ;
 
LIST V-PL2 = (v ind prs pl2) (v ind prt pl2) (v cond prs pl2) 
(v cond prt pl2) (v pot prs pl2) (v neg ind pl2) ;
 
LIST V-PL3 = (v ind prs pl3) (v ind prt pl3) (v cond prs pl3) 
(v cond prt pl3) (v pot prs pl3) (v neg ind pl3) ;
 

 # Note that imperative verbs are not included in these sets!

# Some subsets of the VFIN sets
# - - - - - - - - - - - - - - -
SET V-SG = V-SG1 OR V-SG2 OR V-SG3 ;
SET V-DU = V-DU1 OR V-DU2 OR V-DU3 ;
SET V-PL = V-PL1 OR V-PL2 OR V-PL3 ;

SET V-DU-PL = V-DU1 OR V-DU2 OR V-DU3 OR V-PL1 OR V-PL2 OR V-PL3 ;

SET V-NOT-SG1 = VFIN-NOT-IMP - V-SG1 ;
SET V-NOT-SG2 = VFIN-NOT-IMP - V-SG2 ;
SET V-NOT-SG3 = VFIN-NOT-IMP - V-SG3 ;
SET V-NOT-DU1 = VFIN-NOT-IMP - V-DU1 ;
SET V-NOT-DU2 = VFIN-NOT-IMP - V-DU2 ;
SET V-NOT-DU3 = VFIN-NOT-IMP - V-DU3 ;
SET V-NOT-PL1 = VFIN-NOT-IMP - V-PL1 ;
SET V-NOT-PL2 = VFIN-NOT-IMP - V-PL2 ;
SET V-NOT-PL3 = VFIN-NOT-IMP - V-PL3 ;


SET V-1-2 = V-SG1 OR V-SG2 OR V-DU1 OR V-DU2 OR V-PL1 OR V-PL2 ;
SET V-3 = V-SG3 OR V-DU3 OR V-PL3 ;

# Sets consisting of LEAT
# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
LIST LEAT = "leat" "liehket" "lea" ;

SET LEAT-FIN-NOT-IMP = LEAT - Imprt;
 
LIST VPRFPRC = (v prfprc) ;
LIST LEATPRFPRC = ("leat" prfprc) ("liehket" prfprc) ("lea" prfprc) ;
SET PRC-NOT-LEAT = VPRFPRC - LEATPRFPRC ;
    # In order to distinguish between real leat-constructions and participles of other verbs
LIST HABEO-SG3 = ("leat" sg3) (v neg sg3) ("šaddat" sg3) ("sjaddat" sg3) ("liehket" sg3) ("lea" sg3) ;

LIST HABEO-PL3 = ("leat" pl3) (v neg pl3) ("šaddat" pl3) ("sjaddat" pl3) ("liehket" pl3) ("lea" pl3) ;

SET HABEO-3 = HABEO-SG3 OR HABEO-PL3 ;

# Pronoun sets
# ------------

LIST MUN = (pron pers sg1 nom) ;
LIST DON = (pron pers sg2 nom) ;
LIST SON = ("son" pron pers sg3 nom) ;
LIST MOAI = (pron pers du1 nom) ;
LIST DOAI = (pron pers du2 nom) ;
LIST SOAI = (pron pers du3 nom) ;
LIST MII-PERS = (pron pers pl1 nom) ;
LIST DII = (pron pers pl2 nom) ;
LIST SII = ("son" pron pers pl3 nom) ;

SET PPRON-NOM-NOT-DAT = MUN OR DON OR SON OR MOAI OR DOAI OR SOAI OR MII-PERS OR DII OR SII ;

SET PPRON-NOT-DAT = (pron pers) - ("dat") ;

SET PPRON-DU-PL = MOAI OR DOAI OR SOAI OR MII-PERS OR DII OR SII ;
SET PPRON-PL = MII-PERS OR DII OR SII ;

SET PRON-DU = MOAI OR DOAI OR SOAI ;

SET PPRON-NOT-SII = MUN OR DON OR SON OR MOAI OR DOAI OR SOAI OR MII-PERS OR DII ;

LIST PPRON-GEN = (sg1 gen) (sg2 gen) (sg3 gen) (du1 gen) (du2 gen) (du3 gen) 
	 (pl1 gen) (pl2 gen) (pl3 gen) ;

SET PPRON-NOT-GEN = (pron pers) - PPRON-GEN ;

LIST DEM-SG = (pron dem sg nom) ;
LIST DEM-PL = (pron dem pl nom) ;
SET NOT-DEM = WORD - Dem ;

LIST SGPRON = (pron sg1) (pron sg2) (pron sg3) (pron sg) (pron pxsg1) (pron pxsg2) (pron pxsg3) ;
LIST DUPRON = (pron du1) (pron du2) (pron du3) (pron pxdu1) (pron pxdu2) (pron pxdu3) ;
LIST PLPRON = (pron pl1) (pron pl2) (pron pl3) (pron pl) (pron pxpl1) (pron pxpl2) (pron pxpl3) ;
LIST DUPRON-NOTPX = (pron du1) (pron du2) (pron du3) ;

SET PRON-NOT-SG = DUPRON OR PLPRON ;

LIST DAT-PRON = ("dat") ("dát") ("diet") ("duot") ("dot") ;

LIST QUANT-PRON = "ollu" "olu" "unnán" "váháš" "veaháš" "veháš" ;
# This set is for choosing between Adv and Pron Indef.

# Adjectival sets and their complements
# -------------------------------------

SET NOT-A = WORD - A ;
SET NOT-A-COMMA = WORD - A - COMMA ;
SET NOT-Attr = WORD - Attr ;
SET NOT-A-PCLE = WORD - A - Pcle ;

SET NOT-A-ADV = WORD - A - Adv OR ("maid") ;

LIST NOMINAL-ADJ = "guoktilaš" "lámis" "oasálaš" ("suddu" der_laš) "viissis";
 
 # and many others
 
# Adverbial sets and their complements
# ------------------------------------
SET LEX-ADV = Adv - (a*) ;

SET NOT-ADV-DE = WORD - Adv ;
SET NOT-ADV = NOT-ADV-DE OR ("de" adv) OR CLB ;
SET NOT-ADV-N = NOT-ADV - N;
SET NOT-ADV-PCLE = NOT-ADV - Pcle ;
SET NOT-ADV-INDEF = NOT-ADV - Indef ;
SET NOT-ADV-PCLE-ILL = WORD - Adv - Pcle - Ill ;
SET NOT-ADV-PCLE-Refl = WORD - Adv - Pcle - Refl ;
SET NOT-ADV-PCLE-INDEF = WORD - Adv - Pcle - Indef ;
SET NOT-ADV-PCLE-NEG = WORD - Adv - Pcle - Neg ;
SET NOT-ADVL-PCLE-NEG = WORD - @ADVL - @P← - Pcle - Neg ;


LIST MO-MANge = "goas" "gokko" "gos" "gosa" "govt" "makkár" "man" "manne" ("manin" adv) "mo" "mot" "mov" "movt" ("nugo" @cvp) (v qst) ;
 
SET MO = MO-MANge - ("man" foc_ge) ; 
 
 # Introduce finite clauses.
 
LIST PLACE-ADV = "davá#bealde" "mátta#bealde" "nuortta#bealde" "oarje#bealde" "olggo#beale" ;
# There will usually be a Gen in front. 

LIST TIME-ADVL = "dalle" "diibmá" "dolin" "dovle" "duvle" "eske" "gieskat" "ikte" "ovdal" ("ovdditbeaivi" gen) ("vássánáigi" loc) ("boaresáigi" gen) "dál" "ihttin" "odne" "otne" ("boahtteáigi" loc) ("boahtteáigi" gen) ;

LIST TIME-ADV = (adv sem_time) ;


LIST DOPPE = "badjin" "bajil" "dakko" "dá" "dákko" "dáppe" "diekko" "dieppe" "do" "dokko" "doppe" "duo" "duokko" "duoppe" "olgun" ;
# Adverbs with a locativic form, but don´t get Loc as a tag. 

LIST DOHKO = "bajás" "deike" "diehke" "diehko" "dohko" "duohko" "lulás" "olggos" "ruoktot" "sisa" "vuovdimassii" ;
# Adverbs with a illativic form, but they don´t get Ill as a tag.


# Coordinators
# ------------
SET Foc = Foc_ge OR Foc_gen OR Foc_ges OR Foc_gis OR Foc_naj OR Foc_ba OR Foc_be OR Foc_hal OR Foc_han OR Foc_bat OR Foc_son ;

LIST NEGFOC = (neg foc_ge) ;

LIST XGO = "dego" "dugo" "nugo" "seammaládjego" "seammaláhkaigo" ;
  # Compounds
LIST SEAMMAX = "seamma#ládje" "seamma#láhkai" "seamma#láhkái" ;
  # Those combine with go
  
LIST MADE = "mađe" "mađi" ;
LIST DADE = "dađe" "dađi" ; 
  
 
SET CRD = @CNP OR COMMA OR NEGFOC OR XGO OR ("_") OR ("-") OR DADE ;
    # AFTER LCRD vs. GCRD disambiguation
    

LIST ADV-AFTER-NUM = "geardde" ;

LIST DUSSE = "áibbas" "dušše" "erenoamážit" "goitge" "man" "measta" "meastui" "oalle" "oba" "oktiibuot" "oppa" "sullii" ;

LIST GRADE-ADV = "áibbas" "beare" ("eanet" adv) "erenoamán" "erenoamáš" "hirbmat" "hui" "ila" "issoras" ("man" adv) ("mealgat" adv) "measta" "menddo" "muđui" "muhtun_muddui" "nu" "oalle" "oba" ("oppa" adv) "sakka" "seammá" ("veaháš" adv) ("uhccán" adv) ("unnán" adv) "vehá" "veháš" "veahá" "veaháš" "viehka" ;

LIST BUOT = "buot" "gait" "gaitin" "gaitdivnnat" "visot" ;


LIST EASKKA = "easkka" ("easka" adv) ; 

LIST ADV-NOT-ARG = "aiddo" "ain" "aivve" "albma" "aŋkke" "álggos" "bái#fáhkka" "beanta" "beare" ;



LIST ADV-NOT-VERB = "dušše" "viimmat" ;

LIST ADV-NOT-NOUN = "easka" "várra" ;

LIST ADV-NOT-NAMES = "Ain" "Anne" "Diego" "Dieppe" "Enge" "Galle" "Haga"
"Joba" "Johan" "Liikka" "Mai" "Mannes" "Mo" "Mot" "Naba"
"Nan" "Oktan" "Sierra" "Sokka" "Villa" ;

SET ADV-NOT-OTHER-POS = ADV-NOT-VERB OR ADV-NOT-NOUN OR ADV-NOT-NAMES ;

LIST HAB-CASE = loc (←smj→ ine) (←sma→ gen) ;
LIST HAB-ACTOR-ALL = sem_hum sem_mal sem_sur sem_fem sem_ani pers ("gii") indef coll ;
SET HAB-ACTOR = HAB-ACTOR-ALL - ("cihca") ;
SET HAB-ACTOR-NOT-HUMAN = Sem_Org ;

# Sets of elements with common syntactic behaviour
# ================================================

SETS

#!! * Sets for verbs
# -----

LIST NOT-REAL-V = (actio nom) (actio gen) (actio loc) (actio com) prsprc ;

SET REAL-V = V - NOT-REAL-V ;
  #!! ** V is all readings with a V tag in them, REAL-V should
  #!! be the ones without an N tag following the V.  
  #!! The REAL-V set thus awaits a fix to the preprocess V ... N bug.
            
 
 # The set REAL is smaller than COPULAS, made for verbs with PrfPrc complements: Seammás REAL-COPULAS son dovdan iežas...
LIST REAL-COPULAS = "dáidit" "leat" "soaitit" "veadjit" "liehket" "lea";

#!! * The set COPULAS is for predicative constructions
LIST COPULAS = "dáidit" "gártat" "leat" "soaitit" "šaddat" "orrut" "veadjit" "liehket" "sjaddat" "lea" "sjïdtedh" ;
 #  "bissut" ? 
 # 'Dáidit' can appear without 'leat'.

SET NOT-COP-V = V - COPULAS ;

SET MAIN-V = V - FAUXV ;
 
# All active verbs with a TV tag, including AUX-OR-MAIN.

LIST V-TRANS = (v tv) (iv der_ahtti) (iv der_h) ;


SET TRANS-V = V-TRANS - Der_Pass + REAL-V ;

#!! * NP sets defined according to their morphosyntactic features
# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 


LIST N-SG-NOM = (n sg nom) ;

SET HEAD-N = N - RCmpnd ;

SET HEAD-N-NOM = (n nom) - RCmpnd ;

SET SUBJECTHEAD = N OR A OR Pron - Refl ; # These, can be subject heads

SET NP = N OR A ; # anything that can take except numerals
SET NP-HEAD = Pron OR HEAD-N - ("buorre") ;
SET NP-HEAD-SG = SGPRON OR (n sg) OR (a sg) - RCmpnd - Dem - ("buorre") ;
SET NP-HEAD-PL = PLPRON OR (n pl) OR (a pl) - RCmpnd - Dem - ("buorre") ;
SET NP-HEAD-SG-NOM = SGPRON + Nom OR (n sg nom) OR (a sg nom) - ("buorre") - RCmpnd ;
SET NP-HEAD-PL-NOM = PLPRON + Nom OR (n pl nom) OR (a pl nom) - RCmpnd - ("buorre") ;
SET NP-HEAD-NOM = NP-HEAD-SG-NOM OR NP-HEAD-PL-NOM ;
SET NP-HEAD-ACC = (pron acc) OR (n acc) OR (a acc) - RCmpnd - (dem attr) - ("buorre") ;
SET NP-HEAD-GEN = (pron gen) OR (n gen) OR (a gen) - Der_NomAct - RCmpnd - (dem attr) - ("buorre") ;

#!! * The PRE-NP-HEAD family of sets

#!! These sets model noun phrases (NPs). The idea is to first define whatever can
#!! occur in front of the head of the NP, and thereafter negate that with the
#!! expression __WORD - premodifiers__.


SET PRE-NP-HEAD = (prop attr) OR (prop @→n) OR (a attr) OR (abbr attr) OR ("buorre") 
OR (pron pers gen) OR (n gen) OR (a gen) OR ("buot") OR
Num OR RCmpnd OR CC OR (pron dem) OR (pron refl gen) OR (indef attr) OR 
(prfprc @→n) OR PrsPrc OR (a ord) OR (num @→n) OR (a @→n) OR @→N OR @→A OR @→Pron OR @Num← OR (cc @cnp) OR (@→cc) OR (action gen) OR (@pron←) ;
         # The strict version of items that can only be premodifiers, not parts of the predicate

#LIST PRE-NP-HEAD = @>N @>A @>Pron @Num< @CNP ;
         
SET PRE-NP-V = PrfPrc OR PrsPrc OR Der_NomAg OR Actio OR Der_NomAct OR (v a) OR (ind prs sg3) OR (ind prs sg1) OR (imprt du2) ;
    # to be used together with PRE-NP-HEAD before @>N is disambiguated
         
SET NP-MEMBER = PRE-NP-HEAD OR N ;

SET PRE-A-N = (pron pers gen) OR (pron pers acc) OR (pron indef) OR Num OR (a ord) OR (pron dem) OR (pron refl gen) OR (pron refl acc) ; # Acc pga av manglende disambiguering tidlig i fila

SET NOT-PRE-A-N = WORD - PRE-A-N ;

LIST PUNCT-LEFT = (punct left) ;
LIST PUNCT-RIGHT = (punct right) ;

SET PRE-APP = COMMA OR PUNCT-LEFT OR PRE-NP-HEAD ;
 # This set ist not only for what can
                            # stand in front of appositions but also
                            # postmodifiers.



#!! The set __NOT-NPMOD__ is used to find barriers between NPs.
#!! Typical usage: ... (*1 N BARRIER NPT-NPMOD) ...
#!! meaning: Scan to the first noun, ignoring anything that can be
#!! part of the noun phrase of that noun (i.e., "scan to the next NP head")

SET NOT-NPMOD = WORD - PRE-NP-HEAD OR ABBR ; 
# This is the previous NPNH (npnh) set.
# NOT-NPMOD  = "NOT-PRE-NP-HEAD"
 
SET NOT-NPMOD-ACC = NOT-NPMOD - Acc OR ABBR ; 
SET NOT-NPMOD-ACC-ADV = NOT-NPMOD - Acc - Adv OR ABBR ; 
# To be used in mappingrules, before the disambuation of Acc and Gen.                     

SET NOT-NPMODADV = WORD - PRE-NP-HEAD - Adv ; 
# NOT-NPMODADV = "NOT-PRE-NP-HEAD-OR-ADV"
SET NOT-NPMODADV-INDEF = WORD - PRE-NP-HEAD - Adv - Indef ; 
## NOT-NPMODADVI = "     ...-OR-INDEF"
SET NOT-NPMODADVII = WORD - PRE-NP-HEAD - Adv - Indef - Ill ; 
# Illative indir.obj.
SET NOT-NPMODADVIIP = WORD - PRE-NP-HEAD - Adv - Indef - Ill - Pcle ; 
# <== is this our NOT-NPMOD set?
SET NOT-NPMODCC = WORD - PRE-NP-HEAD - COMMA - @CNP ;
SET NAPP = WORD - PRE-APP ;



#!! * Miscellaneous sets
# ------------------
LIST Px = pxsg1 pxsg2 pxsg3 pxdu1 pxdu2 pxdu3 pxpl1 pxpl2 pxpl3 ;

LIST GASKAL = "gaskal" "gaskkal" "gaskii" "gaskka" "gaskkas" ;
 
 # p-positions that like coordination
 
LIST TIME-PP = "badjel" "čađa" "earret" "gaskkal" "guovddáš" "maŋŋel" "maŋŋil" "maŋŋá" "miehtá" "ovdal" ;
 

SET NUM = Num - OKTA ;

# this set does not contain ordinals, I am not sure if that is necessary.

SET NOT-NUM = WORD - Num ;

LIST MANGA = "máŋga" "galle" ;
 # Not referred to by any rule.

SET CARDINALS = Num - Ord - MANGA ;

SET NOT-CC = WORD - CC ;

SET NOT-PCLE = WORD - Pcle ;

LIST COMPAR = ("dávji" a comp) "eanet" "earalágan" "eará" "earret" "seammás" "seammalágan" "seamma_láhkái" ;
 
 # These combine with "go" Pcle, but COMPAR stands for 'comparison' rather than
 # 'comparative'.

LIST CONTRA = "muhto" ;
 # In lean muitalan, muhto dál muitalan.

LIST PROSEANTA = "proseanta" "%" ;

SET REAL-CLB = CLB - COMMA ;


SET NOT-INITIAL-CC = WORD - INITIAL - CC ;



#!! * Border sets and their complements
# ---------------------------------

SET CP = (pron interr) OR (pron rel) OR MO ;

LIST BOUNDARYSYMBOLS = "\;" ":" "-" "–" ;
	
SET S-BOUNDARY = CP OR BOUNDARYSYMBOLS OR ("muhto") OR ("de" adv) OR (neg sup) OR @CVP OR ("vel" adv qst) ;
	# does not include CS, because of "go" in questions, before it is disambugated.
	# includes CP
	# this one includes @CVP, the conjunction which actually connects two sentences (each with a finite verb) to each other, 
	# and not @CNP, which coordinates internal NP-/AdvP-/AP ... coordination
	# To be used only AFTER the disambiguation of @CVP and @CNP taking place in the chapter right before "Disambiguating pronouns"


SET BOC = S-BOUNDARY OR BOS ;
SET BOC-PUNCT = BOC - ("-") - ("–") ; 
SET EOC = S-BOUNDARY OR EOS ;

SET NP-BOUNDARY = BOS OR EOS OR REAL-CLB OR VFIN OR Inf OR (actio ess) OR ConNeg OR VGen OR Sup OR PPRON-NOT-GEN OR Recipr OR Po OR Pr OR Pcle OR ("jed") OR Interj OR CS OR CP OR @CVP ;

SET APP-BOUNDARY = REAL-CLB OR VFIN OR Inf OR (actio ess) OR ConNeg OR VGen OR Sup OR Recipr OR Po OR Pr OR Pcle OR Interj OR CS OR CP OR PrfPrc - @→N ;
# A special barrier used with mapping of appositions. 

# This set contains FMAINV with @, which means that it functions for all kind of mainverbs after the verb-mapping rules 
SET SV-BOUNDARY = S-BOUNDARY OR Inf - FAUXV OR Sup OR FMAINV ;
 # VFIN-NOT-AUX ;
 # should be MAIN-V linked to VFIN-aux to the left. (cg-3)
# This set is ment to use in rules for disambiguating due to verbs or verbsets. It contents @.

SET SVF-BOUNDARY = S-BOUNDARY OR VFIN ;

# This set is ment to use in rules for disambiguating due to verbs or verbsets. 
# Here we search for either an S-BOUNDARY or a finite verb, either aux or main.

LIST ADVLCASE = ill loc com ess ine ela ;
LIST CASE = nom acc gen ill loc com ess ine ela ;


#!! * Syntactic sets
# --------------

LIST ALLSYNTAG = (@+fauxv) (@+fmainv) (@-fauxv) (@-fmainv) (@-fsubj→) (@-fobj) (@-f←advl) (@-fadvl→) (@→a) (@→advl) (@advl←) (@←advl) (@advl→) (@advl) (@→n) (@app) (@app-n←) (@app-pron←) (@app→pron) (@app-num←) (@app-advl←) (@voc) (@cnp) (@cvp) (@p←) (@→p) (@hnoun) (@interj) (@pron←) (@obj) (@obj→) (@←obj) (@opred) (@←opred) (@opred→) (@spred←obj) (@pcle) (@comp-cs←) (@n←) (@spred) (@←spred) (@spred→) (@ppred) (@num←) (@subj) (@←subj) (@subj→) (@x) ;
 
LIST HEAD = @SUBJ→ @←SUBJ @SUBJ @OBJ @OBJ→ @←OBJ @ADVL @ADVL→ @←ADVL @SPRED→ @←SPRED @SPRED @OPRED→ @←OPRED @OPRED @-FSUBJ→ @-FOBJ→ @-FADVL→ @-FSPRED→ @-F←SUBJ @-F←OBJ @-F←ADVL @-F←SPRED ;
 

SET NON-APP = ALLSYNTAG - (@app) ;

#!! These were the set types.



#           Then come the rules
#           Then come the rules
#           Then come the rules
#           Then come the rules
#           Then come the rules
#           Then come the rules


SECTION

SELECT ("for at"i) IF (0 ("<vai>"i)) (NOT 1 Inf) ; 

SELECT ("for å"i) IF (0 ("<vai>"i)) (1 Inf) ;

SELECT ("mat"i) IF (0 ("<biebmu>"i)) ; 

SELECT ("husdyr"i) IF (0 ("<šibit>"i)) ;

#Verbs

#leat

# leat 0 = være, 1 = ha, 2 = måtte («ha å»)
SELECT ("ha"i) IF (0 ("<leat>"i)) (*-1 ←hab→ BARRIER NOT-ADV) ;
SELECT ("ha"i) IF (0 ("<leat>"i)) (-1 Neg) (*-2 ←hab→ BARRIER NOT-ADV) ;
SELECT ("måtte"i) IF (0 ("<leat>"i)) (1 Inf) ;
    # mis lea cahkkehit dola
SELECT ("ha"i) IF (0 ("<leat>"i)) (0 FAUXV) (NOT *1 (Actio Ess) BARRIER S-BOUNDARY) (NOT 1 (Der_PassL PrfPrc)) ;
    # sin giella lea<er> áitojuvvon ja sáhttá jávkat; … sii leat<er> dál<Adv> guorahallamin<Actio> …
	## Leat go Ibsenii sáddemin dieđu?
SELECT ("ha"i) IF (0 ("<leat>"i))(0 (Ind Prt)) (1 Inf) ;
# Jos mun ledjen dadjat sátnegeažige, de ii son lean vuolgit.
# (Perf Cond 2)
# TODO: Gávpotmuvrra vuođđogeađggit ledje<være> čiŋahuvvon 
# (unfortunately no animacy, and impers isn't until bidix...)
# TODO: ledje<ha> ovdalaččas guokte nieidda


LIST CURRENCY = "denara" "dollár" "euro" "kruvdnu" "kr" "ru" "rubel" "ruvdno" "ruvdnu" "¢" "€" "$" ;


SELECT ("betale"i) IF (0 ("<máksit>"i) )(*-1 HUMAN OR Sem_Org LINK 0 (@SUBJ→)) ;
	#$ Máhtte máksá guokte ruvnnu.
 
SELECT ("koste"i) IF (0 ("<máksit>"i) )(*-1 (@SUBJ→) LINK NOT 0 HUMAN)(0* CURRENCY OR QUANT-PRON OR Num BARRIER Ill OR S-BOUNDARY) ;
	#$ Girji máksá guokte ruvnnu.
 
