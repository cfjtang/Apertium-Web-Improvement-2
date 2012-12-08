from gi.repository import Gtk
from tolk.simplegeneric import generic

    
@generic
def dump_state(widget):
    raise NotImplementedError

@generic
def load_state(widget, dct):
    raise NotImplementedError


@dump_state.when_type(Gtk.Widget)
def widget_dump_state(widget):
    return {}

@load_state.when_type(Gtk.Widget)
def widget_load_state(widget, dct):
    pass


@dump_state.when_type(Gtk.Window)
def window_dump_state(w):
    return { "x_size": w.get_size()[0],
             "y_size": w.get_size()[1],
             "x_pos":  w.get_position()[0], 
             "y_pos":  w.get_position()[1] }

@load_state.when_type(Gtk.Window)
def window_load_state(w, p):
    w.move(int(p["x_pos"]), int(p["y_pos"]))
    w.resize(int(p["x_size"]), int(p["y_size"]))


@dump_state.when_type(Gtk.Paned)
def paned_dump_state(w):
    return { "position": w.get_position() }

@load_state.when_type(Gtk.Paned)
def paned_load_state(w, p):
    w.set_position(int(p["position"]))

