###########################################################################
# Copyright (c) 2008 Miguel Gea Milvaques <xerakko@debian.org>
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#########################################################################

# FILE:         busmodes.py
# Version:      0.2
# DESCRIPTION:  Show a window to select the langs origin and destination

import uno
import unohelper
import string
import dbus
import sys
import os
import codecs

from com.sun.star.task import XJobExecutor
from com.sun.star.awt import XActionListener

import time
from com.sun.star.awt import XTextListener
from com.sun.star.awt import XItemListener
from com.sun.star.awt import XWindowListener
from com.sun.star.awt import XMouseListener
from com.sun.star.awt import FontDescriptor 

def getOOoSetupValue(sNodePath, sValue, context):
        sProvider = "com.sun.star.configuration.ConfigurationProvider"
        sAccess   = "com.sun.star.configuration.ConfigurationAccess"
        smgr = context.ServiceManager
        aConfigProvider = smgr.createInstance(sProvider)
        prop = uno.createUnoStruct('com.sun.star.beans.PropertyValue')
        prop.Name = "nodepath"
        prop.Value = sNodePath
        aSettings = aConfigProvider.createInstanceWithArguments(sAccess,(prop,))
        return aSettings.getByName(sValue)
 
def _(ctx,msg):
	locale = uno.getClass("com.sun.star.lang.Locale")()
        # Linguist determines the language from til UI locale.
        aLocale = getOOoSetupValue("/org.openoffice.Setup/L10N", "ooLocale", ctx)

	clocal=aLocale[:2]
	# language list
	langlist=("ca","es","pl","fr","gl","pt","eu","hr","tg","fa","sr")
	# Translations. Add here new strings for translate
        trad={'Select mode:':{'ca':'Seleccioneu el mode:',
			'es':'Seleccione el modo:',
			'pl':'Wybierz sposób',
			'fr':'Sélectionnez un mode',
			'gl':'Seleccionar modo',
			'pt':'Seleccionar modo',
			'eu':'Aukeratu modua:',
			'hr':'Odaberi način:',
			'tg':'Гузидани ҳолат',
			'fa':'گﺯیﺪﻧ ﺡﺎﻠﺗ:',
			'sr':'Odaberi način:'},
              'Save':{'ca':'Desa',
	      		'es':'Guarda',
			'pl':'Zapisz',
	      		'fr':'Sauvegarder',
			'gl':'Guardar',
			'pt':'Guardar',
			'eu':'Gorde',
			'hr':'Spremi',
			'tg':'Захира кун',
			'fa':'ﺬﺧیﺮﻫ کﻥ',
			'sr':'Spremi'},
	      'Mark unknown words':{'ca':'Marca les paraules desconegudes',
	      		'es':'Marca las palabras desconocidas',
			'pl':'Oznacz nieznane słowa',
			'fr':'Marque mots inconnus',
			'gl':'Marcar as palabras decoñecidas',
			'pt':'Marcar as palavras desconhecidas',
			'eu':'Markatu hitz ezezagunak',
			'hr':'Označi nepoznate riječi',
			'tg':'луғатҳои ношинос ро аломат безан',
			'fa':' ﻭﺍژﻩ<200c>ﻫﺍی ﻥﺍﺂﺸﻧﺍ ﺭﺍ ﻉﻼﻤﺗ ﺏﺰﻧ',
			'sr':'Označi nepoznate riječi'}}
	msgstr=trad[msg]
	if clocal not in langlist:
		retmsg=msg
	else:
		retmsg=msgstr[clocal]
        return retmsg

#Window Constants
# specifies that the window is initially visible.
com_sun_star_awt_WindowAttribute_SHOW        = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.SHOW" )
# specifies that the window fills the complete desktop area.
com_sun_star_awt_WindowAttribute_FULLSIZE    = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.FULLSIZE" )
com_sun_star_awt_WindowAttribute_OPTIMUMSIZE = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.OPTIMUMSIZE" )
com_sun_star_awt_WindowAttribute_MINSIZE     = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.MINSIZE" )
# specifies that the window has visible borders.
com_sun_star_awt_WindowAttribute_BORDER      = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.BORDER" )
# specifies that the size of the window can be changed by the user.
com_sun_star_awt_WindowAttribute_SIZEABLE    = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.SIZEABLE" )
# specifies that the window can be moved by the user.
com_sun_star_awt_WindowAttribute_MOVEABLE    = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.MOVEABLE" )
# specifies that the window can be closed by the user.
com_sun_star_awt_WindowAttribute_CLOSEABLE   = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.CLOSEABLE" )
#[ DEPRECATED ] specifies that the window should support the XSystemDependentWindowPeer interface.
com_sun_star_awt_WindowAttribute_SYSTEMDEPENDENT = uno.getConstantByName( "com.sun.star.awt.WindowAttribute.SYSTEMDEPENDENT" )

#Other Required Constants
com_sun_star_awt_possize = uno.getConstantByName( "com.sun.star.awt.PosSize.POSSIZE" )
com_sun_star_awt_InvalidateStyle_Update = uno.getConstantByName( "com.sun.star.awt.InvalidateStyle.UPDATE" )
def loadconfigfile():
        # Load config from ~/.apertium/oooapertium if exists, else return error
        configFile=[os.path.expanduser('~/.apertium/oooapertium')]
        apertiumHomePath=os.path.expanduser('~/.apertium')
	
        if not os.path.exists(apertiumHomePath):
            os.mkdir(apertiumHomePath)
        
	configFile=os.path.abspath(apertiumHomePath+'/oooapertium')
        
	# if the config file doesn't return an error
	if not os.path.exists(configFile):
            return 'pairerror'
        fileDes=file(os.path.abspath(configFile),'rb')
        cfg=eval(fileDes.read())
        return cfg 	



# Listener Objects
class CheckBoxListener(unohelper.Base, XActionListener):
    def __init__(self,checkbox_control):
    	self.checkbox_control = checkbox_control
	cfg=loadconfigfile()
	self.checkbox_control.setState(cfg['mark_unknown'])

    ####################################
    # load the configuration file 
    ####################################
      
class ListItemListener( unohelper.Base, XItemListener ):
    def __init__(self, window, list_control, fixed_text_model, text, cursor ):
        self.text = text
        self.cursor = cursor
        self.list_control = list_control
        self.fixed_text_model = fixed_text_model
        self.window = window
	cfg=loadconfigfile()
	self.fixed_text_model.Label=cfg['modename']
    def itemStateChanged(self, itemEvent):
        # Update the label when an item in the list is selected
        self.fixed_text_model.Label = self.list_control.getSelectedItem().replace('|', ' | ')
        self.window.invalidate(com_sun_star_awt_InvalidateStyle_Update)
       

class InsertButtonListener( unohelper.Base, XActionListener ):
    def __init__(self, list_control, checkbox_control, desktop ):
        self.list_control = list_control
        self.desktop = desktop
	self.checkbox_control = checkbox_control

    def actionPerformed(self, actionEvent):
        global modos
        pos = self.list_control.getSelectedItemPos()
	cb=self.checkbox_control
	mod=modos[pos]
	cfg=loadconfigfile()
	if (pos == -1):
		mod=cfg['modename']

        text = "{'modename':'"+mod+"','mark_unknown':"+str(cb.getState())+"}"
        self.saveconfig(text)  
        global finestra
        finestra.setVisible(0)

    def saveconfig(self,selection):
        apertiumHomePath=os.path.expanduser('~/.apertium')
        if not os.path.exists(apertiumHomePath):
          os.mkdir(apertiumHomePath)
        configFile=os.path.abspath(apertiumHomePath+'/oooapertium')
        fileDes=file(os.path.abspath(configFile),'wb')
        fileDes.write(selection)


# 'translated' from the developer's guide chapter 11.6
class CreateWindow(unohelper.Base, XJobExecutor):
 def __init__(self,ctx):
 	self.ctx=ctx
 def trigger(self,args):
    ctx = uno.getComponentContext()
   
    smgr = ctx.ServiceManager
    toolkit = smgr.createInstanceWithContext("com.sun.star.awt.Toolkit", ctx);
   
    desktop = smgr.createInstanceWithContext( "com.sun.star.frame.Desktop",ctx)
    # access the current writer document
    model = desktop.getCurrentComponent()
    text = model.Text
    cursor = text.createTextCursor()
    oCoreReflection = smgr.createInstanceWithContext( "com.sun.star.reflection.CoreReflection", ctx )
    oXIdlClass = oCoreReflection.forName( "com.sun.star.awt.WindowDescriptor" )
    oReturnValue, oWindowDesc = oXIdlClass.createObject( None )
    oWindowDesc.Type = uno.getConstantByName( "com.sun.star.awt.WindowClass.TOP" )
    oWindowDesc.WindowServiceName = ""
    oWindowDesc.Parent = toolkit.getDesktopWindow()
    oWindowDesc.ParentIndex = -1
    gnDefaultWindowAttributes = \
        com_sun_star_awt_WindowAttribute_SHOW + \
        com_sun_star_awt_WindowAttribute_BORDER + \
        com_sun_star_awt_WindowAttribute_MOVEABLE + \
        com_sun_star_awt_WindowAttribute_CLOSEABLE 
        #com_sun_star_awt_WindowAttribute_SIZEABLE
    oXIdlClass = oCoreReflection.forName( "com.sun.star.awt.Rectangle" )
    oReturnValue, oRect = oXIdlClass.createObject( None )
    oRect.X = 100
    oRect.Y = 200
    oRect.Width = 302
    oRect.Height = 420
    oWindowDesc.Bounds = oRect
    oWindowDesc.WindowAttributes = gnDefaultWindowAttributes
    oWindow = toolkit.createWindow(oWindowDesc)
    oFrame = smgr.createInstanceWithContext( "com.sun.star.frame.Frame", ctx )
    oFrame.initialize( oWindow )
    oFrame.setCreator( desktop )
    oFrame.activate()
    cont = smgr.createInstanceWithContext(
       "com.sun.star.awt.UnoControlContainer",ctx)
    cont_model = smgr.createInstanceWithContext(
       "com.sun.star.awt.UnoControlContainerModel",ctx)
    cont_model.BackgroundColor = -1
    cont.setModel(cont_model)
    cont.createPeer(toolkit,oWindow)
    cont.setPosSize(0,0,500,400,com_sun_star_awt_possize)
    oFrame.setComponent(cont,None)

    fixed_text_control2, fixed_text_model2=self.createControl(
            smgr,ctx,"FixedText",5,5,300,28,
            ('Name','Label'),('current_item_label',_(ctx,'Select mode:')) )

    fixed_text_control, fixed_text_model = self.createControl(
            smgr,ctx,"FixedText",5,40,300,28,
            ('Name','Label'),('current_item_label','') )
    font_descriptor = FontDescriptor()
    font_descriptor.Name = 'Verdana'
    font_descriptor.Height = 12
    font_descriptor.Width = 8
    font_descriptor.Weight = 150
    font_descriptor.Kerning = True
    fixed_text_model.FontDescriptor = font_descriptor
    fixed_text_model2.FontDescriptor = font_descriptor
    list_control, list_model = self.createControl(
       smgr,ctx,"ListBox",1,68,300,25,
       (),() )
    #list_control.setMultipleMode(False)
    list_model.Dropdown=True
    insert_button_control, insert_button_model = self.createControl(
       smgr,ctx,"Button",120,376,60,30,
       ('Label',),(_(ctx,'Save'),) )
    checkbox_control,checkbox_model=self.createControl(
       smgr,ctx,"CheckBox",10,120,300,25,
       (),() )
    checkbox_control.setLabel(_(ctx,"Mark unknown words"))
    bus = dbus.SessionBus()
    dbi = dbus.Interface(bus.get_object("org.apertium.info", "/"), "org.apertium.Info")
    index_list=dbi.modes()
    global modos
    modos=sorted(index_list)
    #index_list.append("testo")
    list_control.addItems(tuple(sorted(index_list)), 0)
    list_control.addItemListener( ListItemListener(oWindow, list_control, fixed_text_model, text, cursor) )   
    insert_button_control.addActionListener( InsertButtonListener(list_control,checkbox_control, desktop) )
    checkbox_control.addActionListener(CheckBoxListener(checkbox_control))
    
    cont.addControl("list_control",list_control)
    cont.addControl("fixed_text_control",fixed_text_control)
    cont.addControl("fixed_text_control2",fixed_text_control2)
    cont.addControl("insert_button_control", insert_button_control)
    cont.addControl("checkbox_control",checkbox_control)
    global finestra
    finestra=oWindow
    oWindow.setVisible(True)

 def createControl(self,smgr,ctx,type,x,y,width,height,names,values):
   ctrl = smgr.createInstanceWithContext(
      "com.sun.star.awt.UnoControl%s" % type,ctx)
   ctrl_model = smgr.createInstanceWithContext(
      "com.sun.star.awt.UnoControl%sModel" % type,ctx)
   ctrl_model.setPropertyValues(names,values)
   ctrl.setModel(ctrl_model)
   ctrl.setPosSize(x,y,width,height,com_sun_star_awt_possize)
   return (ctrl, ctrl_model)

g_ImplementationHelper = unohelper.ImplementationHelper()
g_ImplementationHelper.addImplementation(
        CreateWindow,
        "es.ua.dlsi.xixona.apertium.modes",
        ("es.ua.apertium.modes",),)
