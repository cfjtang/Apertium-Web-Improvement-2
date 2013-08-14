#!/bin/sh

sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><f><sp>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><m><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><m><sp>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<adj><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<adj><m><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<adj><ind><m><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<adj><ind><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<vblex><pp><m><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<vblex><pp><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<num>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<vblex><inf>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<adj><mf><sg>[$])[ ]+([\^][[:alnum:]]+<n><f><sg>[$])/^el<det><def><f><sg>$ \1 \2/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<adj><mf><sg>[$])[ ]+([\^][[:alnum:]]+<n><m><sg>[$])/^el<det><def><m><sg>$ \1 \2/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<prn><tn><m><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<prn><tn><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><acr><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><acr><m><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<adj><mf><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><mf><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><acr><f><sp>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:]]+<n><acr><m><sp>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^][[:alnum:],.]+<num>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Ariadna<np><ant>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Alberto<np><ant>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Oriol<np><ant>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Erika<np><ant>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ONG<np><al>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Hospitalet<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Obreg�n<np><ant>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ABC<np><al>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]absolutament<adv>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]actualment<adv>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ad�u<ij>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]agreujar<vblex><ger>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ahir<adv>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ai<ij>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Albacete<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]alerta<adv>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]aleshores<cnjadv>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Almudena<np><ant>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]alt<adj><sup><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Am�rica<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Ana<np><ant>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Anna<np><loc>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Anoia<np><loc>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Antonio<np><ant>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]a<pr>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ara<adv>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Arag�<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Arnau<np><ant>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]assaltar<vblex><ger>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Athletic<np><al>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]atreure<vblex><ger>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ATS<n><acr><mf><sp>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]autopista<n><f><pl>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]avui<adv>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Eduard<np><ant>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]el<detnt>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Elx<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]encara<adv>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]en<pr>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ensenyar<vblex><ger>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]entitat<n><f><pl>[$])/^el<det><def><f><pl>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]envoltar<vblex><ger>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Espanya<np><loc>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]espanyol<adj><sup><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]es<prn><pro><ref><p3><mf><sp>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]este<prn><tn><f><pl>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]estrany<adj><sup><f><sg>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]estudiant<n><f><pl>[$])/^el<det><def><f><pl>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Europa<np><loc>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Euskadi<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Extremadura<np><loc>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]FP<np><al>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]haver#_de<vbmod><inf>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]haver<vbhaver><inf>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]hist�ricament<adv>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]i<cnjcoo>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]IES<np><al>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]inicialment<adv>[$])/^el<det><def><f><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]�<lquest>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]MP3<np><al>[$])/^el<det><def><m><sg>$ \1/g" |\
#sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]o<cnjcoo>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Olivia<np><ant>[$])/^el<det><def><f><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]onze<num><mf><sp>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Orense<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Osasuna<np><al>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]Oviedo<np><loc>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]ser<vbser><pp><m><sg>[$])/^el<det><def><m><sg>$ \1/g" |\
sed -re "s/[\^]el<det><def><mf><sg>[$][ ]+([\^]u<num><m><sp>[$])/^el<det><def><m><sg>$ \1/g"
