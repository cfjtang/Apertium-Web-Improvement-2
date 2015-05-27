/*
 * Copyright 2015 Jacob Nordfalk
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 */
package apertiumview.sourceeditor;

import apertiumview.ApertiumView;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apertium.pipeline.Mode;
import org.apertium.pipeline.Program;
import static org.apertium.pipeline.Program.ProgEnum.INTERCHUNK;
import static org.apertium.pipeline.Program.ProgEnum.TRANSFER;
import org.apertium.utils.IOUtils;

/**
 *
 * @author j
 */
public class SourcecodeFinder {


	public static String findTransferInterchunkFile(Program program) {
		if (program.getProgram() == TRANSFER || program.getProgram()==INTERCHUNK) {
			for (String param : program.getParameters().split(" ")) {
				if (param.length()<10) continue; // strip parameters
				if (param.endsWith(".bin")) continue;
				if (param.endsWith(".class")) continue; // only t1x, antaux_t2x, t2x, t3x
				if (new File(param).exists()) {
					return param;
				}
				try {
					IOUtils.openInFileStream(param);
					return param;
				} catch (Exception e) {}
			}
		}
		return null;
	}

	private static final Pattern rulePatternTransferInterchunk = Pattern.compile(": Rule ([0-9]+) ");

	public static String createHtmlErr(Program program, String err) {
		if (err.length()==0) return err;
		String link = findTransferInterchunkFile(program);
		StringBuffer text = new StringBuffer(err.length()*3);
		text.append("<html><div style='white-space:nowrap;font-size:12pt;color:#990000'>");
		err = err.replaceAll("\n", "<br>\n");
		if (link == null) {
			text.append(err);
		} else {
			// We found an t1x, antaux_t2x, t2x, t3x file :-)
			// Make links in texts like: apertium-transfer: Rule 24 bruna vosto
			Matcher m = rulePatternTransferInterchunk.matcher(err);
			while (m.find()) {
				int ruleNo = Integer.parseInt(m.group(1));
				m.appendReplacement(text, "");
				text.append(": <a href='file:///").append(link)
						.append("?program=").append(program.getProgram())
						.append("&dir=").append(new File(link).getParent())
						.append("&findTag=rule&findTagNo=").append(ruleNo-1)
						.append("'>Rule ").append(ruleNo).append("</a> ");
			}
			m.appendTail(text);
			text.append(" &nbsp;&nbsp;&nbsp;<a  style='color:#a0a0a0;text-decoration:none' href='file://"+ApertiumView.DISABLE_traceTransferInterchunk+"'>[X]</a></span>");
		}
		text.append("</div>");
		return text.toString();
	}

	public static String createHtmlLinkText(Program program, StringBuilder sourceFiles) {
		StringBuilder text = new StringBuilder();
//        commandTextPane.setText("<html><div style='white-space:nowrap;font-size:12pt'><a href='http://javabog.dk'>hej</a> "+program.toString());

		switch (program.getProgram()) {
			case UNKNOWN: // Might be constraint grammar
			case LT_PROC:
			case TRANSFER:
			case INTERCHUNK:
			case POSTCHUNK:
				for (String param : program.getParameters().split(" ")) {
					File paramFile = new File(param);
					String link = null;
					if (paramFile.exists()) { // strip parameters and skip nonexistant files
						ArrayList<Path> sourcedirs = getSourcedirs(paramFile.getParent());
						if (!sourcedirs.isEmpty()) { // only make links if we can find a source dir (with a Makefile)
							if (param.endsWith(".bin")) {
								// its a binary file....
								link = findSourcefileFromBinaryFile(paramFile, sourcedirs);
								if (link != null) sourceFiles.append(link).append('\n');
							} else if (param.endsWith(".hfst")) {
								link = findSourcefileFromBinaryFile(paramFile, sourcedirs);
								if (link != null) sourceFiles.append(link).append('\n');
							} else if (param.endsWith(".prob")) {
								// no link
							} else { // t1x, antaux_t2x, t2x, t3x,
								link = paramFile.getPath();
								sourceFiles.append(link).append('\n');
							}
						}
						//System.out.println(program.getProgram() + " " + param + " -> " + link);
						text.append(" <font color='#aaaaaa'>").append(paramFile.getParent()).append("/</font>"); // grey path
						if (link!=null) text.append("<a href='file:///").append(link)
								.append("?program=").append(program.getProgram())
								.append("&dir=").append(paramFile.getParent())
								.append("'>").append(paramFile.getName()).append("</a>");
						else text.append(paramFile.getName());
					} else {
						text.append(" ").append(param);
					}
				}
		}

		return "<html><div style='white-space:nowrap;font-size:12pt'>" + program.getCommandName() + text + "</div>";
	}


	private static String match;
	private static boolean match(Path dir, String glob) {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, glob)) {
			Iterator<Path> i = ds.iterator();
			while (i.hasNext()) {
				match = i.next().toString();
				// avoid files with names like _tmp or ~ in them
				if (match.contains("~") || match.contains("tmp")) continue;
				return true;
			}
		} catch (Exception e) { e.printStackTrace(); }
		return false;
	}


	private static boolean match(ArrayList<Path> sourcedirs, String string) {
		for (Path p : sourcedirs) if (match(p, string)) return true;
		return false;
	}

	private static String findSourcefileFromBinaryFile(File bin, ArrayList<Path> sourcedirs) {
		try {
			// /home/j/esperanto/apertium/trunk/apertium-eo-en/en-eo.automorf.bin
			// -> apertium-eo-en.en.dix
			String[] parts = bin.getName().split("\\."); // "en-eo", "automorf", "bin"
			String dirname = bin.getParentFile().getName(); // "apertium-eo-en"

			if ("automorf".equals(parts[1])) { // source language .dix
				// en-eo.automorf.bin -> apertium-eo-en.en.dix
				String sl = parts[0].split("-")[0]; // "en"
				if (match(sourcedirs, dirname+"."+sl+".dix")) return match;
				// eng-sco.automorf.bin -> apertium-eng.eng.dix
				if (match(sourcedirs, "apertium-"+sl+"."+sl+".dix")) return match;
				// hfst files:
				// apertium-$lang.$lang.lexc – the lexicon and pardefs
				// apertium-$lang.$lang.twol – morphophonological rules (like "turn -y into -ie before -s")
				if (match(sourcedirs, "apertium-"+sl+"."+sl+".lexc")) return match;
				// also return this? if (match(sourcedirs, "apertium-"+sl+"."+sl+".twol")) return match;

				// No match - try some more desperate options
				if (match(sourcedirs, dirname+"."+sl+".dix*")) return match;
				if (match(sourcedirs, "apertium-"+sl+"."+sl+".dix*")) return match;
				if (match(sourcedirs, dirname+"."+sl+".*dix*")) return match;
				if (match(sourcedirs, "apertium-"+sl+"."+sl+".*dix*")) return match;
			}
			else if ("rlx".equals(parts[1])) { // source language .rlx (constraint grammar)
				// en-eo.automorf.bin -> apertium-eo-en.en.dix
				String sl = parts[0].split("-")[0]; // "en"
				if (match(sourcedirs, dirname+"."+sl+".rlx")) return match;
				 // eng-sco.automorf.bin -> apertium-eng.eng.dix
				if (match(sourcedirs, "apertium-"+sl+"."+sl+".rlx")) return match;
			}
			else if ("autobil".equals(parts[1])) { // bilingual .dix
				// eng-sco.autobil.bin, sco-eng.autobil.bin -> apertium-eng-sco.eng-sco.dix
				String sl_tl = parts[0]; // "eng-sco"
				if (match(sourcedirs, dirname+"."+sl_tl+".dix")) return match;
				String[] sltla = sl_tl.split("-"); // "eng-sco"
				String tl_sl = sltla[1]+"-"+sltla[0]; // "sco-eng"
				if (match(sourcedirs, dirname+"."+tl_sl+".dix")) return match;
				// No match - try some more desperate options
				if (match(sourcedirs, dirname+"."+sl_tl+".dix*")) return match;
				if (match(sourcedirs, dirname+"."+tl_sl+".dix*")) return match;
				if (match(sourcedirs, dirname+"."+sl_tl+".*dix*")) return match;
				if (match(sourcedirs, dirname+"."+tl_sl+".*dix*")) return match;
			}
			else if ("autogen".equals(parts[1])) { // target language .dix
				// en-eo.autogen.bin -> apertium-eo-en.eo.dix
				String tl = parts[0].split("-")[1]; // "eo"
				if (match(sourcedirs, dirname+"."+tl+".dix")) return match;
				 // eng-sco.automorf.bin -> apertium-sco.sco.dix
				if (match(sourcedirs, "apertium-"+tl+"."+tl+".dix")) return match;
				// hfst files:
				// apertium-$lang.$lang.lexc – the lexicon and pardefs
				// apertium-$lang.$lang.twol – morphophonological rules (like "turn -y into -ie before -s")
				if (match(sourcedirs, "apertium-"+tl+"."+tl+".lexc")) return match;

				// No match - try some more desperate options
				if (match(sourcedirs, dirname+"."+tl+".dix*")) return match;
				if (match(sourcedirs, "apertium-"+tl+"."+tl+".dix*")) return match;
				if (match(sourcedirs, dirname+"."+tl+".*dix*")) return match;
				if (match(sourcedirs, "apertium-"+tl+"."+tl+".*dix*")) return match;
			}



			/*
			 make -C ../apertium-sco && make
			 AP_SUBDIRS =  ../apertium-eng_feil ../apertium-sco
			 */
			String[][] ioExamples = new String[][] {
				//			{"apertium-dan-nor/nno-dan.automorf.bin", "../../languages/apertium-nno/apertium-nno.nno.dix"},
				//			{"apertium-dan-nor/nno-dan.rlx.bin", "../../languages/apertium-nno/apertium-nno.nno.rlx", },
				{ "apertium-dan-nor/nor-dan.autobil.bin", "apertium-dan-nor.dan-nor.dix", },
				//			{"apertium-dan-nor/nor-dan.t1x.bin", "apertium-dan-nor.dan-nob.t1x"},
				{ "apertium-dan-nor/nor-dan.autogen.bin", "../../languages/apertium-dan/apertium-dan.dan.dix" }, };
			return null;
		} catch (Exception ex) {
			System.out.println("Error for "+bin.getName());
			ex.printStackTrace();
			return null;
		}
	}


	public static void main(String[] args) throws Exception {
		Mode m = new Mode("/home/j/esperanto/apertium/trunk/apertium-eo-en/modes/en-eo.mode");
		for (int i = 0; i<m.getPipelineLength(); i++) {
			Program p = m.getProgramByIndex(i);
			String html = createHtmlLinkText(p, new StringBuilder());
			System.out.println(p + "\n -> " + html);
		}
	}

	public static void xmain(String[] args) throws Exception {
		Mode m = new Mode("/home/j/esperanto/apertium/ENG-SCO/apertium-eng-sco/modes/eng-sco.mode");
		for (int i = 0; i<m.getPipelineLength(); i++) {
			Program p = m.getProgramByIndex(i);
			String html = createHtmlLinkText(p, new StringBuilder());
			System.out.println(p + "\n -> " + html);
		}
	}

	private static HashMap<String, ArrayList<Path>> dirToSourcedirs = new HashMap<>();
	private static ArrayList<Path> findSourcedirsFromMakefile(String binDir) {
		ArrayList<Path> sourcedirs = new ArrayList<>();
		Path makefile = Paths.get(binDir, "Makefile");
		if (Files.exists(makefile)) {
			sourcedirs.add(Paths.get(binDir)); // There's a makefile, so its a source file directory
			try {
				// Read makefile and see if theres something like AP_SUBDIRS with dependent dirs, like
				// AP_SUBDIRS =  ../apertium-eng_feil ../apertium-sco
				for (String line : Files.readAllLines(makefile, Charset.defaultCharset())) {
					if (line.trim().startsWith("AP_SUBDIRS")) {
						System.out.println("sourcedirs " + line);
						for (String dd : line.split("=")[1].split(" +")) {
							if (dd.isEmpty()) continue;
							//System.out.println("sourcedir " + dd + " -> "+ Paths.get(binDir, dd).normalize().toString());
							sourcedirs.add(Paths.get(binDir, dd).normalize());
						}
					}
				}
			} catch (Exception e) { e.printStackTrace(); }
		}
		//System.out.println("sourcedirs for " + binDir + " are: " + sourcedirs);
		return sourcedirs;
	}

	public static ArrayList<Path> getSourcedirs(String binDir) {
		ArrayList<Path> sourcedirs = dirToSourcedirs.get(binDir);
		if (sourcedirs == null) dirToSourcedirs.put(binDir, sourcedirs = findSourcedirsFromMakefile(binDir));
		return sourcedirs;
	}

}
