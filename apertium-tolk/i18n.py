import gettext
#import Gtk.glade
import prefix

APP='apertium-tolk'
DIR=prefix.localedir
 
gettext.bindtextdomain(APP, DIR)
gettext.textdomain(APP)
#Gtk.glade.bindtextdomain(APP, DIR)
#Gtk.glade.textdomain(APP)

_ = gettext.gettext # the i18n function :)

