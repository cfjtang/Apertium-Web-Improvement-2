var APY_URL = '//localhost:2737';

function ajaxSend() {
    $("#loading-indicator").show(); 
}

function ajaxComplete() { 
    $("#loading-indicator").hide(); 
}

$(document).ajaxSend(ajaxSend);
$(document).ajaxComplete(ajaxComplete);

$(document).ready(function() {
    if(!parent.location.hash || !$(parent.location.hash + 'Container'))
        parent.location.hash = '#translation';
    $('.modeContainer' + parent.location.hash + 'Container').show();
    $('.nav li > a[data-mode=' +  parent.location.hash.substring(1) + ']').parent().addClass('active');

    $('.nav a').click(function () {
        var mode = $(this).data('mode');
        $('.nav li').removeClass('active');
        $(this).parent('li').addClass('active');
        $('.modeContainer:not(#' + mode + 'Container)').hide({ queue: false });
        $('#' + mode + 'Container').show({ queue: false }); 
    });
});

function formatModes(modes) {
    var modesArr = [], toReturn = []
    for(var val in modes)
        modesArr.push(val);
    for(var val in modes) {
        if(val.indexOf('-') === -1)
            toReturn.push([val, val]);
        else {
            var mode = val.split('-')[0];
            if(modesArr.indexOf(mode) === -1)
                toReturn.push([val, mode]);
            else
                toReturn.push([val, mode + ' (' + val + ')']);
        }   
    }
    return toReturn;
}

function getLangByCode(code, localizedLanguageNames) {
    code = code.trim();
    if(localizedLanguageNames[code])
        return localizedLanguageNames[code];
    else
        return code;
}

function getCodeByLang(lang, localizedLanguageCodes) {
    lang = lang.trim();
    if(localizedLanguageCodes[lang])
        return localizedLanguageCodes[lang];
    else
        return lang;
}
