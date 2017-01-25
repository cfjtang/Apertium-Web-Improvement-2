#!/bin/bash

set -e -u

tests=(
    "Od.prp. nr. 41 1949 ja Od.prp. nr. 9 1951. Ja"
    "daid sáhte suddjet"
    "Ruvdnaprinseassa Märtha ja moai letne"
    "ja eanet ovddastanbargguid"
    "u-vearba Burton-upon-Trent ."
    "1 () 1998-99 Utjamningsmeldinga sátni."
    "1 () 1998-99 Utjamningsmeldinga sátni"
    "1300-."
    "1814 Vuođđolága."
    "(1998-99) Utjamningsmeldinga."
    "50.0 %."
    "50 %."
    "Anne Tornensis Hætta."
    "buohtastahttit eará gielaiguin."
    "čakčamánu 7.b. 1854"
    "Dalle ledjen juo barggus Romssa Sámeskuvllas."
    "Doai váldibeahtti dušše iežade biergasiid mielde."
    "eanan-"
    "eanan-."
    "eanan- ja luonddudoahpagiid"
    "eanan- ja luonddudoahpagiid."
    "El."
    "Fertebeahtti jáhkkigoahtit dakkár balddonasmuitalusaide."
    "ieš dat muitalii."
    "ieš mun dájun."
    "ieža moai dáppe váccašetne."
    "Ja de álggii muohttit ja šattai nu čoaskkis ahte galbmoje su biktasat."
    "juohke lágán geđggiiguin."
    "Jus in livčče mun ieš."
    "Karen Ellen Marie Siri Utsi čájeha gokko beana lei vuddjon."
    "Leago dát duođasge Sáme Radio oaivil?"
    "Leimmet muhtun oahppásat hálešteamen gaskaneamet oasi sámegillii oasi dárogillii."
    "Lei unnán mii earuhii nuorra Prinssa skuvlavázzima eará skuvlamánáin."
    "Máhtte ii dáidde jienastit Bargiidbellodaga doppe."
    "Máhttu lea ieš namma."
    "Mii háliidit diehtit dáhpáhuvvá go mihkkege, ja jos nu dagaš, makkár áigemeriid siste dán áššis."
    "min nuoramus olbmot."
    "Mis leat alddámet ain šibihat."
    "Moai válde dušše iežame biergasiid mielde."
    "Mot leaččai dasto mánáide dain leairrain?"
    "Mu bealis dat dušše nannejuvvui."
    "Mun ceggen vaikke ieš filbmafestivála."
    "munno."
    "NRK Radio oaivil."
    "ovddasvástideigga soai."
    "Ruvdnaprinseassa Märtha ja moai letne."
    "Sámi allaskuvla lea okta dain allaskuvllain."
    "Sámi allaskuvla lea okta dain allaskuvllain mii čavčča rájes doaibmá."
    "Smávva muorrakássat ledje beankan ja dasa vel lássin min iežamet niestebumbbát."
    "u-vearba Burton-upon-Trent"
    "Váldde biergasiid iežat fárrui."
)


declare -a TMPFILES
cleanup () {
    for f in "${TMPFILES[@]}"; do
        rm -f "$f"
    done
}
trap 'cleanup' EXIT


inp=$(mktemp -t knownhashtestvoc.XXXXXXXXXXX)
TMPFILES+=("${inp}")
res=$(mktemp -t knownhashtestvoc.XXXXXXXXXXX)
TMPFILES+=("${res}")

cd "$(dirname "$0")/../.."
printf "%s\n" "${tests[@]}" >"${inp}"
apertium -d . sme-nob < "${inp}" >"${res}"
bad=$(grep -c '#' "${res}")
if [[ ${bad} -gt 0 ]]; then
    echo "=== Still giving generation errors: ==="
    paste -d '\n' "${inp}" "${res}" /dev/null | grep -B1 '#'
    echo "=== Errors: ${bad}/${#tests[@]} ==="
else
    echo '=== All good! ==='
fi
