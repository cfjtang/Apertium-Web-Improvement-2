DELIMITERS = "<.>" "<!>" "<?>" "<...>" "<¶>" ;

SOFT-DELIMITERS = "<,>" ;

# =========================================================================== #

SETS

LIST BOS = (>>>) ;
LIST EOS = (<<<) ;

LIST Gen = gen ; 

SECTION

SUBSTITUTE ("s") ("s:1") ("s" pr) (1 Gen);
    ## Poletjeh sa piste == > Полетав од писта
