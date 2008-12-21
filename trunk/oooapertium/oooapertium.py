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

# FILE:   	oooapertium.py
# Version: 	0.3
# DESCRIPTION: Translate the selected removing the original text

import uno
import unohelper
import string
import dbus
import sys
import os
import codecs
import tempfile
import time

from com.sun.star.beans import PropertyValue

from com.sun.star.task import XJobExecutor

from com.sun.star.datatransfer import XTransferableSupplier

from com.sun.star.awt import XActionListener
from com.sun.star.awt import XTextListener
from com.sun.star.awt import XItemListener
from com.sun.star.awt import XWindowListener
from com.sun.star.awt import XMouseListener
from com.sun.star.awt import FontDescriptor 

class Apertium( unohelper.Base, XJobExecutor ):
    def __init__( self, ctx ):
        self.ctx = ctx

    ####################################
    # get the mode from the configfile
    ####################################
    def modeName(self):
        cfg=self.loadconfigfile()
        return cfg['modename']
    
    ####################################
    # load the configuration file 
    ####################################
    def loadconfigfile(self):
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

    def markUnknown(self):
    	cfg=self.loadconfigfile()
	return cfg['mark_unknown']

    #####################################################################
    # create a new desktop/document/controller/viewcursor
    # Stolen from:
    #	http://api.openoffice.org/servlets/ReadMsg?list=dev&msgNo=13641
    # License: BSD
    #####################################################################
    def getDesktopDict(self, ctx ):

		desktop = ctx.ServiceManager.createInstanceWithContext( \
		    "com.sun.star.frame.Desktop", ctx )

		#Get the current controller and viewCursor

		document = desktop.getCurrentComponent()
		controller = document.getCurrentController()
		viewCursor = controller.getViewCursor()

		document.RecordChanges = False

		return { 'desktop'    : desktop,
			 'document'  : document,
			 'controller' : controller,
			 'viewcursor' : viewCursor }
   
    ######################################################################
    # Paste to a new Document the contents that has been cut before
    ######################################################################
    def pasteToNewDoc( self, deskDict ):

        document = deskDict['document']
        self.executeSlot( self.ctx, deskDict['controller'], ".uno:Paste")

		 
    def createNewDocument(self, deskDict, url="private:factory/swriter" ):

        """
        loadProps = uno.createUnoStruct(
        	"com.sun.star.beans.PropertyValue" )
        loadProps.Name = "Hidden"
        loadProps.Value = True
        """
	
	#inProps = (PropertyValue('Hidden', 0, True, 0),)
	inProps=()
#        newDocument = deskDict['desktop'].loadComponentFromURL( \
#                        url, "_blank", 0, () )
        newDocument = deskDict['desktop'].loadComponentFromURL( \
                        url, "_blank", 0, inProps )
        controller = newDocument.getCurrentController()
        viewCursor = controller.getViewCursor()

        return  { 'document' : newDocument,
                'controller' : controller,
                'viewcursor' : viewCursor,
                'desktop'    : deskDict['desktop']
                }

    def executeSlot(self, ctx, controller, islot ):

        dispatchHelper = ctx.ServiceManager.createInstanceWithContext( 
                "com.sun.star.frame.DispatchHelper", ctx )
        frame = controller.getFrame()

        dispatchHelper.executeDispatch( frame, islot, "", 0, () )


    ####################################################################
    # main procedure. 
    ####################################################################
    def trigger( self, args ):
        # get the mode 
	mode_name=self.modeName()
	# get the actual context
 	deskDict = self.getDesktopDict(self.ctx)
	viewCursor=deskDict['viewcursor']
	textDict=deskDict['document'].Text
	controller = deskDict['controller']
	endRange= textDict.getEnd()
	
	textCursor=textDict.createTextCursorByRange(viewCursor.getEnd())
	
	# get the selected text (used to assure there are anything selected)
	sel=deskDict['document'].getCurrentSelection()
	selectedText=sel.getByIndex(0).getString()
	if (len(selectedText)<=0):
		textCursor.gotoPreviousParagraph(False)
		textCursor.gotoEndOfParagraph(False)
		textCursor.gotoNextParagraph(False)
		viewCursor.gotoRange(textCursor,False)
		textCursor.gotoEndOfParagraph(True)
		viewCursor.gotoRange(textCursor,True)

		sel=deskDict['document'].getCurrentSelection()
		selectedText=sel.getByIndex(0).getString()

	# only do all work if there are something selected
	if (len(selectedText)>0):
		selection=controller.getSelection() 
		#controller.select( controller.getSelection() )
		controller.select( selection )
		
		# cut the selected text
		self.executeSlot( self.ctx, controller,  ".uno:Cut")
		
		# create a new Document
		docDict = self.createNewDocument( deskDict )

		# Paste the code into
		self.pasteToNewDoc( docDict )

		# Unselect  and move the cursor to end of selection
		controller.getFrame().getComponentWindow().setFocus( )
		textCursor.gotoRange( viewCursor.getEnd() , False)
		viewCursor.gotoRange( textCursor, False )
		
		# create a temp file to store the selected text
		outFile= os.path.join(tempfile.gettempdir() ,str(time.time()) + 'apertiumtmp.odt' )
		url = unohelper.systemPathToFileUrl(outFile )
		doc=docDict['document']
		doc.storeToURL(url,())
		
		# close the window
		doc.dispose()

		# create a temp file to store the translated text
		translDoc=os.path.join(tempfile.gettempdir() ,str(time.time()) + 'apertiumtmp.odt' )

		# prepare the translate command and exec it.
		mark=''
		if (self.markUnknown() != 1):
			mark=' -u '
		apertiumCommand="/usr/bin/apertium -f odt "+mark + mode_name + " " + outFile+" "+translDoc
		os.system(apertiumCommand)
		
		# insert the translated text into original document
		urltranslDoc=unohelper.systemPathToFileUrl(translDoc)
		textCursor.insertDocumentFromURL(urltranslDoc,())
		
		os.unlink(outFile)
		os.unlink(translDoc)



g_ImplementationHelper = unohelper.ImplementationHelper()
g_ImplementationHelper.addImplementation(
        Apertium,
        "es.ua.dlsi.xixona.apertium.transel",
        ("es.ua.apertium.transel",),)
