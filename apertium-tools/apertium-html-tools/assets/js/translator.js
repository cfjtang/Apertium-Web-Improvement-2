var pairs = {};
var srcLangs = [], dstLangs = [];
var curSrcLang, curDstLang;
var recentSrcLangs = [], recentDstLangs = [];

if(modeEnabled('translation')) {
    $(document).ready(function () {
        $('#srcLanguages').on('click', '.languageName:not(.text-muted)', function () {
            var code = $(this).attr('data-code');
            curSrcLang = code;

            $('.srcLang').removeClass('active');
            if(recentSrcLangs.indexOf(code) === -1) {
                recentSrcLangs = [code, recentSrcLangs[0], recentSrcLangs[1]];
                $('#srcLang1').addClass('active');
                refreshLangList(true);
            }
            else {
                $('#srcLang' + (recentSrcLangs.indexOf(code) + 1)).addClass('active');
                persistChoices('translator');
            }

            muteLanguages();
            translate();
        });

        $('#dstLanguages').on('click', '.languageName:not(.text-muted)', function () {
            var code = $(this).attr('data-code');
            curDstLang = code;

            $('.dstLang').removeClass('active');
            if(recentDstLangs.indexOf(code) === -1) {
                recentDstLangs = [code, recentDstLangs[0], recentDstLangs[1]];
                $('#dstLang1').addClass('active');
                refreshLangList();
            }
            else {
                $('#dstLang' + (recentDstLangs.indexOf(code) + 1)).addClass('active');
                persistChoices('translator');
            }

            muteLanguages();
            translate();
        });

        $('.srcLang').click(function () {
            curSrcLang = $(this).attr('data-code');
            $('.srcLang').removeClass('active');
            $(this).addClass('active');
            refreshLangList(true);
            muteLanguages();
            translate();
        });

        $('.dstLang').click(function () {
            curDstLang = $(this).attr('data-code');
            $('.dstLang').removeClass('active');
            $(this).addClass('active');
            refreshLangList();
            muteLanguages();
            translate();
        });

        $('.translateBtn').click(function () {
            translate();
        });

        var timer, timeout = 1000;
        $('#originalText').on('keyup paste', function (event) {
            if(timer && $('#instantTranslation').prop('checked'))
                clearTimeout(timer);
            timer = setTimeout(function () {
                if($('#instantTranslation').prop('checked'))
                    translate();
            }, timeout);
        });

        $('#instantTranslation').on('change', function () {
            persistChoices('translator');
        });

        $('#originalText').on('input propertychange', function () {
            persistChoices('translator');
        });

        $('#originalText').submit(function () {
            translate();
        });

        $('#detect').click(function () {
            $('.srcLang').removeClass('active');
            $(this).addClass('active');
            detectLanguage();
        });

        $('.swapLangBtn').click(function () {
            var srcCode = $('.srcLang.active').attr('data-code'), dstCode = $('.dstLang.active').attr('data-code');
            curSrcLang = dstCode;
            curDstLang = srcCode;

            if(recentSrcLangs.indexOf(curSrcLang) !== -1) {
                $('.srcLang').removeClass('active');
                $('#srcLang' + (recentSrcLangs.indexOf(curSrcLang) + 1)).addClass('active');
                $('#srcLangSelect').val(curSrcLang);
            }
            else
                recentSrcLangs[recentSrcLangs.indexOf(srcCode)] = curSrcLang;

            if(recentDstLangs.indexOf(curDstLang) !== -1) {
                $('.dstLang').removeClass('active');
                $('#dstLang' + (recentDstLangs.indexOf(curDstLang) + 1)).addClass('active');
                $('#dstLangSelect').val(curDstLang);
            }
            else
                recentDstLangs[recentDstLangs.indexOf(dstCode)] = curDstLang;

            refreshLangList(true);
            muteLanguages();

            if($('.active > #detectedText')) {
                $('.srcLang').removeClass('active');
                $('#srcLang' + (recentSrcLangs.indexOf(curSrcLang) + 1)).addClass('active');
            }
        });

        $('#srcLangSelect').change(function () {
            var selectValue = $(this).val();
            if(selectValue === 'detect')
                detectLanguage();
            else {
                curSrcLang = $(this).val();
                recentSrcLangs.unshift(curSrcLang);
                $('.srcLang').removeClass('active');
                $('#srcLang1').addClass('active');

                refreshLangList(true);
                muteLanguages();
                translate();
            }
        });

        $('#dstLangSelect').change(function () {
            curDstLang = $(this).val();
            recentDstLangs.unshift(curDstLang);
            $('.dstLang').removeClass('active');
            $('#dstLang1').addClass('active');

            refreshLangList(true);
            translate();
        });
    });
}

function getPairs() {
    var deferred = $.Deferred();

    var pairData = readCache('pairs', 'LIST_REQUEST');
    if(pairData) {
        handlePairs(pairData);
        deferred.resolve();
    }
    else {
        console.error('Translation pairs cache ' + (pairs === null ? 'stale' : 'miss') + ', retrieving from server');
        $.jsonp({
            url: config.APY_URL + '/list?q=pairs',
            beforeSend: ajaxSend,
            success: function (data) {
                handlePairs(data['responseData']);
                cache('pairs', data['responseData']);
            },
            error: function () {
                console.error('Failed to get available translation language pairs');
                translationNotAvailable();
            },
            complete: function () {
                ajaxComplete();
                deferred.resolve();
            }
        });
    }

    function handlePairs(pairData) {
        $.each(pairData, function (i, pair) {
            srcLangs.push(pair.sourceLanguage);
            dstLangs.push(pair.targetLanguage);

            if(pairs[pair.sourceLanguage])
                pairs[pair.sourceLanguage].push(pair.targetLanguage);
            else
                pairs[pair.sourceLanguage] = [pair.targetLanguage];
        });
        srcLangs = filterLangList(srcLangs.filter(onlyUnique));
        dstLangs = filterLangList(dstLangs.filter(onlyUnique));

        curSrcLang = srcLangs[0];
        curDstLang = dstLangs[0];
        for(var i = 0; i < 3; i++) {
            recentSrcLangs.push(i < srcLangs.length ? srcLangs[i] : undefined);
            recentDstLangs.push(i < dstLangs.length ? dstLangs[i] : undefined);
        }

        populateTranslationList();
        restoreChoices('translator');
        refreshLangList();
    }

    return deferred.promise();
}

function refreshLangList(resetDetect) {
    recentSrcLangs = filterLangList(recentSrcLangs, srcLangs);
    recentDstLangs = filterLangList(recentDstLangs, dstLangs);

    persistChoices('translator');

    for(var i = 0; i < 3; i++) {
        if(i < recentSrcLangs.length && recentSrcLangs[i])
            $('#srcLang' + (i + 1)).attr('data-code', recentSrcLangs[i]).text(getLangByCode(recentSrcLangs[i]));
        if(i < recentDstLangs.length && recentDstLangs[i])
            $('#dstLang' + (i + 1)).attr('data-code', recentDstLangs[i]).text(getLangByCode(recentDstLangs[i]));
    }

    if($('#detectedText').parent('.srcLang').attr('data-code'))
        $('#detectedText').text(getLangByCode($('#detectedText').parent('.srcLang').attr('data-code')) + ' - ' + detectedText);

    if(resetDetect) {
        $('#detectText').show();
        $('#detectedText').hide();
    }

    function filterLangList(recentLangs, allLangs) {
        recentLangs = recentLangs.filter(onlyUnique);
        if(recentLangs.length < 3)
            for(var i = 0; i < allLangs.length; i++)
                if(recentLangs.length < 3 && recentLangs.indexOf(allLangs[i]) === -1)
                    recentLangs.push(allLangs[i]);
        if(recentLangs.length > 3)
            recentLangs = recentLangs.slice(0, 3);
        return recentLangs;
    }
}

function populateTranslationList() {
    sortTranslationList();
    $('.languageName').remove();
    $('.languageCol').show().removeClass('col-sm-3 col-sm-4 col-sm-6 col-sm-12');

    var numSrcCols = Math.ceil(srcLangs.length / 8) < 5 ? Math.ceil(srcLangs.length / 8) : 4,
        numDstCols = Math.ceil(dstLangs.length / 8) < 5 ? Math.ceil(dstLangs.length / 8) : 4;
    var srcLangsPerCol = Math.ceil(srcLangs.length / numSrcCols),
        dstLangsPerCol = Math.ceil(dstLangs.length / numDstCols);

    $('#srcLanguages').css('min-width', Math.floor(650 * (numSrcCols / 4)) + 'px');
    $('#srcLanguages .languageCol').addClass('col-sm-' + (12 / numSrcCols));
    $('#srcLanguages .languageCol:gt(' + (numSrcCols - 1) + ')').hide();

    $('#dstLanguages').css('min-width', Math.floor(650 * (numDstCols / 4)) + 'px');
    $('#dstLanguages .languageCol').addClass('col-sm-' + (12 / numDstCols));
    $('#dstLanguages .languageCol:gt(' + (numDstCols - 1) + ')').hide();

    for(var i = 0; i < numSrcCols; i++) {
        var numSrcLang = Math.ceil(srcLangs.length / numSrcCols) * i;
        for(var j = numSrcLang; j < numSrcLang + srcLangsPerCol; j++)
            if(numSrcLang < srcLangs.length) {
                var langCode = srcLangs[j], langName = getLangByCode(langCode);
                $('#srcLanguages .languageCol:eq(' + i + ')').append($('<div class="languageName"></div>').attr('data-code', langCode).text(langName));
            }
    }

     for(var i = 0; i < numDstCols; i++) {
        var numDstLang = Math.ceil(dstLangs.length / numDstCols) * i;
        for(var j = numDstLang; j < numDstLang + dstLangsPerCol; j++)
            if(numDstLang < dstLangs.length) {
                var langCode = dstLangs[j], langName = getLangByCode(langCode);
                $('#dstLanguages .languageCol:eq(' + i + ')').append($('<div class="languageName"></div>').attr('data-code', langCode).text(langName));
            }
    }

    $('.langSelect option[value!=detect]').remove();
    $.each(srcLangs, function () {
        $('#srcLangSelect').append($('<option></option>').prop('value', this).text(getLangByCode(this)));
    });
    $.each(dstLangs, function () {
        $('#dstLangSelect').append($('<option></option>').prop('value', this).text(getLangByCode(this)));
    });

    $('#srcLangSelect').val(curSrcLang);
    $('#dstLangSelect').val(curDstLang);

    muteLanguages();

    function sortTranslationList() {
        var sortLocale = locale in iso639Codes ? iso639Codes[locale] : locale;

        srcLangs = srcLangs.sort(function (a, b) {
            return getLangByCode(a).localeCompare(getLangByCode(b), sortLocale);
        });

        dstLangs = dstLangs.sort(function (a, b) {
            return getLangByCode(a).localeCompare(getLangByCode(b), sortLocale);
        });
    }
}

function translate() {
    if(pairs[curSrcLang] && pairs[curSrcLang].indexOf(curDstLang) !== -1) {
        sendGAEvent('translator', 'translate', curSrcLang + '-' + curDstLang);
        $.jsonp({
            url: config.APY_URL + '/translate',
            beforeSend: ajaxSend,
            complete: ajaxComplete,
            data: {
                'langpair': curSrcLang + '|' + curDstLang,
                'q': $('#originalText').val()
            },
            success: function (data) {
                if(data.responseStatus === 200) {
                    $('#translatedText').html(data.responseData.translatedText);
                    $('#translatedText').removeClass('notAvailable');
                }
                else
                    translationNotAvailable();
            },
            error: translationNotAvailable
        });
    }
    else
        translationNotAvailable();
}

function detectLanguage() {
    $.jsonp({
        url: config.APY_URL + '/identifyLang',
        beforeSend: ajaxSend,
        complete: ajaxComplete,
        data: {
            'q': $('#originalText').val()
        },
        success: function (data) {
            var possibleLanguages = [];
            for(var lang in data)
                possibleLanguages.push([lang.indexOf('-') !== -1 ? lang.split('-')[0] : lang, data[lang]]);
            possibleLanguages.sort(function (a, b) {
                return b[1] - a[1];
            });

            oldSrcLangs = recentSrcLangs;
            recentSrcLangs = [];
            for(var i = 0; i < possibleLanguages.length; i++)
                if(recentSrcLangs.length < 3 && possibleLanguages[i][0] in pairs)
                    recentSrcLangs.push(possibleLanguages[i][0]);
            recentSrcLangs = recentSrcLangs.concat(oldSrcLangs);
            if(recentSrcLangs.length > 3)
                recentSrcLangs = recentSrcLangs.slice(0, 3);

            curSrcLang = recentSrcLangs[0];
            $('#srcLangSelect').val(curSrcLang);
            muteLanguages();

            $('#detectedText').parent('.srcLang').attr('data-code', curSrcLang);
            refreshLangList();
            $('#detectedText').show();
            $('#detectText').hide();
        },
        error: function () {
            $('#srcLang1').click();
        }
    });
}

function translationNotAvailable() {
    $('#translatedText').text(notAvailableText);
    $('#translatedText').addClass('notAvailable');
}

function muteLanguages() {
    $('.languageName.text-muted').removeClass('text-muted');
    $('.dstLang').removeClass('disabledLang').prop('disabled', false);

    $.each($('#dstLanguages .languageName'), function () {
        if(!pairs[curSrcLang] || pairs[curSrcLang].indexOf($(this).attr('data-code')) === -1)
            $(this).addClass('text-muted');
    });
    $.each($('.dstLang'), function () {
        if(!pairs[curSrcLang] || pairs[curSrcLang].indexOf($(this).attr('data-code')) === -1)
            $(this).addClass('disabledLang').prop('disabled', true);
    });

    $.each($('#dstLangSelect option'), function(i, element) {
        $(element).prop('disabled', !pairs[curSrcLang] || pairs[curSrcLang].indexOf($(element).val()) === -1);
    });
}
