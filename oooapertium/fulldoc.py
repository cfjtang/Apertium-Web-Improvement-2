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

# FILE:   	fulldoc.py
# Version: 	0.3
# DESCRIPTION: Translate all document removing the original text

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

class TranslateFullDoc( unohelper.Base, XJobExecutor ):
    def __init__( self, ctx ):
        self.ctx = ctx
 	self.smgr = self.ctx.ServiceManager
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
   
    ####################################################################
    # main procedure. 
    ####################################################################
    def trigger( self, args ):
	# get the actual context
 	deskDict = self.getDesktopDict(self.ctx)
	viewCursor=deskDict['viewcursor']
	# Go to start of document without select
	viewCursor.gotoStart(False)
	#go to endo of document selecting range from start
	viewCursor.gotoEnd(True)
	#Translate Selection
	ap=self.smgr.createInstanceWithContext("es.ua.apertium.transel", self.ctx)
	ap.trigger("execute");


g_ImplementationHelper = unohelper.ImplementationHelper()
g_ImplementationHelper.addImplementation(
        TranslateFullDoc,
        "es.ua.dlsi.xixona.apertium.tranfulldoc",
        ("es.ua.apertium.tranfulldoc",),)
