from io import StringIO
##import Gtk.glade
from gi.repository import Gtk
from configparser import DuplicateSectionError, NoSectionError
from tolk.widget_state import dump_state, load_state

default_config = '''
[wndMain]
x_size = 465
y_size = 300
x_pos = 551
y_pos = 26

[vpaned1]
position = 112
'''

class GladeXML(Gtk.Builder):
    def get_widgets(self):
        """Return an iterator which produces (widget_name, widget) pairs.

        glade.XML.get_widget_prefix returns all widgets with names starting with the
        supplied string; so if we call get_widget_prefix(''), we get all the widgets
        in the loaded glade file!
        """
        return ((Gtk.Widget.get_name(w), w) for w in  self.get_objects())

    
    def dump_gtk_state(self, cfg):
        """Enumerate the widgets in the loaded glade file.

        For each widget, create a section in the configuration file represented by 'cfg'.
        Then, write the widget state using the dictionary produced by dump_state to the
        widget's section in the 'cfg' file.
        """
        for widget_name, widget in self.get_widgets():
            try:
                cfg.add_section(widget_name)
            except DuplicateSectionError as e:
                pass

            for key, val in dump_state(widget).items():
                cfg.set(widget_name, key, str(val))


    def load_gtk_state_default(self, cfg):
        cfg_buf = StringIO(default_config)
        cfg.read(cfg_buf)

        for widget_name, widget in self.get_widgets():
            try:
                load_state(widget, dict(cfg.items(widget_name)))
            except KeyError as e:
                pass
            except NoSectionError as e:
                pass


    def load_gtk_state(self, cfg):
        for widget_name, widget in self.get_widgets():
            try:
                load_state(widget, dict(cfg.items(widget_name)))
            except KeyError as e:
                pass
            except NoSectionError as e:
                pass


