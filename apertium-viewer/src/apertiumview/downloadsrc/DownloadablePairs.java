/*
 * Copyright (C) 2015 j
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package apertiumview.downloadsrc;

import apertiumview.ApertiumView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;
import org.apertium.Translator;

/**
 *
 * @author j
 */
public class DownloadablePairs {
	private final static String svnroot = "https://svn.code.sf.net/p/apertium/svn/";
	public final static String[] pairmodules = "trunk staging nursery incubator".split(" ");
	private final static String[] lmodules = "languages incubator".split(" ");
	private final static Preferences prefs = Preferences.userNodeForPackage(DownloadablePairs.class);

	private final static String prefs0
			= "trunk:af-nl br-fr ca-it cy-en dan-nor en-ca en-es en-gl eo-ca eo-en eo-es eo-fr es-an es-ast es-ca es-gl es-it es-pt es-ro eu-en eu-es fr-ca fr-es hbs-eng hbs-mkd hbs-slv id-ms is-sv isl-eng kaz-tat mk-bg mk-en mlt-ara nno-nob oc-ca oc-es pt-ca pt-gl sme-nob swe-dan urd-hin\n"
			+ "staging:cat-srd fra-por mt-he pl-cs tet-por\n"
			+ "nursery:bg-en ca-ro en-af en-pt eng-hin eo-fr eo-ru es-cs es-ssp fin-eng fo-is fr-it ga-gd hbs-rus hye-eng it-pt kaz-kir kaz-kum nld-fry nor-eng pl-uk quz-spa ro-it rus-ukr slv-spa sme-fin sme-sma sme-smj spa-qve tat-bak tat-rus tgk-pes tuk-tur tur-aze tur-kir tur-tat tur-uzb udm-rus\n"
			+ "incubator:ara-heb as-hi asm-ben asm-eng ava-rus bg-el bg-ru bn-en bn-hi br-cy br-es bua-khk cat-cos ces-ces ces-hbs ces-rus ces-slk chv-tat cs-sl cv-ru cv-tr cy-es de-en deu-ltz deu-nld deu-swe ell-eng en-de en-fr en-it en-lt en-lv en-mt en-nl en-pl en-sq eng-kaz eng-lvs eng-sco eng-tel eo-be eo-bg eo-br eo-cs eo-de eo-el eo-fa eo-fi eo-he eo-hu eo-it eo-ne eo-nl eo-pl eo-pt eo-sk eo-sv eo-tr es-de es-ia est-nor eu-fr eu-hu eus-fin eus-sme fao-dan fin-ces fin-est fin-gle fin-hbs fin-hun fin-isl fin-ita fin-spa fin-swe fin-udm fkv-fin fo-nb fr-nl fra-eng fra-ron ga-gv gle-eng guc-spa ht-en hun-eng isl-rus ita-ita ita-nor ita-srd kaz-kaa kaz-rus kaz-tyv kaz-uig khk-kaz kir-uzb kpv-fin kpv-mhr kv-ru ky-en la-en la-es la-it lat-eng liv-fin lt-lv lv-lg mal-eng mar-eng mar-hin mfe-en mk-sq mlt-spa mrj-fin myv-fin ne-en nog-kaz oc-fr olo-fin pes-glk pl-csb pl-dsb pl-eo pl-hsb pl-lt pl-lv pl-ru pl-sk pol-hbs ron-ina ron-rup ru-cu rus-eng sah-eng sc-pt si-en sjo-eng sl-mk slv-ita slv-pol sme-deu sme-smn sme-spa spa-pol sw-rn swe-dan swe-nor tat-kir tel-mar tgl-ceb tha-eng tr-en ur-fa ur-pa vi-en zho-spa zu-xh zul-ssw";

	public void refreshList() throws IOException {
		HashMap<String, Process> pp = new HashMap<>();
		for (String pm : pairmodules) pp.put(pm, new ProcessBuilder("svn", "ls", svnroot + pm).start());
		for (String pm : pairmodules) {
			StringBuilder alsb = new StringBuilder();
			BufferedReader std = new BufferedReader(new InputStreamReader(pp.get(pm).getInputStream(), "UTF-8"));
			String lin;
			while ((lin = std.readLine()) != null)
				if (lin.matches("apertium-\\p{Alpha}{2,3}-\\p{Alpha}{2,3}/"))
					alsb.append(lin.substring("apertium-".length(), lin.length() - 1)).append(" ");
			System.out.println(pm + " " + alsb);
			prefs.put(pm, alsb.toString().trim());
		}
	}

	public String getPairs(String module) {
		String pairs = prefs.get(module, null);
		if (pairs != null) return pairs;
			for (String l : prefs0.split("\n")) {
				String[] m = l.split(":");
				prefs.put(m[0], m[1]);
			}
		return prefs.get(module, "");
	}

	public static void main(String[] args) throws IOException {
		new DownloadablePairs().refreshList();
	}
}
