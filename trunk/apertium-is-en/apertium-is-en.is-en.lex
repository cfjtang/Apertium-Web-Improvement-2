DELIMITERS            = (sent) (".") ;
SOFT-DELIMITERS       = ("<,>") ;

LIST BOS              = (>>>);
LIST EOS              = (<<<);

LIST CLB              = (sent) (cm) (cnjsub);

LIST PrnPos           = (prn pos);
LIST PrnPersAnim      = (prn p1) (prn p2) (prn p3 m) (prn p3 f);
LIST Ord              = (ord) ("fyrstur"ri);
LIST Num              = (num) ("milljarður"ri) ("þúsund"ri);

LIST Acc              = (acc);
LIST Gen              = (gen);
LIST Dat              = (dat);

LIST CommVerb         = "tala" "segja" "mæla" "yrða" "spjalla" "kveða" "segja";
LIST Human            = (np ant) (np cog) (prn p2) (prn p3 m) (prn p3 f) ("enginn"ri prn ind);

LIST ISLANDS          = "England" "Ísland" "Kúba" "Haítí" "Kanarí" "Kanaríeyjar" "Jótland"; 
LIST LANGUAGES        = "íslenska" "enska" ;
LIST BUILDINGS        = ".*skóli"ri ".*hús"ri;

LIST @N→              = @N→;
LIST @←N              = @←N;
LIST @SUBJ→           = @SUBJ→;
LIST @←SUBJ           = @←SUBJ;

SECTION

##
## NOUNS 
##

# "bóndi" : 
#        0:"farmer"
#        1:"husband"
SUBSTITUTE (n) (n :0) ("bóndi"ri);
SUBSTITUTE (n :0) (n :1) ("bóndi"ri) (1C PrnPos OR PrnPersAnim + Gen);

# "drottinn" : 
#           0:"king"
#           1:"lord"
SUBSTITUTE (n) (n :0) ("drottinn"ri);
SUBSTITUTE (n :0) (n :1) ("drottinn"ri) (1C ("Guð"));

# "sæti" : 
#       0:"seat"
#       1:"place"
SUBSTITUTE (n) (n :0) ("sæti"ri);
SUBSTITUTE (n :0) (n :1) ("sæti"ri) (-1C Ord);

# "rektor" :
#         0:"vice chancellor"
#         1:"headmaster"
SUBSTITUTE (n) (n :0) ("rektor"ri);

# "bakki"  :
#         0:"tray"
#         1:"bank" [river]
SUBSTITUTE (n) (n :0) ("bakki"ri);

# "aðgerð" :
#         0:"operation"
#         1:"measures"
#         2:"repairs"
SUBSTITUTE (n) (n :0) ("aðgerð"ri);
SUBSTITUTE (n :0) (n :1) ("aðgerð"ri) (0 ("<aðgerða>"ri)) (-1 ("grípa# til"ri));

# "dalur" :
#        0:"valley"
#        1:"dollar"
SUBSTITUTE (n) (n :0) ("dalur"ri);
SUBSTITUTE (n :0) (n :1) ("dalur"ri) (-1 Num);

# "himinn" :
#        0:"sky"
#        1:"heaven"
SUBSTITUTE (n) (n :0) ("himinn"ri);
SUBSTITUTE (n :0) (n :1) ("himinn"ri) (1 ("og"ri)) (2 ("jörð"ri)); # í upphafi

# "land" :
#       0:"land"
#       1:"country"
SUBSTITUTE (n) (n :0) ("land"ri);
SUBSTITUTE (n :0) (n :1) ("land"ri) ((1 @←N) OR (-1 @N→));

# "mynt" :
#       0:"coin"
#       1:"currency"
SUBSTITUTE (n) (n :0) ("mynt"ri);
SUBSTITUTE (n :0) (n :1) ("mynt"ri) (-1* ("í" pr) BARRIER CLB);

# "maður" :
#        0:"man"
#        1:"people"
SUBSTITUTE (n) (n :0) ("maður"ri);
SUBSTITUTE (n :0) (n :1) ("maður"ri) (-1 @N→) (0 Gen);


##
## VERBS
##

# "skipa" : 
#        0:"order"
#        1:"appoint"
#SUBSTITUTE (vblex) (vblex :0) ("skipa"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("skipa"ri) (1C Acc);

# "lýsa" : 
#       0:"show"
#       1:"light"
#       2:"describe"
#SUBSTITUTE (vblex) (vblex :0) ("lýsa"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("lýsa"ri) (1C Acc);

# "greiða" : 
#       0:"pay"
#       1:"comb"
#SUBSTITUTE (vblex) (vblex :0) ("greiða"ri);
#SUBSTITUTE (vblex :0) (vblex :1) ("greiða"ri) (1C Dat);

# "leggja" : 
#       0:"lay"
#       1:"park"
#SUBSTITUTE (vblex) (vblex :0) ("leggja"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("leggja"ri) (1C Dat);

# "þýða" : 
#       0:"mean"
#       1:"translate"
#SUBSTITUTE (vblex) (vblex :0) ("þýða"ri);

# "batna" : 
#        0:"improve"
#        1:"get# better"
#SUBSTITUTE (vblex) (vblex :0) ("batna"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("batna"ri) ((-1C* @SUBJ→ + Dat BARRIER CLB) OR (1C* @←SUBJ + Dat BARRIER CLB));

# "halda" : 
#        0:"hold"
#        1:"think"
#SUBSTITUTE (vblex) (vblex :0) ("halda"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("halda"ri) (1 ("að"));

# "telja" : 
#        0:"count"
#        1:"reckon"
#SUBSTITUTE (vblex) (vblex :0) ("telja"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("telja"ri) (1 ("að"));

# "afgreiða" : 
#        0:"serve"
#        1:"deal# with"
#SUBSTITUTE (vblex) (vblex :0) ("afgreiða"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("afgreiða"ri) (1 ("mál"));

# "jafna" : 
#        0:"level"
#        1:"equalise"
#        2:"compare"
#SUBSTITUTE (vblex) (vblex :0) ("jafna"ri);

# "fylgja" : 
#        0:"accompany"
#        1:"keep up"
#        2:"pay attention"
#SUBSTITUTE (vblex) (vblex :0) ("fylgja"ri);
SUBSTITUTE (vblex :0) (vblex :1) ("fylgja"ri vblex midv) (1C* ("með") BARRIER CLB);
SUBSTITUTE (vblex :0) (vblex :2) ("fylgja"ri vblex midv) (1C* ("með") BARRIER CLB LINK 1* (@X));

# "benda" : 
#        0:""
#        1:""
#        2:""
#SUBSTITUTE (vblex) (vblex :0) ("benda"ri);


## 
## PREPOSITIONS 
##

# "við" : 
#      0: "with"
#      1: "to"
#      2: "beside"
SUBSTITUTE (pr) (pr :0) ("við"ri);
SUBSTITUTE (pr :0) (pr :1) ("við"ri) (-1C* CommVerb BARRIER CLB) (1C Human);

# "á" : 
#    0:"on"
#    1:"in"
#    1:"at"
SUBSTITUTE (pr) (pr :0) ("á"ri);
SUBSTITUTE (pr :0) (pr :1) ("á"ri) (1C ISLANDS OR LANGUAGES);
SUBSTITUTE (pr :0) (pr :2) ("á"ri) (1C BUILDINGS);

##
## PARTICLES
##

# "sem" :
#      0:"that"
#SUBSTITUTE (rel) (rel :0) ("sem"ri);

##
## ADJECTIVES 
## 

# "rómanskur" :
#            0:"Romance"
#            1:"Latin"
SUBSTITUTE (adj) (adj :0) ("rómanskur"ri);
SUBSTITUTE (adj :0) (adj :1) ("rómanskur"ri) (1C ("Ameríka"));
