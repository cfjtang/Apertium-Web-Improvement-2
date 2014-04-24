#!/usr/bin/python
# -*- coding: utf-8 -*-
'''
    Apertium Verb Conjugator
    Copyright (C) 2009  Brendan Molloy

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
'''

import os # Operating system functions
import re # Regular expressions
import gettext # Localisation
import sys # System functions
import gtk # GIMP Toolkit (the GUI)
import ctypes # C Types
import gzip # GNU zip, for compression
import cPickle # Serialisation of objects
import pango # Used for formatting 
import types # Used for checking for blanks NoneType

from time import strftime, localtime # For benchmarking

from xml.sax.handler import ContentHandler # XML parsing modules
from xml.sax import make_parser # XML parser

def frozen():
	'''Checks if file is frozen, for py2exe'''
	return hasattr(sys, "frozen")

def app_path():
	'''Get directory even if frozen by .exe'''
	if frozen():
		return os.path.dirname(sys.executable)
	
	return os.path.dirname(__file__)

APP = 'apertium-verbconj'

if os.name == 'posix':
	import config
	DIR = config.localedir # Generated configuration
	gladedir = config.gladedir
	lib = ctypes.CDLL("libc.so.6")
elif os.name == 'nt':
	gladedir = app_path()
	DIR = os.path.join(app_path(), "po")
	lib = ctypes.cdll.intl

gettext.bindtextdomain(APP, DIR)
gettext.textdomain(APP)
gettext.install(APP,localedir=DIR)
lib.bind_textdomain_codeset(APP, "UTF-8")
lib.bindtextdomain(APP, DIR)

_ = gettext.gettext # For internationalisation

# Standard definitions of verb definitions
sdefs = {
		'vbaux': _('Auxiliary verb'),
		'vblex': _('Verb'),
		'vbmod': _('Modal verb'),
		'vbser': _('Verb (to be)'),
		'vbhaver': _('Verb (to have)'),
		
		'actv': _('Active voice'),
		'pasv': _('Passive voice'),

		'pres': _('Present tense'),
		'pri': _('Present indicative'),
		'prs': _('Present subjunctive'),
		'past': _('Past tense'),
		
		'inf': _('Infinitive'),
		'pp': _('Past participle'),
		'ger': _('Gerund'),
		'imp': _('Imperative'),
		'cni': _('Conditional'),
		'pprs': _('Present participle'),

		'sg': _('Singular'),
		'pl': _('Plural'),
		'sp': _('Singular/Plural'),
		'du': _('Dual'),

		'p1': _('First person'),
		'p2': _('Second person'),
		'p3': _('Third person'),
		
		'gen': _('Genitive'),
		'nom': _('Nominative'),
		'dat': _('Dative'),
		'acc': _('Accusative'),
		'loc': _('Locative'),
		'voc': _('Vocative'),
		'ins': _('Instrumental'),
		
		'ind': _('Indefinite'),
		'def': _('Definite')#,
}

# Definitions of verbs
# TODO: Make more dynamic
verbDefs = ['V', 'TV', 'IV', 'vblex', 'vbser', 'vbhaver', 'vbaux', 'vaux', 
			'vbmod', 'vbper', 'vbsint', 'voice', 'case']


class verbObjects:
	'''Class for collection of dictionaries of paradigms and main words'''
	saveVer = 1
	pardefDict = {}
	mainDict = {}
	citationList = []

class verbHandler(ContentHandler):
	'''Class used for parsing the file using SAX'''
	# Event status verbs 
	isPardefs = False
	isPardef = False
	isSection = False
	isE = False
	isL = False
	isR = False
	isS = False
	isI = False
	isPar = False
	
	# Content between tags
	buildI = ""
	buildL = ""
	buildR = ""
	buildS = []
	buildPardef = ""
	
	direction = "" # Currently unused.
	lemma = "" # Citation word root.
	paradigm = "" # Special parameters of a paradigm.
	
	# On start events of selected tag functions
	# Beginning of tag example: <pardef 
	def startElement(self, tag, attrs):
		# First part of dictionary: paradigm definitions
		if tag == "pardefs":
			self.isPardefs = True

		if tag == "pardef":
			self.buildPardef = attrs.get("n") # Grabs content of pardef
			if self.buildPardef.split("_")[-1] in verbDefs:  # Checks for actual verb
				self.isPardef = True 
				objects.pardefDict[self.buildPardef] = [] # Creates new structure for verbs
			else:
				self.isPardef = False

		if tag == "section" and attrs.get("id") == "main": # Make sure we're looking in the correct section
			self.isSection = True
		
		if tag == "e":
			self.isE = True
			if self.isSection:
				self.lemma = attrs.get("lm") # Grabs root word
			if self.isPardef:
				# CURRENTLY UNUSED, CHECKS DIRECTION OF DICT
				# Disabled to speed up processing
				#self.direction = attrs.get("r")
				pass
		
		if tag == "l":
			self.isL = True
		
		if tag == "r":
			self.isR = True
		
		if tag == "s":
			self.isS = True
			if self.buildPardef.split("_")[-1] in verbDefs:
				self.buildS.append(attrs.get("n")) # Grabs sdefs of verb
		if tag == "i":
			self.isI = True
		
		if tag == "par":
			self.isPar = True
			self.paradigm = attrs.get("n") # Grabs special pieces such as Voice type
	
	# Rips content between tags
	# Example: <i>f</i>, f gets ripped as content
	def characters(self, content):
		if self.isI:
			self.buildI = content
		if self.isL:
			self.buildL = content
		if self.isR:
			self.buildR = content
	
	# Functions run on end event of tags
	# Example of tag: </pardef>
	def endElement(self, tag):
		if tag == "pardefs":
			self.isPardefs = False
		
		if tag == "pardef":
			# Clears variable for clean building of verb forms
			self.buildL = ""
			self.isPardef = False
		
		if tag == "section":
			self.isSection = False
			# This loop adds all dictionary keys to citation list
			for i in objects.mainDict.keys():
				objects.citationList.append(i)
			objects.citationList.sort() # Sorts the list

		if tag == "e":
			if self.isPardef: 
				# Appends paradigm to dictionary
				objects.pardefDict[self.buildPardef].append([self.buildL, self.buildR, self.buildS, self.paradigm])
			
			# Kids, I hope you enjoy nested ifs. This is going to be a rough ride!
			if self.isSection and self.paradigm.split("_")[-1] in verbDefs: # Checks for correct event and that the paradigm exists
				if type(self.lemma) is not types.NoneType: # Stop blank verbs
					if len(self.lemma.split(" ")) is 1: # Stop multiword verbs
						if len(self.lemma.split("'")) is 1: # Stop apostrophes in faulty, non-conforming dictionaries
							objects.mainDict[self.lemma] = [self.paradigm, self.buildI] # Adds dictionary reference
			
			self.isE = False
			
			# Clean complete build variables
			self.buildS = []
			self.buildL = ""
			self.buildR = ""
			self.buildI = ""
		
		# Empty event flags, but event flags nonetheless
		# Required for debugging and future potential
		if tag == "l":
			self.isL = False
		
		if tag == "r":
			self.isR = False
		
		if tag == "s":
			self.isS = False
		
		if tag == "i":
			self.isI = False
		
		if tag == "par":
			self.isPar = False


class userInterface:
		'''Class that defines the GTK user interface'''
		def pickle_export(self, dumpfile):
			dumpto = open(dumpfile, 'w') # open file handle
			# Below takes all objects and adds to a tuple to store in one file
			temp = (objects.saveVer, objects.pardefDict, objects.mainDict, objects.citationList)
			cPickle.dump(temp, dumpto) # Dump tuple to file
			dumpto.close() # Close file handle
			print _('File dumped successfully.') # Debug
		
		def pickle_import(self, dumpfile):
			temp = cPickle.load(dumpfile) # Dumps tuple from file to variable
			if temp[0] == 1: # File version check in case of spec change
				objects.pardefDict = temp[1] # All dictionaries reassigned
				objects.mainDict = temp[2]
				objects.citationList = temp[3]
			dumpfile.close() # Close handle

		def pango_set_tags(self, buffer):  
			"""Sets tags in a similar manner to HTML"""
			buffer.create_tag("heading", weight=pango.WEIGHT_BOLD, size=15 * pango.SCALE)
			buffer.create_tag("bold", weight=pango.WEIGHT_BOLD)
			buffer.create_tag("grey", foreground="grey")
			buffer.create_tag("center", justification=gtk.JUSTIFY_CENTER)
		
		def pango_conjugate_verb(self, verb):
			'''Function to check main dictionary verb conjugations'''
			iter = self.textbuffer.get_iter_at_offset(0) # Create iterator for iterating output
			pverb = objects.mainDict[verb][0] # Verb to reference pardefDict with
			thepardef = objects.mainDict[verb][0].replace('/', '').split('_')[0] # The inflection rule verb
			self.textbuffer.insert_with_tags_by_name(iter, "%s\n" % verb, "heading", "center") # Insert heading verb name
			if verb != thepardef:
				self.textbuffer.insert_with_tags_by_name(iter, _("Inflects like %s\n") % thepardef,  "center") # If the verb inflects like another one, display which verb
			
			for i in range(len(objects.pardefDict[pverb])):
				# Takes the stem of the verb, and adds conjugates
				left = objects.mainDict[verb][1]
				final = left + objects.pardefDict[pverb][i][0]
				self.textbuffer.insert_with_tags_by_name(iter, "%s: \n " % final, "bold") # Displays conjugate
				
				pardefLength = len(objects.pardefDict[pverb][i][2]) # Length of sdefs in pardef
				for j in range(pardefLength):
					x = objects.pardefDict[pverb][i][2][j] # temporary variable used for manipulation
					if x in sdefs:
						self.textbuffer.insert(iter, sdefs[x]) # If a better version of the sdef code is found in the sdefs dictionary, that will be displayed
					else:
						self.textbuffer.insert_with_tags_by_name(iter, x, "grey") # Otherwise, it will be displayed as is, in grey
					
					if j != (pardefLength - 1):
						self.textbuffer.insert(iter, ", ") # This stops a trailing comma
						
				self.textbuffer.insert(iter, "\n") # This creates a new line after every conjugation

		def show_about_dialog(self, widget, data=None):
			"""Shows the about dialog"""
			response = self.aboutbox.run()
			if response:
				self.aboutbox.hide()

		def on_window_destroy(self, widget, data=None):
			"""Cleans up window states before quitting"""
			self.window.hide()
			gtk.main_quit()

		def on_export_clicked(self, widget, data=None):
			"""Exports dictionary as strdix"""
			self.exportdialog = gtk.FileChooserDialog(_("Export..."), 
								None, 
								gtk.FILE_CHOOSER_ACTION_SAVE,
								(gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL,
								gtk.STOCK_SAVE, gtk.RESPONSE_OK))
			filter = gtk.FileFilter()
			filter.set_name(_("String Dictionary")) # Sets filter name
			filter.add_pattern("*.strdix") # Sets filter mask
			
			self.exportdialog.add_filter(filter) # Adds filter to dialog object
			
			response = self.exportdialog.run() # Run dialog
			if response == gtk.RESPONSE_OK: # If OK clicked
				filename = self.exportdialog.get_filename() # Gets selected file
				if filename[-7:] != ".strdix": # If string dictionary ending missing
					filename += ".strdix" # add it.
				self.pickle_export(filename) # Exports it as string dictionary
			self.exportdialog.destroy() # Destroys dialog

		def show_preferences_dialog(self, widget=None, data=None):
			dialog = gtk.MessageDialog(self.window, gtk.DIALOG_DESTROY_WITH_PARENT, gtk.MESSAGE_INFO, gtk.BUTTONS_OK)
			dialog.set_markup(_('Preferences to be implemented.'))
			response = dialog.run()
			if response:
				dialog.destroy()

		def show_usage_dialog(self, widget=None, data=None):
			"""Shows help dialog with usage instructions"""
			dialog = gtk.MessageDialog(self.window, gtk.DIALOG_DESTROY_WITH_PARENT, gtk.MESSAGE_INFO, gtk.BUTTONS_OK) 
			# Distorted tabbing required for formatting reasons
			dialog.set_markup(_('''
<span size="x-large"><b>Help</b></span>\n
<big>To load a dictionary:</big>
Click File→Open. Choose a .dix, .dix.gz or .strdix file.\n\n
<big>To conjugate a verb:</big>
Select a citation word from the citation list and click Conjugate. Alternatively, you may double click the citation word.\n\n
<big>To export a dictionary as a string dictionary:</big>
You may want to do this if your dictionary takes quite a while to parse. Open the dictionary, then select File→Export. Name your dictionary and press Save.\n\n
'''))
			response = dialog.run()
			if response:
				dialog.destroy()

		def on_open_clicked(self, widget, data=None):
			"""Loads the dictionary for use"""
			self.opendialog = gtk.FileChooserDialog(_("Open..."),
								None,
								gtk.FILE_CHOOSER_ACTION_OPEN,
								(gtk.STOCK_CANCEL, gtk.RESPONSE_CANCEL,
								gtk.STOCK_OPEN, gtk.RESPONSE_OK))
			filter = gtk.FileFilter() # Creates filter object
			filter.set_name("Apertium dictionaries") # Sets display name of filter in open dialog
			filter.add_pattern("*.dix") # Sets filter for .dix
			filter.add_pattern("*.dix.gz") # Sets filter for .dix.gz
			filter.add_pattern("*.strdix") # Sets filter for .strdix
			filter2 = gtk.FileFilter() # Creates a second filter object
			filter2.set_name("All files") # Sets display name of filter in open dialog
			filter2.add_pattern("*") # Sets filter to all files
			
			self.opendialog.add_filter(filter) # Adds filter to dialog
			self.opendialog.add_filter(filter2)

			response = self.opendialog.run() # Runs dialog code and checks for response code
			if response == gtk.RESPONSE_OK: # OK clicked
				filename = self.opendialog.get_filename() # Gets selected file
				result = self.valid_file(filename) # Checks for result of file loading
				if result is 0: # If failure
					print _("Error: not a dictionary file")
					dialog = gtk.MessageDialog(self.window, 
									gtk.DIALOG_DESTROY_WITH_PARENT,
									gtk.MESSAGE_ERROR, gtk.BUTTONS_CLOSE, 
									_("Not a dictionary file."))
					response = dialog.run()
					if response:
						dialog.destroy()
				
				self.opendialog.destroy()
				if result is 1: # If normal dictionary
					self.load_standard_file(self.filehandle)
					self.generate_liststore()

				if result is 2: # If string dictionary
					self.load_strdix_file(self.filehandle)
					self.generate_liststore()

			elif response == gtk.RESPONSE_CANCEL: # If cancel clicked
				self.opendialog.destroy()

		def valid_file(self, x):
			"""Checks a file for validity"""
			if x[-3:] == ".gz":
				self.filehandle = gzip.open(x) # open compressed file
				return 1
			elif x[-4:] == ".dix":
				self.filehandle = open(x) # Open uncompressed file
				return 1
			elif x[-7:] == ".strdix":
				self.filehandle = open(x) # Open string dictionary
				return 2
			else:
				return 0 # Fail.


		def load_standard_file(self, x):
			"""Loads a file."""
			self.build_buffers() # Clears variables to avoid conflicts
			processBegin = _("Processing begins: %s") % strftime(_("%a, %d %b %Y %H:%M:%S %z"), localtime())
			parser.parse(x) # Parses the file with the verbHandler class
			x.close() # Closes file handle
			processEnd = _("Processing ends: %s") % strftime(_("%a, %d %b %Y %H:%M:%S %z"), localtime())
			print processBegin # Only seen when run from the command line, for debugging purposes
			print processEnd


		def load_strdix_file(self, x):
			self.build_buffers() # Clears build variables
			processBegin = _("Processing begins: %s") % strftime("%a, %d %b %Y %H:%M:%S %z", localtime())
			self.pickle_import(x) # Loads string dictionary objects
			processEnd = _("Processing ends: %s") % strftime("%a, %d %b %Y %H:%M:%S %z", localtime())
			print processBegin
			print processEnd

		def generate_liststore(self):
			"""Generates liststore for viewing citation list in GUI"""
			self.citationList = objects.mainDict.keys() # Clone mainDict to make citationList 
			self.citationList.sort() # Sort list
			
			for i in self.citationList:
					self.liststore.append([i]) # Adds all verbs to GUI list
			self.treeview.set_model(self.liststore)
			
			# Finds amount of citation words and puts it in statusbar
			context_id = self.statusbar.get_context_id(_("Status"))
			msg_id = self.statusbar.push(context_id, _("Citation words: %s") % len(self.liststore))

		def conjugate_button_clicked(self, button=None):
			self.on_citationword_click() # Required to workaround inbuilt GTK parameter requirements

		def on_citationword_click(self, treeview=None, path=None, view_column=None):
			"""Function that gets a selection from the verb list"""
			self.wordSelected = self.treeselection.get_selected()[1] # Find citation word that's selected
			value = self.liststore.get_value(self.wordSelected, 0).decode('UTF-8') # 0 = first and only column, .decode fixes output 
			self.textbuffer.set_text("") # Erases any text in buffer
			self.pango_conjugate_verb(value) # Formats output and conjugates verb

		def build_buffers(self):
			'''Cleans up variables and rebuilds objects'''
			self.liststore.clear()
			objects.pardefDict.clear()
			objects.mainDict.clear()
			del objects.citationList[:]

		def __init__(self):
			'''Welcome to the initialiser. Prepare for a rough ride.'''
			builder = gtk.Builder()
			builder.set_translation_domain(APP)
			builder.add_from_file(os.path.join(gladedir, "verbconj.glade"))
			
			# INITIALISATION #
			# These are prebuilt element in the .glade file
			self.window = builder.get_object("window") # Creates main window
			self.window.set_title(_("Apertium Verb Conjugator"))
			self.window.set_default_size(450, 450) # Force window to not be little and insignificant
			self.window.set_icon_from_file(os.path.join(gladedir, "apertium-logo.png")) # makes the icon work with awesome relative functions
			# Below assigns window elements from the glade file
			self.aboutbox = builder.get_object("aboutdialog")
			self.treeview = builder.get_object("treeview")
			self.statusbar = builder.get_object("statusbar")
			self.textview = builder.get_object("textview")
			
			# These are elements being generated here
			self.liststore = gtk.ListStore(str) # creates list store
			self.textbuffer = gtk.TextBuffer() # creates text buffer
			self.pango_set_tags(self.textbuffer) # Sets the Pango formatting tags for the text buffer
			self.treeview.set_enable_search(True) # enables searching the list
			self.treeview.set_model(self.liststore) # sets the list store to be used
			self.textview.set_buffer(self.textbuffer) # sets the text buffer to be used
			self.treeselection = self.treeview.get_selection() # enables selections
			self.treeselection.set_mode(gtk.SELECTION_SINGLE) # only allows a single word to be selected
			self.tvcolumn = gtk.TreeViewColumn(_("Citation Words")) # sets title of list to Citation Words
			self.treeview.append_column(self.tvcolumn) # shows the column data
			self.cell = gtk.CellRendererText() # sets the cell renderer
			self.tvcolumn.pack_start(self.cell, True) # starts to fill the cell
			self.tvcolumn.add_attribute(self.cell, 'text', 0) # sets attributes of the cell to text
			
			builder.connect_signals(self) # builds the GUI


if __name__ == "__main__":
	objects = verbObjects() # Quick, have some objects
	parser = make_parser() # Creates SAX XML parser object
	parser.setContentHandler(verbHandler()) # Using verbHandler class, parses through the XML
	gui = userInterface() # Creates instance of user interface
	gui.window.show() # Shows user interface
	gtk.main() # Begins event-loop

else:
	raw_input(_("Press ENTER.")) # This is displayed if this file is imported as a module. You can't.
