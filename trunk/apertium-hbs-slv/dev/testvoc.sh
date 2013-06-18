if [[ $1 = "1" ]]; then
echo "==Slovenian->Serbo-Croatian===========================";
bash inconsistency.sh slv-hbs_SR > /tmp/slv-hbs_SR.testvoc; bash inconsistency-summary.sh /tmp/slv-hbs_SR.testvoc slv-hbs_SR
bash inconsistency.sh slv-hbs_HR > /tmp/slv-hbs_HR.testvoc; bash inconsistency-summary.sh /tmp/slv-hbs_HR.testvoc slv-hbs_HR
echo ""

elif [[ $1 = "2" ]]; then
echo "==Serbo-Croatian->Slovenian===========================";
bash inconsistency.sh hbs-slv > /tmp/hbs-slv.testvoc; bash inconsistency-summary.sh /tmp/hbs-slv.testvoc hbs-slv

fi
