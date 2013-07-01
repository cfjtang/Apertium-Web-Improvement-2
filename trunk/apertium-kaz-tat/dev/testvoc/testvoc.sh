if [[ $1 = "tat-kaz" ]]; then
echo "==Tatar->Kazakh===========================";
bash inconsistency.sh tat-kaz > /tmp/tat-kaz.testvoc; bash inconsistency-summary.sh /tmp/tat-kaz.testvoc tat-kaz
echo ""

elif [[ $1 = "kaz-tat" ]]; then
echo "==Kazakh->Tatar===========================";
bash inconsistency.sh kaz-tat > /tmp/kaz-tat.testvoc; bash inconsistency-summary.sh /tmp/kaz-tat.testvoc kaz-tat

fi
