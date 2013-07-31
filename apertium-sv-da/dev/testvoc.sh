echo "==Swedish->Danish==============================";
sh inconsistency.sh sv-da > /tmp/sv-da.testvoc; sh inconsistency-summary.sh /tmp/sv-da.testvoc sv-da
echo ""
echo "==Danish->Swedish=============================";
bash inconsistency.sh da-sv > /tmp/da-sv.testvoc; bash inconsistency-summary.sh /tmp/da-sv.testvoc da-sv
