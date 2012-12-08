import gettext
import locale
import tolk.prefix as prefix

APP='apertium-tolk'
DIR=prefix.localedir

locale.setlocale(locale.LC_ALL, '')
locale.bindtextdomain(APP, DIR)
locale.textdomain(APP)
gettext.bindtextdomain(APP, DIR)
gettext.textdomain(APP)

_ = gettext.gettext # the i18n function :)

