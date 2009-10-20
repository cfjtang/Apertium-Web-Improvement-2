DELIMITERS            = (sent) (".");
SOFT-DELIMITERS       = ("<,>") ;

LIST BOS              = (>>>);
LIST EOS              = (<<<);

LIST Acc              = (acc);
LIST PrnPos           = (prn pos);
LIST Ord              = (ord) ("fyrstur"ri);

SECTION

# "bóndi" : "farmer"
SUBSTITUTE (n) (n :0) ("bóndi"ri);

# "bóndi" : "husband"
SUBSTITUTE (n :0) (n :1) ("bóndi"ri) (1C PrnPos);

# "drottinn" : "king"
SUBSTITUTE (n) (n :0) ("drottinn"ri);

# "drottinn" : "lord"
SUBSTITUTE (n :0) (n :1) ("drottinn"ri) (1C ("Guð"));

# "sæti" : "seat"
SUBSTITUTE (n) (n :0) ("sæti"ri);

# "sæti" : "place"
SUBSTITUTE (n :0) (n :1) ("sæti"ri) (-1C Ord);

##
## VERBS
##

# "skipa" : "order"
SUBSTITUTE (vblex) (vblex :0) ("skipa"ri);

# "skipa" : "appoint"
SUBSTITUTE (vblex :0) (vblex :1) ("skipa"ri) (1C Acc);

