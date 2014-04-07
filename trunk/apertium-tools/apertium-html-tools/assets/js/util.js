﻿function ajaxSend() {
    $('#loading-indicator').show();
}

function ajaxComplete() {
    $('#loading-indicator').hide();
}

$(document).ajaxSend(ajaxSend);
$(document).ajaxComplete(ajaxComplete);
$(document).ajaxError(ajaxComplete);

$.jsonp.setup({
    callbackParameter: 'callback'
});

$(document).ready(function () {
    $('#noscript').hide();
    $('.navbar').css('margin-top', '0px');
    $('body > .container').css('margin-top', '0px');

    if(SHOW_NAVBAR) {
        if(ENABLED_MODES === undefined)
            $('.nav a').removeClass('hide');
        else
            $.each(ENABLED_MODES, function () {
                $('.nav a[data-mode=' + this + ']').removeClass('hide');
            });
    }
    else {
        $('.navbar-toggle').hide();
        $('.nav').hide();
    }

    var hash = parent.location.hash;

    if(!hash || !$(hash + 'Container')) {
        hash = '#' + DEFAULT_MODE;
        parent.location.hash = hash;
    }

    $('.modeContainer' + hash + 'Container').show();
    $('.nav li > a[data-mode=' + hash.substring(1) + ']').parent().addClass('active');

    $('.nav a').click(function () {
        var mode = $(this).data('mode');
        $('.nav li').removeClass('active');
        $(this).parent('li').addClass('active');
        $('.modeContainer:not(#' + mode + 'Container)').hide({
            queue: false
        });
        $('#' + mode + 'Container').show({
            queue: false
        });
    });

    if(ALLOWED_LANGS)
        $.each(ALLOWED_LANGS.slice(0), function () {
            if(iso639Codes[this])
                ALLOWED_LANGS.push(iso639Codes[this]);
            if(iso639CodesInverse[this])
                ALLOWED_LANGS.push(iso639CodesInverse[this]);
        });

    $('form').submit(function () {
        return false;
    });

    $('.modal').on('show.bs.modal', function () {
        $.each($(this).find('img[data-src]'), function () {
            $(this).attr('src', $(this).attr('data-src'));
        });
    });
});

function filterLangList(langs, filterFn) {
    if(ALLOWED_LANGS === undefined && ALLOWED_VARIANTS === undefined)
        return langs;
    else {
        if(!filterFn)
            filterFn = function (code) {
                return  allowedLang(code) || ((code.indexOf('-') !== -1 && (allowedLang(code.split('-')[0]) || allowedLang(code.split('-')[1]))));
            };

        return langs.filter(filterFn);
    }
}

function allowedLang (code) {
    if(code.indexOf('_') === -1)
        return ALLOWED_LANGS === undefined || ALLOWED_LANGS.indexOf(code) !== -1;
    else
        return allowedLang(code.split('_')[0]) && (ALLOWED_VARIANTS === undefined || ALLOWED_VARIANTS.indexOf(code.split('_')[1]) !== -1);
}

function onlyUnique(value, index, self) {
    return self.indexOf(value) === index;
}

function isSubset(subset, superset) {
    return subset.every(function (val) {
        return superset.indexOf(val) >= 0;
    });
}
