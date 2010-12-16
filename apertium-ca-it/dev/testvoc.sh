#echo "==Catalan->Italian============================";
#sh inconsistency.sh ca-it > /tmp/ca-it.testvoc; sh inconsistency-summary.sh /tmp/ca-it.testvoc ca-it
#echo ""
echo "==Italian->Catalan=============================";
bash inconsistency.sh it-ca > /tmp/it-ca.testvoc; bash inconsistency-summary.sh /tmp/it-ca.testvoc it-ca
