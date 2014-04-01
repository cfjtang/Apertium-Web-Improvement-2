echo "==Serbo-Croatian->Macedonian===========================";
bash inconsistency.sh hbs-mkd > /tmp/hbs-mkd.testvoc; bash inconsistency-summary.sh /tmp/hbs-mkd.testvoc hbs-mkd
echo ""
echo "==Macedonian->Serbo-Croatian===========================";
bash inconsistency.sh mkd-hbs > /tmp/mkd-hbs.testvoc; bash inconsistency-summary.sh /tmp/mkd-hbs.testvoc mkd-hbs
