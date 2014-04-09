﻿var locale;
var languages = {'af': 'Afrikaans', 'id': 'Bahasa Indonesia', 'cy': 'Cymraeg', 'de': 'Deutsch', 'en': 'English', 'ga': 'Gaeilge', 'gv': 'Gaelg', 'gd': 'Gàidhlig', 'rn': 'Ikirundi', 'sw': 'Kiswahili', 'lg': 'Luganda', 'mt': 'Malti', 'nl': 'Nederlands', 'sq': 'Shqip', 'vi': 'Tiếng Việt', 'tr': 'Türkçe', 'az': 'azərbaycan', 'br': 'brezhoneg', 'ca': 'català', 'da': 'dansk', 'se': 'davvisámegiella', 'et': 'eesti', 'es': 'español', 'eo': 'esperanto', 'eu': 'euskara', 'fr': 'français', 'fo': 'føroyskt', 'ia': 'interlingua', 'xh': 'isiXhosa', 'zu': 'isiZulu', 'it': 'italiano', 'mfe': 'kreol morisien', 'lv': 'latviešu', 'lt': 'lietuvių', 'hu': 'magyar', 'nb': 'norsk bokmål', 'nn': 'nynorsk', 'uz': 'oʻzbekcha', 'pl': 'polski', 'pt': 'português', 'ro': 'română', 'rm': 'rumantsch', 'sk': 'slovenčina', 'sl': 'slovenščina', 'fi': 'suomi', 'sv': 'svenska', 'is': 'íslenska', 'cs': 'čeština', 'el': 'Ελληνικά', 'tg': 'Тоҷикӣ', 'ba': 'башҡортса', 'be': 'беларуская', 'bg': 'български', 'os': 'ирон', 'kum': 'къумукъча', 'ky': 'кыргызча', 'mk': 'македонски', 'ru': 'русский', 'tt': 'татарча', 'uk': 'українська', 'kk': 'қазақша', 'hy': 'հայերեն', 'he': 'עברית', 'ur': 'اردو', 'ar': 'العربية', 'fa': 'فارسی', 'ne': 'नेपाली', 'mr': 'मराठी', 'hi': 'हिंदी', 'as': 'অসমীয়া', 'bn': 'বাংলা', 'pa': 'ਪੰਜਾਬੀ', 'te': 'తెలుగు', 'ml': 'മലയാളം', 'si': 'සිංහල', 'th': 'ไทย', 'lo': 'ລາວ', 'zh': '中文', 'ko': '한국어'};
var iso639Codes = {"abk":"ab","aar":"aa","afr":"af","aka":"ak","sqi":"sq","amh":"am","ara":"ar","arg":"an","hye":"hy","asm":"as","ava":"av","ave":"ae","aym":"ay","aze":"az","bam":"bm","bak":"ba","eus":"eu","bel":"be","ben":"bn","bih":"bh","bis":"bi","bos":"bs","bre":"br","bul":"bg","mya":"my","cat":"ca","cha":"ch","che":"ce","nya":"ny","zho":"zh","chv":"cv","cor":"kw","cos":"co","cre":"cr","hrv":"hr","ces":"cs","dan":"da","div":"dv","nld":"nl","dzo":"dz","eng":"en","epo":"eo","est":"et","ewe":"ee","fao":"fo","fij":"fj","fin":"fi","fra":"fr","ful":"ff","glg":"gl","kat":"ka","deu":"de","ell":"el","grn":"gn","guj":"gu","hat":"ht","hau":"ha","heb":"he","her":"hz","hin":"hi","hmo":"ho","hun":"hu","ina":"ia","ind":"id","ile":"ie","gle":"ga","ibo":"ig","ipk":"ik","ido":"io","isl":"is","ita":"it","iku":"iu","jpn":"ja","jav":"jv","kal":"kl","kan":"kn","kau":"kr","kas":"ks","kaz":"kk","khm":"km","kik":"ki","kin":"rw","kir":"ky","kom":"kv","kon":"kg","kor":"ko","kur":"ku","kua":"kj","lat":"la","ltz":"lb","lug":"lg","lim":"li","lin":"ln","lao":"lo","lit":"lt","lub":"lu","lav":"lv","glv":"gv","mkd":"mk","mlg":"mg","msa":"ms","mal":"ml","mlt":"mt","mri":"mi","mar":"mr","mah":"mh","mon":"mn","nau":"na","nav":"nv","nob":"nb","nde":"nd","nep":"ne","ndo":"ng","nno":"nn","nor":"no","iii":"ii","nbl":"nr","oci":"oc","oji":"oj","chu":"cu","orm":"om","ori":"or","oss":"os","pan":"pa","pli":"pi","fas":"fa","pol":"pl","pus":"ps","por":"pt","que":"qu","roh":"rm","run":"rn","ron":"ro","rus":"ru","san":"sa","srd":"sc","snd":"sd","sme":"se","smo":"sm","sag":"sg","srp":"sr","gla":"gd","sna":"sn","sin":"si","slk":"sk","slv":"sl","som":"so","sot":"st","azb":"az","spa":"es","sun":"su","swa":"sw","ssw":"ss","swe":"sv","tam":"ta","tel":"te","tgk":"tg","tha":"th","tir":"ti","bod":"bo","tuk":"tk","tgl":"tl","tsn":"tn","ton":"to","tur":"tr","tso":"ts","tat":"tt","twi":"tw","tah":"ty","uig":"ug","ukr":"uk","urd":"ur","uzb":"uz","ven":"ve","vie":"vi","vol":"vo","wln":"wa","cym":"cy","wol":"wo","fry":"fy","xho":"xh","yid":"yi","yor":"yo","zha":"za","zul":"zu", "hbs":"sh", "arg":"an", "pes":"fa"}
var languagesInverse = {}, iso639CodesInverse = {};
var localizedLanguageCodes = {}, localizedLanguageNames = {};
var notAvailableText = 'Translation not yet available!', detectedText = 'detected';

$(document).ready(function () {
    $.each(languages, function (key, value) {
        languagesInverse[value] = key
    });
    $.each(iso639Codes, function (key, value) {
        iso639CodesInverse[value] = key
    });

    $('.localeSelect').change(function () {
        locale = $(this).val();
        localizeLanguageNames();
        persistChoices('localization');
    });
    
    var possibleItems = {'translation': getPairs, 'generation': getGenerators, 'analyzation': getAnalyzers};
    var deferredItems = [getLocales()];
    if(config.ENABLED_MODES === undefined)
        $.each(possibleItems, function (mode, deferrer) {
            deferredItems.push(deferrer.call());
        });
    else 
        $.each(config.ENABLED_MODES, function () {
            if(possibleItems[this])
                deferredItems.push(possibleItems[this].call());
        });

    $.when.apply($, deferredItems).then(function () {
        locale = $('.localeSelect').val();
        localizeLanguageNames();
    });
});

function getLocale(deferred) {
    restoreChoices('localization');

    if(!locale) {
        $.jsonp({
            url: config.APY_URL + '/getLocale',
            beforeSend: ajaxSend,
            success: function (data) {
                for(var i = 0; i < data.length; i++) {
                    localeGuess = data[i];
                    if(localeGuess.indexOf('-') !== -1)
                        localeGuess = localeGuess.split('-')[0];
                    if(localeGuess in iso639Codes) {
                        locale = localeGuess;
                        break;
                    }
                    else if(localeGuess in iso639CodesInverse) {
                        locale = iso639CodesInverse[localeGuess];
                        break;
                    }
                }
                $('.localeSelect').val(locale);
                persistChoices('localization');
            },
            error: function () {
                console.error('Failed to determine locale, defaulting to en');
                locale = 'en';
            },
            complete: function () {
                ajaxComplete();
                deferred.resolve();
            }
        });
    }
    else
        deferred.resolve();
}

function getLocales() {
    var deferred = $.Deferred();

    $.ajax({
        url: './assets/strings/locales.json',
        type: 'GET',
        success: function (data) {
            $.each(data, function (code, langName) {
                $('.localeSelect').append($('<option></option>').prop('value', code).text(langName));
            });
            getLocale(deferred);
        },
        error: function (jqXHR, textStatus, errorThrow) {
            console.error('Failed to fetch available locales: ' + errorThrow);
            deferred.resolve();
        }
    });

    return deferred.promise();
}

function generateLanguageList() {
    var languages = [];
    $.each(srcLangs.concat(dstLangs), function () {
        languages.push(this);
    });

    var langObjects = [generators, analyzers];
    for(var i = 0; i < langObjects.length; i++) {
        $.each(Object.keys(langObjects[i]), function () {
            if(this.indexOf('-') !== -1)
                languages = languages.concat(this.split('-'));
            else
                languages.push(this);
        });
    }

    languages = languages.filter(onlyUnique);
    return languages;
}

function localizeLanguageNames() {
    var languages = generateLanguageList();
    localizeStrings(locale);

    $.jsonp({
        url: config.APY_URL + '/listLanguageNames?locale=' + locale + '&languages=' + languages.join('+'),
        beforeSend: ajaxSend,
        complete: ajaxComplete,
        success: function (data) {
            localizedLanguageNames = data;
            localizedLanguageCodes = {};
            $.each(data, function (key, value) {
                localizedLanguageCodes[value] = key
            });

            if(modeEnabled('translation')) {
                populateTranslationList();
                refreshLangList();
            }

            if(modeEnabled('generation'))
                populateGeneratorList(generatorData);

            if(modeEnabled('analyzation'))
                populateAnalyzerList(analyzerData);
        },
        error: function () {
            localizedLanguageNames = {};
        }
    });
}

function localizeStrings(locale) {
    var replacements = {
        '{{apertiumTurkicUrl}}': '<a href="http://wiki.apertium.org/wiki/Apertium_Turkic" target="_blank">Apertium Turkic</a>'
    }

    $.ajax({
        url: './assets/strings/' + locale + '.json',
        type: 'GET',
        success: function (data) {
            for(var textId in data) {
                var text = data[textId];
                $.each(replacements, function (name, value) {
                    if(text.indexOf(name) !== -1)
                        text = text.replace(name, value);
                });

                $('[data-text=' + textId + ']').html(text);
            }
            if(data['Not_Available'])
                notAvailableText = data['Not_Available'];
            if(data['detected'])
                detectedText = data['detected'];
        },
        error: function (jqXHR, textStatus, errorThrow) {
            console.error('Failed to fetch localized strings for ' + locale + ': ' + errorThrow);
        }
    });
}

function getLangByCode(code) {
    code = code ? code.trim() : code;
    if(localizedLanguageNames[code])
        return localizedLanguageNames[code];
    else if(languages[code])
        return languages[code];
    else if(iso639Codes[code] && languages[iso639Codes[code]])
        return languages[iso639Codes[code]];
    else
        return code;
}
