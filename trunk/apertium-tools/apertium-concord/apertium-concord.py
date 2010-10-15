""" First go at a concordancer for spectie
Will have a GLADE gui and show concordances for frequencies """

# First import our libs

import sys

try:
    import pygtk
    pygtk.require("2.0")
except:
    pass

try:
    import gtk
    import gtk.glade
except:
    sys.exit(1)
    
NO_CONCS_TO_SHOW = 50 # how many concordances for each frequency to find

# Create the main class that will contain and do everything
class ConcordGTK:
    """ This is our concordancer class, will show UI and respond to input """

    def __init__(self, freqList, sentListFile):
        """ The constructor for this class """
        
        # Set up instance variables
        self.freqList = freqList
        self.sentListFile = sentListFile
        
        # Set the glade file
        self.gladefile = "concordancer.glade"
        self.wTree = gtk.Builder()
        self.wTree.add_from_file(self.gladefile)
        
        # Get a handle on the main window, connect to destroy event
        self.window = self.wTree.get_object("MainWindow")
            
        # Get handle on data store for tree view, populate
        self.listStore = self.wTree.get_object("liststore1")
        if (self.listStore) :
            self.listStore.clear()
            for line in freqList:
                self.listStore.append([line.strip()])
                    
        # Get a handle on the concordances view
        self.concView = self.wTree.get_object("concTextView")
        
        # Now connect our on click signal handler, this is done using a dict
        dic = { "on_freq_clicked" : self.freq_clicked, 
                                    "on_MainWindow_destroy" : gtk.main_quit }
        self.wTree.connect_signals(dic)

            
    def freq_clicked(self, treeview, path, viewcolumn):
        """ Frequency click event handler """
        
        # Find out which frequency word was clicked
        clickedFrequency = self.freqList[path[0]].strip().split()[1]
        
        # Find sentences from our file containing this word
        global NO_CONCS_TO_SHOW
        concList = []
        sentListHandler = open(self.sentListFile)
        
        while(len(concList) < NO_CONCS_TO_SHOW) :
            myLine = sentListHandler.readline()
            
            if len(myLine):
                if clickedFrequency in myLine:
                    concList.append(myLine)
            else:
                # EOL, break the while
                break
                
        sentListHandler.close()
        
        # Update the concordances list accordingly
        self.concView.get_buffer().set_text("")
        for line in concList:
            self.concView.get_buffer().insert_at_cursor(line)  
            

if __name__ == "__main__":
    """ Instantiate above class and kick off main gtk loop 
    Don't forget to get the input files from the command line """
    
    # Sanity check input
    if len(sys.argv) != 3:
        print("Usage: python concordancer.py freqList sentences")
        sys.exit(-1)
        
    # Read frequency list, pass to main class
    # Pass handle to the sentence list as well
    freqListFile = open(sys.argv[1])
    sentList = sys.argv[2]
    freqList = freqListFile.readlines()
    freqListFile.close()
   
    # Instantiate our class, passing data        
    concGTK = ConcordGTK(freqList, sentList)
    gtk.main()
    
   

