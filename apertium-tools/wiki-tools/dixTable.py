#!/usr/bin/env python3

import sys, os, argparse, itertools, re, subprocess, urllib.request, logging, collections, textwrap
import xml.etree.ElementTree as etree

iso639Codes = {'roh': 'rm', 'gv': 'glv', 'gu': 'guj', 'ron': 'ro', 'oss': 'os', 'gd': 'gla', 'nld': 'nl', 'ga': 'gle', 'se': 'sme', 'gl': 'glg', 'oji': 'oj', 'oci': 'oc', 'ty': 'tah', 'jav': 'jv', 'tw': 'twi', 'tt': 'tat', 'hrv': 'hr', 'tr': 'tur', 'ts': 'tso', 'tn': 'tsn', 'to': 'ton', 'tl': 'tgl', 'tk': 'tuk', 'th': 'tha', 'ti': 'tir', 'ven': 've', 'tg': 'tgk', 'te': 'tel', 'ta': 'tam', 'fas': 'fa', 'ssw': 'ss', 'de': 'deu', 'da': 'dan', 'ay': 'aym', 'dz': 'dzo', 'fao': 'fo', 'dv': 'div', 'rn': 'run', 'hin': 'hi', 'qu': 'que', 'hye': 'hy', 'guj': 'gu', 'kua': 'kj', 'cre': 'cr', 'div': 'dv', 'bam': 'bm', 'bak': 'ba', 'tel': 'te', 'mi': 'mri', 'za': 'zha', 'mh': 'mah', 'ara': 'ar', 'ce': 'che', 'nbl': 'nr', 'zu': 'zul', 'wa': 'wln', 'sun': 'su', 'abk': 'ab', 'kur': 'ku', 'wol': 'wo', 'lub': 'lu', 'gn': 'grn', 'lug': 'lg', 'jv': 'jav', 'nep': 'ne', 'ms': 'msa', 'iku': 'iu', 'lg': 'lug', 'wo': 'wol', 'tur': 'tr', 'mr': 'mar', 'tuk': 'tk', 'ja': 'jpn', 'cos': 'co', 'ile': 'ie', 'gla': 'gd', 'bos': 'bs', 'gle': 'ga', 'glg': 'gl', 'aka': 'ak', 'bod': 'bo', 'glv': 'gv', 'aa': 'aar', 'ch': 'cha', 'co': 'cos', 'vie': 'vi', 'ipk': 'ik', 'ca': 'cat', 'bs': 'bos', 'por': 'pt', 'uzb': 'uz', 'na': 'nau', 'pol': 'pl', 'cs': 'ces', 'tgk': 'tg', 'bre': 'br', 'cv': 'chv', 'tgl': 'tl', 'aym': 'ay', 'cha': 'ch', 'fra': 'fr', 'che': 'ce', 'pt': 'por', 'swa': 'sw', 'twi': 'tw', 'swe': 'sv', 'pa': 'pan', 'chu': 'cu', 'chv': 'cv', 'vi': 'vie', 'fry': 'fy', 'pi': 'pli', 'msa': 'ms', 'am': 'amh', 'hmo': 'ho', 'iii': 'ii', 'ml': 'mal', 'mg': 'mlg', 'mlg': 'mg', 'ibo': 'ig', 'hat': 'ht', 'slv': 'sl', 'mn': 'mon', 'xho': 'xh', 'deu': 'de', 'mk': 'mkd', 'cat': 'ca', 'mt': 'mlt', 'mlt': 'mt', 'slk': 'sk', 'ful': 'ff', 'my': 'mya', 'tat': 'tt', 've': 'ven', 'jpn': 'ja', 'vol': 'vo', 'oc': 'oci', 'is': 'isl', 'iu': 'iku', 'it': 'ita', 'vo': 'vol', 'ii': 'iii', 'mya': 'my', 'ik': 'ipk', 'io': 'ido', 'spa': 'es', 'ia': 'ina', 'ave': 'ae', 'tah': 'ty', 'ava': 'av', 'ig': 'ibo', 'yo': 'yor', 'eng': 'en', 'ie': 'ile', 'ewe': 'ee', 'id': 'ind', 'nya': 'ny', 'sin': 'si', 'pan': 'pa', 'snd': 'sd', 'mar': 'mr', 'sna': 'sn', 'kir': 'ky', 'kik': 'ki', 'fa': 'fas', 'kin': 'rw', 'ff': 'ful', 'lat': 'la', 'mah': 'mh', 'lav': 'lv', 'mal': 'ml', 'fo': 'fao', 'ss': 'ssw', 'sr': 'srp', 'sq': 'sqi', 'sw': 'swa', 'sv': 'swe', 'su': 'sun', 'st': 'sot', 'sk': 'slk', 'epo': 'eo', 'si': 'sin', 'so': 'som', 'sn': 'sna', 'sm': 'smo', 'sl': 'slv', 'sc': 'srd', 'sa': 'san', 'ido': 'io', 'sg': 'sag', 'nb': 'nob', 'tha': 'th', 'sd': 'snd', 'ita': 'it', 'tsn': 'tn', 'tso': 'ts', 'lb': 'ltz', 'ell': 'el', 'la': 'lat', 'ln': 'lin', 'lo': 'lao', 'li': 'lim', 'lv': 'lav', 'lt': 'lit', 'lu': 'lub', 'fij': 'fj', 'fin': 'fi', 'hau': 'ha', 'eus': 'eu', 'yi': 'yid', 'amh': 'am', 'bih': 'bh', 'dan': 'da', 'nob': 'nb', 'ces': 'cs', 'mon': 'mn', 'bis': 'bi', 'nor': 'no', 'cy': 'cym', 'afr': 'af', 'el': 'ell', 'eo': 'epo', 'en': 'eng', 'ee': 'ewe', 'fr': 'fra', 'lao': 'lo', 'cr': 'cre', 'eu': 'eus', 'et': 'est', 'es': 'spa', 'ru': 'rus', 'est': 'et', 'smo': 'sm', 'cu': 'chu', 'fy': 'fry', 'rm': 'roh', 'sme': 'se', 'ro': 'ron', 'be': 'bel', 'bg': 'bul', 'run': 'rn', 'ba': 'bak', 'ps': 'pus', 'bm': 'bam', 'bn': 'ben', 'bo': 'bod', 'bh': 'bih', 'bi': 'bis', 'orm': 'om', 'que': 'qu', 'br': 'bre', 'ori': 'or', 'rus': 'ru', 'pli': 'pi', 'pus': 'ps', 'om': 'orm', 'oj': 'oji', 'srd': 'sc', 'ltz': 'lb', 'nde': 'nd', 'dzo': 'dz', 'ndo': 'ng', 'srp': 'sr', 'wln': 'wa', 'isl': 'is', 'os': 'oss', 'or': 'ori', 'zul': 'zu', 'xh': 'xho', 'som': 'so', 'sot': 'st', 'fi': 'fin', 'zh': 'zho', 'fj': 'fij', 'yid': 'yi', 'mkd': 'mk', 'kom': 'kv', 'her': 'hz', 'kon': 'kg', 'ukr': 'uk', 'ton': 'to', 'heb': 'he', 'kor': 'ko', 'hz': 'her', 'hy': 'hye', 'hr': 'hrv', 'hun': 'hu', 'ht': 'hat', 'hu': 'hun', 'hi': 'hin', 'ho': 'hmo', 'bul': 'bg', 'ha': 'hau', 'cym': 'cy', 'he': 'heb', 'ben': 'bn', 'bel': 'be', 'uz': 'uzb', 'azb': 'az', 'aze': 'az', 'ur': 'urd', 'zha': 'za', 'pl': 'pol', 'uk': 'ukr', 'aar': 'aa', 'ug': 'uig', 'zho': 'zh', 'nno': 'nn', 'ab': 'abk', 'ae': 'ave', 'san': 'sa', 'uig': 'ug', 'af': 'afr', 'ak': 'aka', 'arg': 'an', 'sag': 'sg', 'an': 'arg', 'as': 'asm', 'ar': 'ara', 'khm': 'km', 'av': 'ava', 'ind': 'id', 'az': 'aze', 'ina': 'ia', 'asm': 'as', 'nl': 'nld', 'nn': 'nno', 'no': 'nor', 'lim': 'li', 'lin': 'ln', 'nd': 'nde', 'ne': 'nep', 'tir': 'ti', 'ng': 'ndo', 'lit': 'lt', 'ny': 'nya', 'nav': 'nv', 'nau': 'na', 'grn': 'gn', 'nr': 'nbl', 'yor': 'yo', 'nv': 'nav', 'kv': 'kom', 'tam': 'ta', 'cor': 'kw', 'kan': 'kn', 'kal': 'kl', 'kas': 'ks', 'sqi': 'sq', 'rw': 'kin', 'kau': 'kr', 'kat': 'ka', 'kaz': 'kk', 'urd': 'ur', 'ka': 'kat', 'kg': 'kon', 'kk': 'kaz', 'kj': 'kua', 'ki': 'kik', 'ko': 'kor', 'kn': 'kan', 'km': 'khm', 'kl': 'kal', 'ks': 'kas', 'kr': 'kau', 'kw': 'cor', 'mri': 'mi', 'ku': 'kur', 'ky': 'kir', 'hbs': 'sh', 'sh': 'hbs'}

englishLangNames = {"aa":"Afar","ab":"Abkhazian","ace":"Achinese","ach":"Acoli","ada":"Adangme","ady":"Adyghe","ae":"Avestan","af":"Afrikaans","afh":"Afrihili","agq":"Aghem","ain":"Ainu","ak":"Akan","akk":"Akkadian","ale":"Aleut","alt":"Southern Altai","am":"Amharic","an":"Aragonese","ang":"Old English","anp":"Angika","ar":"Arabic","ar-001":"Modern Standard Arabic","arc":"Aramaic","arn":"Mapuche","arp":"Arapaho","arw":"Arawak","as":"Assamese","asa":"Asu","ast":"Asturian","av":"Avaric","awa":"Awadhi","ay":"Aymara","az":"Azerbaijani","az-alt-short":"Azeri","ba":"Bashkir","bal":"Baluchi","ban":"Balinese","bas":"Basaa","bax":"Bamun","bbj":"Ghomala","be":"Belarusian","bej":"Beja","bem":"Bemba","bez":"Bena","bfd":"Bafut","bg":"Bulgarian","bho":"Bhojpuri","bi":"Bislama","bik":"Bikol","bin":"Bini","bkm":"Kom","bla":"Siksika","bm":"Bambara","bn":"Bengali","bo":"Tibetan","br":"Breton","bra":"Braj","brx":"Bodo","bs":"Bosnian","bss":"Akoose","bua":"Buriat","bug":"Buginese","bum":"Bulu","byn":"Blin","byv":"Medumba","ca":"Catalan","cad":"Caddo","car":"Carib","cay":"Cayuga","cch":"Atsam","ce":"Chechen","ceb":"Cebuano","cgg":"Chiga","ch":"Chamorro","chb":"Chibcha","chg":"Chagatai","chk":"Chuukese","chm":"Mari","chn":"Chinook Jargon","cho":"Choctaw","chp":"Chipewyan","chr":"Cherokee","chy":"Cheyenne","ckb":"Sorani Kurdish","co":"Corsican","cop":"Coptic","cr":"Cree","crh":"Crimean Turkish","cs":"Czech","csb":"Kashubian","cu":"Church Slavic","cv":"Chuvash","cy":"Welsh","da":"Danish","dak":"Dakota","dar":"Dargwa","dav":"Taita","de":"German","de-AT":"Austrian German","de-CH":"Swiss High German","del":"Delaware","den":"Slave","dgr":"Dogrib","din":"Dinka","dje":"Zarma","doi":"Dogri","dsb":"Lower Sorbian","dua":"Duala","dum":"Middle Dutch","dv":"Divehi","dyo":"Jola-Fonyi","dyu":"Dyula","dz":"Dzongkha","dzg":"Dazaga","ebu":"Embu","ee":"Ewe","efi":"Efik","egy":"Ancient Egyptian","eka":"Ekajuk","el":"Greek","elx":"Elamite","en":"English","en-AU":"Australian English","en-CA":"Canadian English","en-GB":"British English","en-GB-alt-short":"U.K. English","en-US":"American English","en-US-alt-short":"U.S. English","enm":"Middle English","eo":"Esperanto","es":"Spanish","es-419":"Latin American Spanish","es-ES":"European Spanish","es-MX":"Mexican Spanish","et":"Estonian","eu":"Basque","ewo":"Ewondo","fa":"Persian","fan":"Fang","fat":"Fanti","ff":"Fulah","fi":"Finnish","fil":"Filipino","fj":"Fijian","fo":"Faroese","fon":"Fon","fr":"French","fr-CA":"Canadian French","fr-CH":"Swiss French","frm":"Middle French","fro":"Old French","frr":"Northern Frisian","frs":"Eastern Frisian","fur":"Friulian","fy":"Western Frisian","ga":"Irish","gaa":"Ga","gay":"Gayo","gba":"Gbaya","gd":"Scottish Gaelic","gez":"Geez","gil":"Gilbertese","gl":"Galician","gmh":"Middle High German","gn":"Guarani","goh":"Old High German","gon":"Gondi","gor":"Gorontalo","got":"Gothic","grb":"Grebo","grc":"Ancient Greek","gsw":"Swiss German","gu":"Gujarati","guz":"Gusii","gv":"Manx","gwi":"Gwichʼin","ha":"Hausa","hai":"Haida","haw":"Hawaiian","he":"Hebrew","hi":"Hindi","hil":"Hiligaynon","hit":"Hittite","hmn":"Hmong","ho":"Hiri Motu","hr":"Croatian","hsb":"Upper Sorbian","ht":"Haitian","hu":"Hungarian","hup":"Hupa","hy":"Armenian","hz":"Herero","ia":"Interlingua","iba":"Iban","ibb":"Ibibio","id":"Indonesian","ie":"Interlingue","ig":"Igbo","ii":"Sichuan Yi","ik":"Inupiaq","ilo":"Iloko","inh":"Ingush","io":"Ido","is":"Icelandic","it":"Italian","iu":"Inuktitut","ja":"Japanese","jbo":"Lojban","jgo":"Ngomba","jmc":"Machame","jpr":"Judeo-Persian","jrb":"Judeo-Arabic","jv":"Javanese","ka":"Georgian","kaa":"Kara-Kalpak","kab":"Kabyle","kac":"Kachin","kaj":"Jju","kam":"Kamba","kaw":"Kawi","kbd":"Kabardian","kbl":"Kanembu","kcg":"Tyap","kde":"Makonde","kea":"Kabuverdianu","kfo":"Koro","kg":"Kongo","kha":"Khasi","kho":"Khotanese","khq":"Koyra Chiini","ki":"Kikuyu","kj":"Kuanyama","kk":"Kazakh","kkj":"Kako","kl":"Kalaallisut","kln":"Kalenjin","km":"Khmer","kmb":"Kimbundu","kn":"Kannada","ko":"Korean","kok":"Konkani","kos":"Kosraean","kpe":"Kpelle","kr":"Kanuri","krc":"Karachay-Balkar","krl":"Karelian","kru":"Kurukh","ks":"Kashmiri","ksb":"Shambala","ksf":"Bafia","ksh":"Colognian","ku":"Kurdish","kum":"Kumyk","kut":"Kutenai","kv":"Komi","kw":"Cornish","ky":"Kyrgyz","ky-alt-variant":"Kirghiz","la":"Latin","lad":"Ladino","lag":"Langi","lah":"Lahnda","lam":"Lamba","lb":"Luxembourgish","lez":"Lezghian","lg":"Ganda","li":"Limburgish","lkt":"Lakota","ln":"Lingala","lo":"Lao","lol":"Mongo","loz":"Lozi","lt":"Lithuanian","lu":"Luba-Katanga","lua":"Luba-Lulua","lui":"Luiseno","lun":"Lunda","luo":"Luo","lus":"Mizo","luy":"Luyia","lv":"Latvian","mad":"Madurese","maf":"Mafa","mag":"Magahi","mai":"Maithili","mak":"Makasar","man":"Mandingo","mas":"Masai","mde":"Maba","mdf":"Moksha","mdr":"Mandar","men":"Mende","mer":"Meru","mfe":"Morisyen","mg":"Malagasy","mga":"Middle Irish","mgh":"Makhuwa-Meetto","mgo":"Meta'","mh":"Marshallese","mi":"Maori","mic":"Micmac","min":"Minangkabau","mk":"Macedonian","ml":"Malayalam","mn":"Mongolian","mnc":"Manchu","mni":"Manipuri","moh":"Mohawk","mos":"Mossi","mr":"Marathi","ms":"Malay","mt":"Maltese","mua":"Mundang","mul":"Multiple Languages","mus":"Creek","mwl":"Mirandese","mwr":"Marwari","my":"Burmese","mye":"Myene","myv":"Erzya","na":"Nauru","nap":"Neapolitan","naq":"Nama","nb":"Norwegian Bokmål","nd":"North Ndebele","nds":"Low German","ne":"Nepali","new":"Newari","ng":"Ndonga","nia":"Nias","niu":"Niuean","nl":"Dutch","nl-BE":"Flemish","nmg":"Kwasio","nn":"Norwegian Nynorsk","nnh":"Ngiemboon","no":"Norwegian","nog":"Nogai","non":"Old Norse","nqo":"N’Ko","nr":"South Ndebele","nso":"Northern Sotho","nus":"Nuer","nv":"Navajo","nwc":"Classical Newari","ny":"Nyanja","nym":"Nyamwezi","nyn":"Nyankole","nyo":"Nyoro","nzi":"Nzima","oc":"Occitan","oj":"Ojibwa","om":"Oromo","or":"Oriya","os":"Ossetic","osa":"Osage","ota":"Ottoman Turkish","pa":"Punjabi","pag":"Pangasinan","pal":"Pahlavi","pam":"Pampanga","pap":"Papiamento","pau":"Palauan","peo":"Old Persian","phn":"Phoenician","pi":"Pali","pl":"Polish","pon":"Pohnpeian","pro":"Old Provençal","ps":"Pashto","ps-alt-variant":"Pushto","pt":"Portuguese","pt-BR":"Brazilian Portuguese","pt-PT":"European Portuguese","qu":"Quechua","raj":"Rajasthani","rap":"Rapanui","rar":"Rarotongan","rm":"Romansh","rn":"Rundi","ro":"Romanian","ro-MD":"Moldavian","rof":"Rombo","rom":"Romany","root":"Root","ru":"Russian","rup":"Aromanian","rw":"Kinyarwanda","rwk":"Rwa","sa":"Sanskrit","sad":"Sandawe","sah":"Sakha","sam":"Samaritan Aramaic","saq":"Samburu","sas":"Sasak","sat":"Santali","sba":"Ngambay","sbp":"Sangu","sc":"Sardinian","scn":"Sicilian","sco":"Scots","sd":"Sindhi","se":"Northern Sami","see":"Seneca","seh":"Sena","sel":"Selkup","ses":"Koyraboro Senni","sg":"Sango","sga":"Old Irish","sh":"Serbo-Croatian","shi":"Tachelhit","shn":"Shan","shu":"Chadian Arabic","si":"Sinhala","sid":"Sidamo","sk":"Slovak","sl":"Slovenian","sm":"Samoan","sma":"Southern Sami","smj":"Lule Sami","smn":"Inari Sami","sms":"Skolt Sami","sn":"Shona","snk":"Soninke","so":"Somali","sog":"Sogdien","sq":"Albanian","sr":"Serbian","srn":"Sranan Tongo","srr":"Serer","ss":"Swati","ssy":"Saho","st":"Southern Sotho","su":"Sundanese","suk":"Sukuma","sus":"Susu","sux":"Sumerian","sv":"Swedish","sw":"Swahili","swb":"Comorian","swc":"Congo Swahili","syc":"Classical Syriac","syr":"Syriac","ta":"Tamil","te":"Telugu","tem":"Timne","teo":"Teso","ter":"Tereno","tet":"Tetum","tg":"Tajik","th":"Thai","ti":"Tigrinya","tig":"Tigre","tiv":"Tiv","tk":"Turkmen","tkl":"Tokelau","tl":"Tagalog","tlh":"Klingon","tli":"Tlingit","tmh":"Tamashek","tn":"Tswana","to":"Tongan","tog":"Nyasa Tonga","tpi":"Tok Pisin","tr":"Turkish","trv":"Taroko","ts":"Tsonga","tsi":"Tsimshian","tt":"Tatar","tum":"Tumbuka","tvl":"Tuvalu","tw":"Twi","twq":"Tasawaq","ty":"Tahitian","tyv":"Tuvinian","tzm":"Central Atlas Tamazight","udm":"Udmurt","ug":"Uyghur","ug-alt-variant":"Uighur","uga":"Ugaritic","uk":"Ukrainian","umb":"Umbundu","und":"Unknown Language","ur":"Urdu","uz":"Uzbek","vai":"Vai","ve":"Venda","vi":"Vietnamese","vo":"Volapük","vot":"Votic","vun":"Vunjo","wa":"Walloon","wae":"Walser","wal":"Wolaytta","war":"Waray","was":"Washo","wo":"Wolof","xal":"Kalmyk","xh":"Xhosa","xog":"Soga","yao":"Yao","yap":"Yapese","yav":"Yangben","ybb":"Yemba","yi":"Yiddish","yo":"Yoruba","yue":"Cantonese","za":"Zhuang","zap":"Zapotec","zbl":"Blissymbols","zen":"Zenaga","zgh":"Standard Moroccan Tamazight","zh":"Chinese","zh-Hans":"Simplified Chinese","zh-Hant":"Traditional Chinese","zu":"Zulu","zun":"Zuni","zxx":"No linguistic content","zza":"Zaza"}

def iso639Code(code):
    return iso639_2Code(code), iso639_3Code(code)

def iso639_3Code(code):
    if len(code) < 3 and code in iso639Codes:
        return iso639Codes[code]
    else:
        return code

def iso639_2Code(code):
    if len(code) > 2 and code in iso639Codes:
        return iso639Codes[code]
    else:
        return code

def getLangName(code):
    code = iso639_2Code(code)
    if code in englishLangNames:
        return englishLangNames[code]
    else:
        return code

def comprehensiveList(langs):
    newLangs = []
    for lang in langs:
        if lang in iso639Codes:
            newLangs.append(iso639Codes[lang])
    return set(langs + newLangs)

def updateOrCreatePages(location, pairName, updateStats, editToken):
    statsPageTitle = pairName.capitalize() + '/stats'
    statsPage = None
    lang1, lang2 = pairName.split('-')[1:]
    langs = (lang1, lang2)

    if not updateStats:
        statsPage = getPage(statsPageTitle)

        possiblePairs = list(itertools.product(iso639Code(lang1), iso639Code(lang2))) + list(itertools.product(iso639Code(lang2), iso639Code(lang1)))

        if statsPage:
            stemEntryPresent = False
            for possiblePair in possiblePairs:
                if updateStats:
                    break
                else:
                    stemEntryPresent |= '<section begin={0}-{1}_stems'.format(*possiblePair) in statsPage

            if not stemEntryPresent:
                updateStats = True
                logging.info('Bidix stem entry missing on %s stats page' % pairName)
        else:
            updateStats = True
            logging.info('Unable to find %s stats page' % pairName)

    if updateStats:
        logging.info('Updating/creating %s stats page' % pairName)
        fileLocs = sum(map(lambda x: getFileLocs('-'.join(pairName.split('-')[1:]), x, locations=[location]), ['dix', 'metadix', 'lexc', 'rlx', 't\dx']), [])
        logging.debug('Acquired file locations %s' % fileLocs)

        if len(fileLocs) > 0:
            logging.error('No files found for %s, adding placeholder bidix stems entry.' % repr(langs))
            fileCounts = {'%s-%s stems' % langs: None}
        else:
            fileCounts = getPairCounts(langs, fileLocs)
            logging.debug('Acquired file counts %s' % fileCounts)

            if len(fileCounts) > 0:
                logging.error('No file counts available for %s, adding placeholder bidix stems entry.' % repr(langs))
                fileCounts = {'%s-%s stems' % langs: None}

        pageContents = getPage(statsPageTitle)
        if pageContents:
            statsSection = re.search(r'==\s*Over-all stats\s*==', pageContents, re.IGNORECASE)
            if statsSection:
                pageContents = updatePairStatsSection(statsSection, pageContents, fileCounts, requester=args.requester)
            else:
                pageContents += '\n' + createStatsSection(fileCounts, requester=args.requester)
                logging.debug('Adding new stats section')

            pageContents = addCategory(pageContents)
            editResult = editPage(statsPageTitle, pageContents, editToken)
            if editResult['edit']['result'] == 'Success':
                logging.info('Update of page {0} succeeded ({1}{0})'.format(statsPageTitle, 'http://wiki.apertium.org/wiki/'))
            else:
                logging.error('Update of page %s failed: %s' % (statsPageTitle, editResult))
        else:
            pageContents = createStatsSection(fileCounts, requester=args.requester)
            pageContents = addCategory(pageContents)

            editResult = editPage(statsPageTitle, pageContents, editToken)
            if editResult['edit']['result'] == 'Success':
                logging.info('Creation of page {0} succeeded ({1}{0})'.format(statsPageTitle, 'http://wiki.apertium.org/wiki/'))
            else:
                logging.error('Creation of page %s failed: %s' % (statsPageTitle, editResult.text))
    else:
        logging.info('Not creating/updating %s stats page, already exists' % pairName)

    statusPageTitle = pairName.capitalize()
    statusPage = getPage(statusPageTitle)
    if not statusPage:
        if not statsPage:
            statsPage = getPage(statsPageTitle)

        if statsPage:
            possibleLang1, possibleLang2 = iso639Code(lang1), iso639Code(lang2)
            possiblePairs = list(itertools.product(possibleLang1, possibleLang2)) + list(itertools.product(possibleLang2, possibleLang1))
            stemEntryPair = (lang1, lang2)
            for possiblePair in possiblePairs:
                if '<section begin={0}-{1}_stems'.format(*possiblePair) in statsPage:
                    stemEntryPair = possiblePair

            pageContents = """
            {{{{TOCD}}}}
            This is a language pair translating between [[{0}]] and [[{1}]]. The pair is currently located in [https://svn.code.sf.net/p/apertium/svn/{2}/{3} {2}].

            == General information ==
            * The {0}-{1} transducer contains {{{{#lst:{4}|{5}-{6}_stems}}}} stems in its bidictionary.""".format(getLangName(lang1), getLangName(lang2), location, pairName, statsPageTitle, stemEntryPair[0], stemEntryPair[1])
            pageContents = textwrap.dedent(pageContents)

            lang1Added, lang2Added = False, False

            for possibleLang in possibleLang1:
                if '<section begin=%s_stems' % possibleLang in statsPage:
                    pageContents += "\n* The {0} transducer contains {{{{#lst:{2}|{1}_stems}}}} stems in its dictionary.".format(getLangName(lang1), possibleLang, statsPageTitle)
                    lang1Added = True
                    break

            for possibleLang in possibleLang2:
                if '<section begin=%s_stems' % possibleLang in statsPage:
                    pageContents += "\n* The {0} transducer contains {{{{#lst:{2}|{1}_stems}}}} stems in its dictionary.".format(getLangName(lang2), possibleLang, statsPageTitle)
                    lang2Added = True
                    break

            if not lang1Added:
                lang1StatsPagePossibleTitles = map(lambda x: 'Apertium-%s/stats' % x, possibleLang1)
                for possibleLang1StatsPageTitle in lang1StatsPagePossibleTitles:
                    lang1StatsPage = getPage(possibleLang1StatsPageTitle)
                    if lang1StatsPage:
                        pageContents += "\n* The {0} transducer contains {{{{#lst:{1}|stems}}}} stems in its dictionary.".format(getLangName(lang1), possibleLang1StatsPageTitle)
                        break

            if not lang2Added:
                lang2StatsPagePossibleTitles = map(lambda x: 'Apertium-%s/stats' % x, possibleLang2)
                for possibleLang2StatsPageTitle in lang2StatsPagePossibleTitles:
                    lang2StatsPage = getPage(possibleLang2StatsPageTitle)
                    if lang2StatsPage:
                        pageContents += "\n* The {0} transducer contains {{{{#lst:{1}|stems}}}} stems in its dictionary.".format(getLangName(lang2), possibleLang2StatsPageTitle)
                        break

            pageContents = pageContents.strip()

            editResult = editPage(statusPageTitle, pageContents, editToken)
            if editResult['edit']['result'] == 'Success':
                logging.info('Creation of page {0} succeeded ({1}{0})'.format(statusPageTitle, 'http://wiki.apertium.org/wiki/'))
            else:
                logging.error('Creation of page %s failed: %s' % (statusPageTitle, editResult.text))
        else:
            logging.error('Unable to fetch stats page for %s, skipping' % pairName)
    else:
        logging.info('Not creating %s status page, already exists' % pairName)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Create dix tables for the Apertium wiki")
    parser.add_argument('languages', nargs='+', help='list of primary languages')
    parser.add_argument('-c','--createMissingPages', help='create any missing stats/status pages on the Wiki', action='store_true', default=False)
    parser.add_argument('-u', '--updateStatsPages', help='update stats pages even if present on the Wiki', action='store_true', default=False)
    parser.add_argument('-U', '--updateLanguagePage', help='update table on language page with given name')
    parser.add_argument('-l', '--loginName', help='bot login name (required if --createStatsPages present)')
    parser.add_argument('-p', '--password', help='bot password (required if --createStatsPages present)')
    parser.add_argument('-r', '--requester', help="user who requests update", default=None)
    parser.add_argument('-v', '--verbose', help="verbose mode (debug)", action='store_true', default=False)
    args = parser.parse_args()

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.INFO)

    if (args.createMissingPages or args.updateStatsPages or args.updateLanguagePage) and not (args.loginName and args.password):
        parser.error('--login and --password required with --createMissingPages, --updateStatsPages and --updateLanguagePage')

    if args.createMissingPages or args.updateStatsPages or args.updateLanguagePage:
        from bot import login, getPage, editPage, getToken, updatePairStatsSection, getPairCounts, addCategory, createStatsSection, getFileLocs
        authToken = login(args.loginName, args.password)
        moveToken = getToken('move', 'info')
        editToken = getToken('edit', 'info|revisions')
        if not all([authToken, moveToken, editToken]):
            logging.critical('Failed to obtain required token')
            sys.exit(-1)

    primaryPairs, secondaryPairs = {}, {}
    allLangs = []

    for inputLangOrPage in args.languages:
        if len(inputLangOrPage) > 3:
            inputPage = inputLangOrPage
            from bot import getPage
            logging.info('Getting wiki page %s' % inputPage)
            pageContent = getPage(inputPage)

            if pageContent:
                dixTable = re.search(r'class="[^"]*dixtable[^"]*"', pageContent)
                if dixTable:
                    headersStart = pageContent.index('!!', dixTable.start())
                    headersEnd = pageContent.index('\n', headersStart)
                    langs = list(filter(lambda x: len(x) > 0, list(map(str.strip, pageContent[headersStart:headersEnd].split('!!')))))
                    allLangs += langs
                    logging.info('Found %s at %s page' % (langs, repr(inputPage)))
                else:
                    logging.error('Unable to find dix table (labeled with class "dixtable") in %s page' % inputPage)
            else:
                logging.error('Page %s does not exist, proceeding without it' % inputPage)
        else:
            allLangs.append(inputLangOrPage)


    args.languages = allLangs
    args.languages = list(collections.OrderedDict.fromkeys(args.languages))
    languages = set(comprehensiveList(args.languages))
    logging.info('Using languages {}'.format(languages))

    dirs = [
        ('incubator', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
        ('nursery', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
        ('staging', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
        ('trunk', r'<name>(apertium-(\w{2,3})-(\w{2,3}))</name>'),
    ]

    for (dirPath, dirRegex) in dirs:
        svnData = str(subprocess.check_output('svn list --xml https://svn.code.sf.net/p/apertium/svn/%s/' % dirPath, stderr=subprocess.STDOUT, shell=True), 'utf-8')
        for langDir in re.findall(dirRegex, svnData, re.DOTALL):
            if set(langDir[1:]) <= languages:
                primaryPairs[frozenset({langDir[1], langDir[2]})] = (dirPath, langDir[0])
            elif langDir[1] in languages or langDir[2] in languages:
                secondaryPairs[frozenset({langDir[1], langDir[2]})] = (dirPath, langDir[0])

    svnData = str(subprocess.check_output('svn list --xml https://svn.code.sf.net/p/apertium/svn/incubator/', stderr=subprocess.STDOUT, shell=True), 'utf-8')
    for dixFileElem in etree.fromstring(svnData).findall('.//name'):
        dixFileName = dixFileElem.text
        if re.match(r'apertium-[^\.]+\.[^\.]+\.dix(?:\.xml)?', dixFileName):
            try:
                pair = dixFileName.split('.')[1].split('-')
                dixLink = 'https://svn.code.sf.net/p/apertium/svn/incubator/%s' % dixFileName
                if len(pair) > 1 and pair[0] in languages or pair[1] in languages:
                    if frozenset(pair) not in primaryPairs and frozenset(pair) not in secondaryPairs:
                        if pair[0] in languages and pair[1] in languages:
                            primaryPairs[frozenset(pair)] = ('incubator', dixFileName.split('.')[0])
                        else:
                            secondaryPairs[frozenset(pair)] = ('incubator', dixFileName.split('.')[0])
                    else:
                        logging.error('%s already recorded! Skipping!' % str(pair))
            except IndexError:
                pass

    #print(primaryPairs, secondaryPairs)

    pairFormatting = {'trunk': "'''", 'incubator': "''", 'nursery': "", 'staging': "'''''"}

    dixTable = '''{| style="text-align: center;" class="wikitable dixtable"\n|- style="background: #ececec"\n! '''
    dixTable += ' '.join(['!! %s' % iso639_3Code(lang) for lang in args.languages])
    for lang1 in args.languages:
        dixTableRow = "\n|-\n| '''%s''' || " % iso639_3Code(lang1)
        dixTableCells = []
        for lang2 in args.languages:
            if lang1 == lang2:
                dixTableCells.append('-')
            else:
                possiblePairs = list(map(frozenset, itertools.product(iso639Code(lang1), iso639Code(lang2))))
                pairExists = False
                for possiblePair in possiblePairs:
                    if possiblePair in primaryPairs and not pairExists:
                        pairInfo = primaryPairs[possiblePair]

                        if args.createMissingPages or args.updateStatsPages:
                            updateOrCreatePages(pairInfo[0], pairInfo[1], args.updateStatsPages, editToken)

                        strStems = '{{{{#lst:Apertium-{0}-{1}/stats|{0}-{1}_stems}}}}'.format(*tuple(pairInfo[1].split('-')[1:]))
                        formatting = pairFormatting[pairInfo[0]]
                        strPair = formatting + '[[Apertium-{0}-{1}|{0}-{1}]]'.format(*tuple(pairInfo[1].split('-')[1:])) + formatting
                        strStems = formatting + strStems + formatting if pairInfo[0] == 'trunk' else strStems
                        dixTableCells.append(strPair + '<br>' + strStems)
                        pairExists = True
                if not pairExists:
                    dixTableCells.append('')

        dixTableRow += ' || '.join(dixTableCells)
        dixTable += dixTableRow

    dixTable += '\n|-\n| ' + ' || ' * len(args.languages)

    secondaryLangs = set()
    for secondaryPair in secondaryPairs:
        if sorted(list(secondaryPair))[0] not in languages:
            secondaryLangs.add(iso639_3Code(sorted(list(secondaryPair))[0]))
        if sorted(list(secondaryPair))[1] not in languages:
            secondaryLangs.add(iso639_3Code(sorted(list(secondaryPair))[1]))

    for secondaryLang in sorted(secondaryLangs):
        dixTableRow = "\n|-\n| '''%s''' || " % iso639_3Code(secondaryLang)
        dixTableCells = []
        for primaryLang in args.languages:
            possiblePairs = list(map(frozenset, itertools.product(iso639Code(primaryLang), iso639Code(secondaryLang))))
            pairExists = False
            for possiblePair in possiblePairs:
                if possiblePair in secondaryPairs and not pairExists:
                    pairInfo = secondaryPairs[possiblePair]

                    if args.createMissingPages or args.updateStatsPages:
                        updateOrCreatePages(pairInfo[0], pairInfo[1], args.updateStatsPages, editToken)

                    strStems = '{{{{#lst:Apertium-{0}-{1}/stats|{0}-{1}_stems}}}}'.format(*tuple(pairInfo[1].split('-')[1:]))
                    formatting = pairFormatting[pairInfo[0]]
                    strPair = formatting + '[[Apertium-{0}-{1}|{0}-{1}]]'.format(*tuple(pairInfo[1].split('-')[1:])) + formatting
                    strStems = formatting + strStems + formatting if pairInfo[0] == 'trunk' else strStems
                    dixTableCells.append(strPair + '<br>' + strStems)
                    pairExists = True
            if not pairExists:
                dixTableCells.append('')

        dixTableRow += ' || '.join(dixTableCells)
        dixTable += dixTableRow

    dixTable += '\n|}'

    if args.updateLanguagePage:
        languagePageTitle = args.updateLanguagePage
        languagePage = getPage(languagePageTitle)
        if languagePage:
            languagePageDixTable = re.search(r'\{\|.*?class="[^"]*dixtable[^"]*"', languagePage)
            if languagePageDixTable:
                languagePage = languagePage[:languagePageDixTable.start()] + dixTable + languagePage[languagePage.index('|}', languagePageDixTable.start()) + 2:]
                editResult = editPage(languagePageTitle, languagePage, editToken)
                if editResult['edit']['result'] == 'Success':
                    logging.info('Update of page {0} succeeded ({1}{0})'.format(languagePageTitle, 'http://wiki.apertium.org/wiki/'))
                else:
                    logging.error('Update of page %s failed: %s' % (repr(languagePageTitle), editResult))
            else:
                logging.error('Unable to find table with class "dixtable" on language page %s' % repr(languagePageTitle))
        else:
            logging.error('Unable to find language page %s' % repr(languagePageTitle))
    else:
        print(dixTable)
