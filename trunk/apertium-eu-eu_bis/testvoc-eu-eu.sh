#!/bin/sh
PREFIX=apertium-eu-eu_bis
LANG1=eu
LANG2=eu_bis
FILTERTAG="(<post>)"

echo "=======================";
echo "Comprovació superficial";
echo "=======================";
echo -n "Calculant expansió inicial...";
#lt-expand $PREFIX.$1.dix | awk 'BEGIN{FS=":"}{if($2!="<") printf("^.<sent>$ ^%s$\n",$NF)}' |apertium-pretransfer | grep -v "REGEXP" > comp-$1-$2.expand
lt-expand $PREFIX.$1.dix > expand-$1-$2
cat expand-$1-$2 | egrep -v "$FILTERTAG" | grep -v "REGEXP" > expand-$1-$2.filtered
cat expand-$1-$2.filtered | awk 'BEGIN{FS=":"}{if($2!="<") printf($1"::::\n")}'  > lemes-$1-$2.expand.filtered
cat expand-$1-$2.filtered | awk 'BEGIN{FS=":"}{if($2!="<") printf("^.<sent>$ ^%s$\n",$NF)}' | apertium-pretransfer > comp-$1-$2.expand.filtered
echo " fet.";

echo -n "Executant el traductor..."
apertium-transfer $PREFIX.$1-$2.t1x $1-$2.t1x.bin $1-$2.autobil.bin < comp-$1-$2.expand.filtered | apertium-interchunk $PREFIX.$1-$2.t2x $1-$2.t2x.bin | apertium-postchunk $PREFIX.$1-$2.t3x $1-$2.t3x.bin | lt-proc -d $1-$2.autogen.bin > comp-$1-$2.trans
echo " fet.";
echo -n "Detectant errades i guardant-les en errors-$1-$2.superficial..."
paste lemes-$1-$2.expand.filtered comp-$1-$2.expand.filtered comp-$1-$2.trans > comprovacio-$1-$2
egrep "(@|/|. #)" comprovacio-$1-$2 > errors-$1-$2.superficial
echo " fet."
#echo "==========================" >> errors-$1-$2.superficial
#echo "ALTRES ERRADES DE BILINGÜE" >> errors-$1-$2.superficial
#echo "==========================" >> errors-$1-$2.superficial
#echo -n "Expandint diccionari bilingüe... "
#if [ $1-$2 = $LANG1-$LANG2 ]
#then lt-expand $PREFIX.$LANG1-$LANG2.dix | awk 'BEGIN{FS=":"}{if($2!="<") printf("^.<sent>$ ^%s$\n",$(NF-1))}' | grep -v "REGEXP" > $$.expand
#else lt-expand $PREFIX.$LANG1-$LANG2.dix | awk 'BEGIN{FS=":"}{if($2!="<") printf("^.<sent>$ ^%s$\n",$NF)}' | grep -v "REGEXP" > $$.expand
#fi
#echo " fet."
#echo -n "Compilant diccionari convertit... "
#if [ $1-$2 = $LANG1-$LANG2 ]
#then lt-comp lr $PREFIX.$LANG1-$LANG2.dix $$.bin >/dev/null
#else lt-comp rl $PREFIX.$LANG1-$LANG2.dix $$.bin >/dev/null
#fi
#echo " fet."
#echo -n "Detectant errades i guardant-les en errors-$1-$2.superficial..."
#awk 'BEGIN{FS=":";}{if($2 == ">") print "^" $1 "$"; else if($2=="<"); else print "^" $1 "$";}' <$$.expand|lt-proc -d $$.bin |grep "/" >>errors-$1-$2.superficial
#echo " fet."
#rm $$.bin
#rm $$.expand

echo "Comprovació superficial finalitzada. Mireu en 'errors-$1-$2.superficial'"



# echo ""
# echo "===================="
#  echo "Comprovació completa"
# echo "===================="

# FASE 2
echo -n "Voleu continuar amb la comprovació completa? Escriviu 'si' per a continuar, 'no' per a cancelar: "
read resposta
if [ $resposta = "si" ]
then
    echo "Continuant comprovació completa, ^C per a cancelar"
    echo -n "Calculant expansió filtrada..."
    cat expand-$1-$2 | egrep "$FILTERTAG" | grep -v "REGEXP" | awk 'BEGIN{FS=":"}{if($2!="<") printf("^.<sent>$ ^%s$\n",$NF)}' | apertium-pretransfer > comp-$1-$2.filtered
    echo " fet."
    echo -n "Executant el traductor..."
    apertium-transfer $PREFIX.$1-$2.t1x $1-$2.t1x.bin $1-$2.autobil.bin < comp-$1-$2.filtered | apertium-interchunk $PREFIX.$1-$2.t2x $1-$2.t2x.bin | apertium-postchunk $PREFIX.$1-$2.t3x $1-$2.t3x.bin | lt-proc -d $1-$2.autogen.bin > comp-$1-$2.trans
    echo " fet."
    echo -n "Detectant errades i guardant-les en errors-$1-$2"
    paste comp-$1-$2.filtered comp-$1-$2.trans > comprovacio-$1-$2
    egrep "(@|/|. #)" comprovacio-$1-$2 > errors-$1-$2
    echo " fet."
    rm expand-$1-$2
    rm expand-$1-$2.filtered
    rm lemes-$1-$2.expand.filtered
    rm comp-$1-$2.expand.filtered
    rm comp-$1-$2.trans
    rm comprovacio-$1-$2

else
    echo "Fi de la comprovació de vocabulari."
    rm expand-$1-$2
    rm expand-$1-$2.filtered
    rm lemes-$1-$2.expand.filtered
    rm comp-$1-$2.expand.filtered
    rm comp-$1-$2.trans
    rm comprovacio-$1-$2
fi