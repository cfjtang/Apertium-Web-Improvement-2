if [[ $1 = "1" ]]; then
echo "==Slovenian->Serbo-Croatian===========================";
bash inconsistency.sh sl-sh_SR > /tmp/sl-sh_SR.testvoc; bash inconsistency-summary.sh /tmp/sl-sh_SR.testvoc sl-sh_SR
bash inconsistency.sh sl-sh_HR > /tmp/sl-sh_HR.testvoc; bash inconsistency-summary.sh /tmp/sl-sh_HR.testvoc sl-sh_HR
echo ""

elif [[ $1 = "2" ]]; then
echo "==Serbo-Croatian->Slovenian===========================";
bash inconsistency.sh sh-sl > /tmp/sh-sl.testvoc; bash inconsistency-summary.sh /tmp/sh-sl.testvoc sh-sl

fi
