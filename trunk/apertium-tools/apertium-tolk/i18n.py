import gettext
import gtk.glade

APP='apertium-tolk'
DIR='po'
 
gettext.bindtextdomain(APP, DIR)
gettext.textdomain(APP)
gtk.glade.bindtextdomain(APP, DIR)
gtk.glade.textdomain(APP)

_ = gettext.gettext # the i18n function :)

