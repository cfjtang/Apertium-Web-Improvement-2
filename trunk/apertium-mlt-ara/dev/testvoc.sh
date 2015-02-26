#!/bin/bash

cd $(dirname $0)

echo "==Arabic->Maltese===========================";
bash inconsistency.sh ar-mt > /tmp/ar-mt.testvoc; bash inconsistency-summary.sh /tmp/ar-mt.testvoc ar-mt
echo ""
echo "==Maltese->Arabic===========================";
bash inconsistency.sh mt-ar > /tmp/mt-ar.testvoc; bash inconsistency-summary.sh /tmp/mt-ar.testvoc mt-ar
