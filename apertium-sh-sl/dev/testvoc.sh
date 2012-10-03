if [[ $1 = "1" ]]; then
echo "==Slovenian->Serbo-Croatian===========================";
bash inconsistency.sh sl-sh > /tmp/sl-sh.testvoc; bash inconsistency-summary.sh /tmp/sl-sh.testvoc sl-sh
echo ""

elif [[ $1 = "2" ]]; then
echo "==Serbo-Croatian->Slovenian===========================";
bash inconsistency.sh sh-sl > /tmp/sh-sl.testvoc; bash inconsistency-summary.sh /tmp/sh-sl.testvoc sh-sl

fi