#!/usr/bin/perl
# Succinct copyright notice: GPL/Artistic

use warnings;
use strict;
use utf8;

use Data::Dumper;
use URI::Escape;

binmode STDIN, ":utf8";
binmode STDOUT, ":utf8";
binmode STDERR, ":utf8";

my $wikipage = "http://wiki.apertium.org/w/index.php?title=Special:Export&action=submit";

my %difficulty = (
	"1.&nbsp;Hard"       => "Hard",
	"2.&nbsp;Medium"     => "Medium",
	"3.&nbsp;Medium"     => "Medium",
	"2.&nbsp;Нормальное" => "Medium",
	"2.&nbsp;Easy"       => "Easy",
	"3.&nbsp;Easy"       => "Easy",
	"3.&nbsp;Лёгкое"     => "Easy",
);

my %areas = (
	"{{sc|code}}"           => "Code",
	"{{sc|код}}"            => "Code",
	"{{sc|outreach}}"       => "Outreach",
	"{{sc|quality}}"        => "Quality Assurance",
	"{{sc|research}}"       => "Research",
	"{{sc|исследование}}"   => "Research",
	"{{sc|documentation}}"  => "Documentation",
	"{{sc|translation}}"    => "Translation",
	"{{sc|user interface}}" => "User Interface",
	"{{sc|training}}"       => "Training",
);

my %who = (
	"[[User:Firespeaker|Firespeaker]]" => "jnw",
	"[[User:Francis Tyers|Francis Tyers]]" => "ftyers",
	"[[User:Francis Tyers|Francis&nbsp;Tyers]]" => "ftyers",
	"[[User:Jimregan|Jimregan]]" => "jimregan",
	"[[User:Hectoralos|Hèctor&nbsp;Alòs]]" => "hectoralos",
	"[[User:Jacob_Nordfalk|Jacob_Nordfalk]]" => "jacob_nordfalk",
	"[[User:mlforcada|Mikel&nbsp;L.&nbsp;Forcada]]" => "mlforcada",
	"[[User:mlforcada|Mikel L. Forcada]]" => "mlforcada",
	"[[User:Juanpabl|Juan Pablo Martínez]]" => "juanpabl",
	"[[User:AureiAnimus|Pim&nbsp;Otte]]" => "aureianimus",
	"[[User:Unhammer|Unhammer]]" => "unhammer",
	"[[User:Unhammer]]" => "unhammer",
	"[[User:Gramirez|Gramirez]]" => "gramirez",
);

my %lang = (
	"горномарийский"  => "Hill Mari",
	"горномарийского"  => "Hill Mari",
	"луговомарийский" => "Meadow Mari",
	"Луговомарийский" => "Meadow Mari",
	"луговомарийско"  => "Meadow Mari",
	"финско" => "Finnish",
	"финский" => "Finnish",
	"финского" => "Finnish",
	"русско" => "Russian",
	"русский" => "Russian",
	"Русский" => "Russian",
	"русского" => "Russian",
	"коми" => "Komi",
	"удмурстский" => "Udmurt",
	"Удмурский" => "Udmurt",
	"удмурский" => "Udmurt",
	"удмурстского" => "Udmurt",
	"мокша" => "Moksha",
	"якутский" => "Yakut",
	"англо" => "English",
	"Бурятский" => "Buryat",
	"бурятского" => "Buryat",
	"бурятско" => "Buryat",
	"эрзя" => "Erzya",
	"калмыцкий" => "Kalmyk",
	"тувинский" => "Tuvan",
	"Тувинский" => "Tuvan",
	"тувинского" => "Tuvan",
	"эсперанто" => "Esperanto",
	"Осетинский" => "Ossetian",
	"осетинского" => "Ossetian",
	"осетинско" => "Ossetian",
	"английский" => "English",
	"английского" => "English",
	"Кумыкский" => "Kumyk",
	"кумыкский" => "Kumyk",
	"кумыкского" => "Kumyk",
	"турецкий" => "Turkish",
	"турецский" => "Turkish",
	"турецкого" => "Turkish",
	"Карачаево-балкарский" => "Karachay-Balkar",
	"карачаево-балкарский" => "Karachay-Balkar",
	"хакасский" => "Khakas",
	"татарский" => "Tatar",
	"Татарский" => "Tatar",
	"татарского" => "Tatar",
	"татарско" => "Tatar",
	"Якутский" => "Sakha",
	"якутского" => "Sakha",
	"башкирский" => "Bashkir",
	"Башкирский" => "Bashkir",
	"башкирского" => "Bashkir",
	"башкирско" => "Bashkir",
	"чувашский" => "Chuvash",
	"Чувашский" => "Chuvash",
	"чувашского" => "Chuvash",
	"чувашско" => "Chuvash",
	"ногайский" => "Nogai",

	"is" => "Icelandic",
	"en" => "English",
	"nn" => "Nynorsk",
	"es" => "Spanish",
	"mk" => "Macedonian",
	"oc" => "Occitan",
	"ast" => "Asturian",
	"nb" => "Bokmål",
	"ca" => "Catalan",
	"bg" => "Bulgarian",

	"cze" => "Czech",
	"ces" => "Czech",
	"eng" => "English",
	"ell" => "Greek",
	"deu" => "German",
	"pol" => "Polish",
	"hrv" => "Croatian",
	"hin" => "Hindi",
	"dan" => "Danish",
	"jpn" => "Japanese",
	"rom" => "Romanian",
	"fra" => "French",
	"por" => "Portuguese",
	"ita" => "Italian",
	"nld" => "Dutch",
	"lat" => "Latin",
	"swe" => "Swedish",
	"swa" => "Swahili",
	"lit" => "Lithuanian",
	"ara" => "Arabic",
	"hun" => "Hungarian",
	"wel" => "Welsh",
	"cym" => "Welsh",
	"gle" => "Irish",
	"bre" => "Breton",
	"rus" => "Russian",
	"kur" => "Kurdish",
	"spa" => "Spanish",
	"kha" => "Khasi",
	"tur" => "Turkish",
	"scr" => "Serbo-Croatian",
	"swh" => "Swahili",
	"slo" => "Slovakian",
	"afr" => "Afrikaans",
	"gla" => "Scots Gaelic",
	"san" => "Sanskrit",
	"ckb" => "Central Kurdish",
	"kmr" => "Northern Kurdish",
);

my $tags; #global, as we're taking this from different places

sub dewikilink ($) {
	my $line = shift;

	my ($url, $text, $whole);

	if ($line =~ /\[\[([^\|\]]*)\]\]/) {
		$text = $1;
		my $tmp = $text;
		$tmp =~ s/ /_/g;
		$url = "http://wiki.apertium.org/wiki/$tmp";

		my $html = "<a href='$url'>$text</a>";
		$line =~ s/\[\[$text\]\]/$html/;
	}

	if ($line =~ /\[([^ ]*) ([^\]]*)\]/) {
		$url = $1;
		$text = $2;

		my $html = "<a href='$url'>$text</a>";
		$line =~ s/\[$url $text\]/$html/;
	}
	return $line;
}

sub fixtext {
	my $text = shift;
	my $out = $text;

	my $ltag;
	if ($text =~ /Создать ([^\-]*)-([^ ]*) словарь из ([0-9]*) слов на формате lttoolbox Апертиума./) {
		my $l1 = $1;
		my $l2 = $2;
		if (!$lang{$l1} || $lang{$l1} eq "") {
			print STDERR "Error: No mapping for language: $l1\n";
		} elsif (!$lang{$l2} || $lang{$l2} eq "") {
			print STDERR "Error: No mapping for language: $l2\n";
		} else {
			$out = "Create a $lang{$l1}-$lang{$l2} dictionary with $3 words in lttoolbox format.";
			$ltag = $lang{$l1};
			$ltag =~ s/ /_/g;
			$tags .= "$ltag, ";
			$ltag = $lang{$l2};
			#$ltag =~ s/ /_/g;
			$tags .= "$ltag, ";
		}		
	}

	if ($text =~ /Противопоставление: ([^ ]*)(?: язык)?(?:\s*)и ([^ ]*)(?: языки?)?/) {
		my $l1 = $1;
		my $l2 = $2;
		if (!$lang{$l1} || $lang{$l1} eq "") {
			print STDERR "Error: No mapping for language: $l1\n";
		} elsif (!$lang{$l2} || $lang{$l2} eq "") {
			print STDERR "Error: No mapping for language: $l2\n";
		} else {
			$out = "Contrastive analysis: $lang{$l1} and $lang{$l2}";
		}		
	}

	if ($text =~ /Создать множество тестовых фраз \(посмотрите (?:страницы )?'Pending tests' и 'Regression tests' в Вики\) для перевода с ([^ ]*)(?:\s*языка?)?(?:\s*)на ([^ ]*)(?:\s*языки?)?\./) {
		my $l1 = $1;
		my $l2 = $2;
		if (!$lang{$l1} || $lang{$l1} eq "") {
			print STDERR "Error: No mapping for language: $l1\n";
		} elsif (!$lang{$l2} || $lang{$l2} eq "") {
			print STDERR "Error: No mapping for language: $l2\n";
		} else {
			$out = "Create a set of test sentences (see the 'Pending tests' and 'Regression tests' pages on the wiki) for translation from $lang{$l1} to $lang{$l2}. ";
			$out .= "Tests should include as many features of the languages as possible. ";
			$out .= "Some examples may be found in grammar texts, while others may be devised. This task does not involve programming, only grammatical analysis.";
			$ltag = $lang{$l1};
			$ltag =~ s/ /_/g;
			$tags .= "$ltag, ";
			$ltag = $lang{$l2};
			#$ltag =~ s/ /_/g;
			$tags .= "$ltag, ";
		}		
	}

	if ($text =~ /Создать ([^\-]*)-([^ ]*) словарь/) {
		$out = "Create $lang{$1}-$lang{$2} dictionary";
	}

	if ($text =~ /Contrastive analysis: ([^ ]*) and (.*)$/) {
		$tags .= "$1, $2, ";
	}

	if ($text =~ /Convert existing resource: ([^ ]*) morphological analyser/) {
		$tags .= "$1, morphological analysis, script, ";
	}

	if ($text =~ /Convert existing resource: Reta Vortaro ([^\-]*)-(.*)/) {
		$tags .= "$1, $2, dictionary, script, ";
	}

	if ($text =~ /Create manually tagged corpora: (.*)/) {
		$tags .= "$1, corpus, PoS tagging, ";
	}

	if ($text =~ /Improve a language pair: ([^-]*)--?(.*)/) {
		$tags .= "$1, $2, mt, ";
	}

	if ($text =~ /Translate the Wikipedia article on Apertium: (.*)/) {
		$tags .= "$1, wikipedia, ";
	}

	if ($text =~ /Improve ([^-]*)--?([^ ]*) dictionaries/) {
		$tags .= "$1, $2, mt, dictionary, ";
	}

	if ($text =~ /([^-]*)--?([^ ]*) bilingual dictionary/) {
		$tags .= "$1, $2, mt, dictionary, ";
	}

	if ($text =~ /Contrastive analysis: ([^-]*)--?([^ ]*)/) {
		$tags .= "$1, $2, mt, ";
	}

	if ($text =~ /Quality evaluation: ([^ ]*) and ([^ ]*)/) {
		$tags .= "$1, $2, mt, ";
	}


	if ($text =~ /Apertium on ([^ ]*) Wikipedia/) {
		$tags .= "$1, wikipedia, ";
	}

	if ($text =~ /Convert Apertium resources: ([^-]*)--?([^ ]*) for Freedict/) {
		my $l1 = $1;
		my $l2 = $2;
		if (!$lang{$l1} || $lang{$l1} eq "") {
			print STDERR "Error: No mapping for language: $l1\n";
		} elsif (!$lang{$l2} || $lang{$l2} eq "") {
			print STDERR "Error: No mapping for language: $l2\n";
		} else {
			$tags .= "$lang{$l1}, $lang{$l2}, script, freedict, dictionary, ";
		}
	}

	if ($text =~ /Convert existing resource: FreeDict ([^-]*)--?([^ ]*)/) {
		my $l1 = $1;
		my $l2 = $2;
		if (!$lang{$l1} || $lang{$l1} eq "") {
			print STDERR "Error: No mapping for language: $l1\n";
		} elsif (!$lang{$l2} || $lang{$l2} eq "") {
			print STDERR "Error: No mapping for language: $l2\n";
		} else {
			$tags .= "$lang{$l1}, $lang{$l2}, script, freedict, dictionary, ";
		}
	}

	return $out;
}

sub simple_text_test {
	my $name = shift;
	my $in = shift;
	my $out = shift;
	my $func = shift;

	my $test = '';

	print "Testing: $name\n";
	for (my $i = 0; $i <= $#{$in}; $i++) {
		print "INFO: $i: ${$in}[$i]\n";
		$test = $func->(${$in}[$i]);
		if ($test ne ${$out}[$i]) { 
			print "FAIL: ${$in}[$i]\nExpected: ${$out}[$i]\nGot $test\n";
		} else {
			print "PASS\n";
		}
	}
}

sub test_dewikilink {
	my @in = (
		'You can use the utility [http://vikitraduko.saluton.dk:8080/vikitraduko/ Vikitradukilo].',
		'Take [http://www.sk-spell.sk.cx/mass-msas MSAS/MASS] and convert to lttoolbox format.',
		'Translate the [[new language pair HOWTO]] into Nynorsk.',
	);
	my @out = (
		"You can use the utility <a href='http://vikitraduko.saluton.dk:8080/vikitraduko/'>Vikitradukilo</a>.",
		"Take <a href='http://www.sk-spell.sk.cx/mass-msas'>MSAS/MASS</a> and convert to lttoolbox format.",
		"Translate the <a href='http://wiki.apertium.org/wiki/new_language_pair_HOWTO'>new language pair HOWTO</a> into Nynorsk.",
	);

	simple_text_test("dewikilink", \@in, \@out, \&dewikilink);
}

sub lrtrim {
	my $text = shift;
	$text =~ s/^\s*//;
	$text =~ s/\s*$//;
	return $text;
}

sub warning {
	my $out = shift;
	my $raw = shift;

	if (!$out || $out eq '') {
		print STDERR "WARNING: Extraction failed: $raw\n";
		print STDERR "Escaped: " . uri_escape($raw) . "\n";
	}
}

#test_dewikilink;

my $task_count = 0;

while (<>) {
	next if ($_ !~ /align=center/);
	$tags = '';
	s/align=center//g;
	my @pieces = split/ ?\|\| ?/;
	shift(@pieces) if ($pieces[0] eq '');

	my $area = $areas{lrtrim($pieces[0])};
	warning ($area, $pieces[0]);
	my $dif = $difficulty{lrtrim($pieces[1])};
	warning ($dif, $pieces[1]);
	my $title = fixtext(lrtrim($pieces[2]));
	my $text = fixtext(dewikilink(lrtrim($pieces[3])));
	
#	print "\"$area\", \"$dif\", \"$title\", \"$text\"\n";

	my $time;
	if ($pieces[4] =~ /(\d*)&ndash;(\d*)/) {
		$time = $1 * 24;
	} else {
		$time = "24";
	}

	my $ids = '';
	my @tmpid = ();

	for my $id (split/,/, $pieces[5]) {
		push @tmpid, $who{lrtrim($id)} if ($who{lrtrim($id)} && $who{lrtrim($id)} ne '');
	}
	$ids = join(", ", @tmpid);
	$ids = "jimregan" if ($ids eq '');

	$tags =~ s/, $//;


	my $output = "\"$title\",\"$text\",$time,";
	$output .= ($ids !~ /,/) ? "$ids" : "\"$ids\"";
	$output .= ',';
	$output .= ($dif !~ / /) ? "$dif" : "\"$dif\"";
	$output .= ",$area,";
	$output .= ($tags !~ /,/) ? "$tags" : "\"$tags\"";

	print "$output\n";
}
