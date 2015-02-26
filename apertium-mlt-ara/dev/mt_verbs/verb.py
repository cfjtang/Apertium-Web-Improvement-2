#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, codecs, copy, time;
import classes;

sys.stdin  = codecs.getreader('utf-8')(sys.stdin);
sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

def sym(syms): #{
	return '<s n="' + syms.replace('.', '"/><s n="') + '"/>';
#}

def header(): #{
	header = '';
	header = header + '<dictionary>\n';
	header = header + '  <alphabet>ABĊDEFĠGGHĦIIJKLMNOPQRSTUVWXZŻabċdefġgħghieiejklmnopqrstuvwxzżycYCáéíóúàèìòùñöëäïüçÿāēīōūăĕĭŏŭãẽĩõũ</alphabet>\n';
	header = header + '  <sdefs>\n';
	header = header + '    <sdef n="vblex"/>\n';
	header = header + '    <sdef n="vaux"/>\n';
	header = header + '    <sdef n="prn"/>\n';
	header = header + '    <sdef n="tv"/>\n';
	header = header + '    <sdef n="iv"/>\n';
	header = header + '    <sdef n="TD"/>\n';
	header = header + '    <sdef n="past"/>\n';
	header = header + '    <sdef n="neg"/>\n';
	header = header + '    <sdef n="pres"/>\n';
	header = header + '    <sdef n="imp"/>\n';
	header = header + '    <sdef n="pprs"/>\n';
	header = header + '    <sdef n="pp"/>\n';
	header = header + '    <sdef n="p1"/>\n';
	header = header + '    <sdef n="p2"/>\n';
	header = header + '    <sdef n="p3"/>\n';
	header = header + '    <sdef n="sg"/>\n';
	header = header + '    <sdef n="pl"/>\n';
	header = header + '    <sdef n="m"/>\n';
	header = header + '    <sdef n="f"/>\n';
	header = header + '    <sdef n="mf"/>\n';
	header = header + '  </sdefs>\n';
	header = header + '  <pardefs>\n';


# negation
	header = header + '    <pardef n="S__qtalt/x">\n';
        header = header + '      <e><p><l>x</l><r><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';

# direct object
	# after consonant, -ek version
	header = header + '    <pardef n="S__qtalt/ni">\n';
        header = header + '      <e><p><l>u</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ha</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ek</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ni</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>hom</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>kom</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>na</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	#after consonant, -ok version
	header = header + '    <pardef n="S__xrobt/ni">\n';
        header = header + '      <e><p><l>u</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ha</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ok</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ni</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>hom</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>kom</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>na</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	#after vowel
	header = header + '    <pardef n="S__qtaltu/ni">\n';
        header = header + '      <e><p><l>h</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ha</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>k</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ni</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>hom</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>kom</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>na</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# consonant endings: fetaħ-ni, fetaħ-ha, fetaħ-kom/hom
	header = header + '    <pardef n="S__fetaħ/ni">\n';
        header = header + '      <e><p><l>ha</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ni</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>hom</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>kom</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>na</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# vowel endings: fetħ-ek, fetħ-u
	header = header + '    <pardef n="S__fetħ/ek">\n';
        header = header + '      <e><p><l>u</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ek</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# vowel endings: xorb-ok, xorb-u
	header = header + '    <pardef n="S__xorb/ok">\n';
        header = header + '      <e><p><l>u</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ok</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
	header = header + '    </pardef>\n';


# direct object + negation
	# after consonant, -ek version
	header = header + '    <pardef n="S__qtalt/nix">\n';
        header = header + '      <e><p><l>ux</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>hiex</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ekx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>nix</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>homx</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>komx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>niex</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after consonant, -ok version
	header = header + '    <pardef n="S__xrobt/nix">\n';
        header = header + '      <e><p><l>ux</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>hiex</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>okx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>nix</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>homx</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>komx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>niex</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after vowel
	header = header + '    <pardef n="S__qtaltu/nix">\n';
        header = header + '      <e><p><l>hx</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>hiex</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>kx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>nix</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>homx</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>komx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>niex</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# consonant endings: ma fetaħ-nix etc.
	header = header + '    <pardef n="S__fetaħ/nix">\n';
        header = header + '      <e><p><l>hiex</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>nix</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>homx</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>komx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>niex</l><r><j/>u<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# vowel endings: ma fetħ-ekx, ma fetħ-ux
	header = header + '    <pardef n="S__fetħ/ekx">\n';
        header = header + '      <e><p><l>ux</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ekx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# vowel endings: ma xorb-okx, ma xorb-ux
	header = header + '    <pardef n="S__xorb/okx">\n';
        header = header + '      <e><p><l>ux</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>okx</l><r><j/>u<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';


# indirect object
	# after vowel, -lek version
	header = header + '    <pardef n="S__qtaltu/lha">\n';
        header = header + '      <e><p><l>lu</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lha</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lek</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>li</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lhom</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>lkom</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>lna</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after vowel, -lok version
	header = header + '    <pardef n="S__xrobtu/lha">\n';
        header = header + '      <e><p><l>lu</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lha</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lok</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>li</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lhom</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>lkom</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>lna</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after consonant, -lek version
	header = header + '    <pardef n="S__qtalt/ilha">\n';
        header = header + '      <e><p><l>lu</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilha</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lek</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>li</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhom</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>ilkom</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>ilna</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after consonant, -lok version
	header = header + '    <pardef n="S__xrobt/ilha">\n';
        header = header + '      <e><p><l>lu</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilha</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lok</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>li</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhom</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>ilkom</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>ilna</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# fetaħli, fetaħlek, fetaħlu
	header = header + '    <pardef n="S__fetaħ/li">\n';
        header = header + '      <e><p><l>lu</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lek</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>li</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# xorobli, xoroblok, xoroblu
	header = header + '    <pardef n="S__xorob/li">\n';
        header = header + '      <e><p><l>lu</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>lok</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>li</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# fetħilha, fetħilna, fetħilkom, fetħilhom
	header = header + '    <pardef n="S__fetħ/ilha">\n';
        header = header + '      <e><p><l>ilha</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhom</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>ilkom</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/></r></p></e>\n';
        header = header + '      <e><p><l>ilna</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/></r></p></e>\n';
	header = header + '    </pardef>\n';

# indirect object + negation
	# after vowel, -lek version
	header = header + '    <pardef n="S__qtaltu/lhiex">\n';
        header = header + '      <e><p><l>lux</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lhiex</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lekx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lix</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lhomx</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lkomx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lniex</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after vowel, -lok version
	header = header + '    <pardef n="S__xrobtu/lhiex">\n';
        header = header + '      <e><p><l>lux</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lhiex</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lokx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lix</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lhomx</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lkomx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lniex</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after consonant, -lek version
	header = header + '    <pardef n="S__qtalt/ilhiex">\n';
        header = header + '      <e><p><l>lux</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhiex</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lekx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lix</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhomx</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilkomx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilniex</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# after consonant, -lok version
	header = header + '    <pardef n="S__xrobt/ilhiex">\n';
        header = header + '      <e><p><l>lux</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhiex</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lokx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lix</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhomx</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilkomx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilniex</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# fetaħlix, fetaħlekx, fetaħlux
	header = header + '    <pardef n="S__fetaħ/lix">\n';
        header = header + '      <e><p><l>lux</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lekx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lix</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# xoroblix, xoroblokx, xoroblux
	header = header + '    <pardef n="S__xorob/lix">\n';
        header = header + '      <e><p><l>lux</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lokx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>lix</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';
	# fetħilhiex etc.
	header = header + '    <pardef n="S__fetħ/ilhiex">\n';
        header = header + '      <e><p><l>ilhiex</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilhomx</l><r><j/>lu<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilkomx</l><r><j/>lu<s n="prn"/><s n="p2"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
        header = header + '      <e><p><l>ilniex</l><r><j/>lu<s n="prn"/><s n="p1"/><s n="mf"/><s n="pl"/><j/>x<s n="neg"/></r></p></e>\n';
	header = header + '    </pardef>\n';

# direct object + indirect object
	header = header + '    <pardef n="S__qtaltu/hielha">\n';
        header = header + '      <e><p><l>hu</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p><par n="S__qtaltu/lha"/></e>\n';
        header = header + '      <e><p><l>hie</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p><par n="S__qtaltu/lha"/></e>\n';
        header = header + '      <e><p><l>hom</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p><par n="S__xrobtu/lha"/></e>\n';  # homlkom, not homilkom; and homlok 
	header = header + '    </pardef>\n';


# direct object + indirect object + negation
	header = header + '    <pardef n="S__qtaltu/hielhiex">\n';
        header = header + '      <e><p><l>hu</l><r><j/>u<s n="prn"/><s n="p3"/><s n="m"/><s n="sg"/></r></p><par n="S__qtaltu/lhiex"/></e>\n';
        header = header + '      <e><p><l>hie</l><r><j/>u<s n="prn"/><s n="p3"/><s n="f"/><s n="sg"/></r></p><par n="S__qtaltu/lhiex"/></e>\n';
        header = header + '      <e><p><l>hom</l><r><j/>u<s n="prn"/><s n="p3"/><s n="mf"/><s n="pl"/></r></p><par n="S__xrobtu/lhiex"/></e>\n';
	header = header + '    </pardef>\n';



	header = header + '  </pardefs>\n\n';
	header = header + '  <section id="main" type="standard">\n';
    	header = header + '  <!-- Generated on: ' + time.strftime('%Y-%m-%d %H:%M %Z') + ' -->\n\n';
	

	return header;
	
#}
 
def footer(): #{
	footer = '';
	footer = footer + '  </section>\n';
	footer = footer + '</dictionary>\n';
	
	return footer;
#}


##-----------------------------------------------------------------------------##

#tv_with_pprs = [];

if len(sys.argv)>1:
    if '--help' in sys.argv:
        print "Usage: verbs.py semitic_stems_file irregular_stems_file loan_stems_file";
        sys.exit(1);


if len(sys.argv) == 4 :
	semstemsfile = sys.argv[1];
	irregstemsfile = sys.argv[2];
	loanstemsfile = sys.argv[3];
else :
	semstemsfile = sys.path[0] + '/stems-semitic.wiki';
	irregstemsfile = sys.path[0] + '/stems-irregular.wiki';
	loanstemsfile = sys.path[0] + '/stems-loan.wiki';


try:
	lines = file(semstemsfile);
except IOError as e:
        sys.stderr.write("Error reading file: {0}\n".format(semstemsfile));
        sys.exit(1);


stems = [];

for line in lines: #{

	line = line.strip(); 

	# '|-', '|}', headings, '{|'
	if len(line) < 3 or line[0] == '!' or line[0] == '{':
	        continue;
    
	line = line.lstrip('|');
	row = line.split('||'); 

	stems = stems + [{'stem': row[0].strip(), 'cat': row[1].strip(), 'type': row[2].strip(), 'theme': row[3].strip(), 'gloss': row[4].strip(), 'root': row[5].strip(), 'vowel_perf': row[6].strip(), 'vowel_impf': row[7].strip(), 'trans': row[8].strip(), 'pprs': row[9].strip(), 'pp': row[10].strip()}];

#}



try:
	lines = file(irregstemsfile);
except IOError as e:
        sys.stderr.write("Error reading file: {0}\n".format(irregstemsfile));
        sys.exit(1);


for line in lines: #{

	line = line.strip(); 

	# '|-', '|}', headings, '{|'
	if len(line) < 3 or line[0] == '!' or line[0] == '{':
	        continue;
    
	line = line.lstrip('|');
	row = line.split('||'); 

	stem = {'stem': row[0].strip(), 'cat': row[1].strip(), 'type': row[2].strip(), 'gloss': row[3].strip(), 'trans': row[4].strip()};
	stems = stems + [stem];

	if stem['type'] == 'auxiliary' :
		aux_verbs = aux_verbs + [stem['stem']];

#}



try:
	lines = file(loanstemsfile);
except IOError as e:
        sys.stderr.write("Error reading file: {0}\n".format(loanstemsfile));
        sys.exit(1);


for line in lines: #{

	line = line.strip(); 

	# '|-', '|}', headings, '{|', comments
	if len(line) < 3 or line[0] == '!' or line[0] == '{' or line[0] == '/' :
	        continue;
    
	line = line.lstrip('|');
	row = line.split('||'); 

	stems = stems + [{'stem': row[0].strip(), 'cat': row[1].strip(), 'type': row[2].strip(), 'subtype': row[3].strip(), 'gloss': row[4].strip(), 'base': row[5].strip(), 'ixx': row[6].strip(), 'vowel_impf': row[7].strip(), 'trans': row[8].strip(), 'pp': row[9].strip()}];

#}



##-----------------------------------------------------------------------------##

infl = {};
verb_cat = {};

for stem in stems: #{

    	try:
    	    stem_class = getattr(classes, stem['type']);
    	except AttributeError:
    	    sys.stderr.write("Error: Missing class '{0}'\n".format(stem['type']));
    	    sys.exit(1);

    	infl[stem['stem'] + '.' + stem['trans']] = stem_class.main(stem);
	verb_cat[stem['stem'] + '.' + stem['trans']] = stem['cat'];

#}


print header().decode('utf-8');

for stem in infl: #{

	s = stem.split('.');
	stem_stem = s[0];
	stem_trans = s[1];

	for flex in infl[stem]: #{
		for subflex in infl[stem][flex]: #{
			# restriction attribute, lm attribute, postgenerator tag:
			r, lm, a = '', '', ''	 
			if flex == 'past.p3.m.sg' and subflex[1] == '-':  
				lm = ' lm="%s"' % (stem_stem,)
			if subflex[2] == 'LR':
				r = ' r="%s"' % (subflex[2],)
				a = ''
			elif subflex[2] == 'RL':
				r = ' r="%s"' % (subflex[2],)
				a = '<a/>'

			left = subflex[0];
			right = '%s<s n="%s"/><s n="%s"/>%s' % (stem_stem, verb_cat[stem], stem_trans, sym(flex));
#			if stem in aux_verbs :
#				right = stem + '<s n="vaux"/>' + sym(flex);
#			else :
#				right = stem + '<s n="vblex"/>' + sym(flex);
			paradigm = subflex[1];

			if paradigm == "-": # no suffix
				entry  = '<e%s%s><p><l>%s%s</l><r>%s</r></p></e>' % (r, lm, a, left, right);
			else :  # suffix, in this case subflex[1] tells us which paradigm should be used
				entry = '<e%s><p><l>%s%s</l><r>%s</r></p><par n="%s"/></e>' % (r, a, left, right, paradigm);

			print entry.decode('utf-8')
			#}
		#}
	#}
#}

print footer();
