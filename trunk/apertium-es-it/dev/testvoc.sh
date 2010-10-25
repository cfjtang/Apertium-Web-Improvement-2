echo "==Spanish->Italian============================";
sh inconsistency.sh es-it > /tmp/es-it.testvoc; sh inconsistency-summary.sh /tmp/es-it.testvoc es-it
echo ""
echo "==Italian->Spanish=============================";
bash inconsistency.sh it-es > /tmp/it-es.testvoc; bash inconsistency-summary.sh /tmp/it-es.testvoc it-es
