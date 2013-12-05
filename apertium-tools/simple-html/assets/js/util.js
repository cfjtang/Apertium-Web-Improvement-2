var abbreviations = {
    'Spanish': 'es',
    'Catalan': 'ca',
    'Catalan (Valencian)': 'ca_valencia',
    'Galician': 'gl',
    'Portuguese': 'pt',
    'Brazilian Portuguese': 'pt_BR',
    'Occitan': 'oc',
    'Aranese': 'oc_aran',
    'English': 'en',
    'French': 'fr',
    'Esperanto': 'eo',
    'Romanian': 'ro',
    'Welsh': 'cy',
    'Basque': 'eu',
    'Breton': 'br',
    'Norwegian Bokmål': 'nb',
    'Norwegian Nynorsk': 'nn',
    'Swedish': 'sv',
    'Danish': 'da',
    'Asturian': 'ast',
    'Icelandic': 'is',
    'Macedonian': 'mk',
    'Bulgarian': 'bg',
    'Italian': 'it',
    'Tatar': 'tat',
    'Kazakh': 'kaz'
}

function ajaxSend() {
    $("#loading-indicator").show(); 
}

function ajaxComplete() { 
    $("#loading-indicator").hide(); 
}

$(document).ajaxSend(ajaxSend);
$(document).ajaxComplete(ajaxComplete);

$(document).ready(function() {
    $('.nav a').click(function () {
        $('.nav li').removeClass('active');
        $(this).parent('li').addClass('active');
        $('.modeContainer:not(#' + $(this).prop('id') + 'Container)').hide({ queue: false });
        $('#' + $(this).prop('id') + 'Container').show({ queue: false }); 
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

function getLangByCode(code) {
    language = code
    //FIXME: currently not able to parse abbreviations
    for (abbv in abbreviations)
        if (abbv == code)
            language = abbreviations[abbv];
    return language;
}
