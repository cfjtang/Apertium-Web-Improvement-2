""" First go at a concordancer for spectie
Will have a GLADE gui and show concordances for frequencies """

# First import our libs

import sys, pango, re, os

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
    
MAX_CONCORDANCES = 100 # how many concordances for each frequency to find
WINDOW_CHARS = 40 # how many characters of context for the concordance ... should be replaced with words -FMT

exactMatch = False;

# Create the main class that will contain and do everything
class ConcordGTK:
    """ This is our concordancer class, will show UI and respond to input """

    def __init__(self, freqList, sentListFile):
        """ The constructor for this class """
        
        # Set up instance variables
        self.freqList = freqList
        self.sentListFile = sentListFile
        
        # Set the glade file
        self.gladefile = "apertium-concord.glade"
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

        fontdesc = pango.FontDescription("Monospace 10")
        self.concView.modify_font(fontdesc)

        self.corpusNameLabel = self.wTree.get_object("label_corpus_name");
        self.corpusNameLabel.set_text(os.path.basename(sentListFile)); 

        self.numTokensLabel = self.wTree.get_object("label_num_tokens");
        self.sentListNumTokens = self.calc_num_tokens(sentListFile);
        self.numTokensLabel.set_text('Tokens: ' + str(self.sentListNumTokens)); 
        
        # Now connect our on click signal handler, this is done using a dict
        dic = { "on_freq_clicked" : self.freq_clicked,
                "on_exact_match_check_button_toggled" : self.exact_match_check,
                "on_search_box_changed" : self.search_box_update,
                "on_MainWindow_destroy" : gtk.main_quit }
                
        self.wTree.connect_signals(dic)

    def calc_num_tokens(self, sentFileName): 
        # This almost certainly wants to be more efficient
        return len(file(sentFileName).read().split(' '));

    def search_box_update(self, box): 

        print 'Search box updated';

    def exact_match_check(self, treeview): 
        # This should also update the concordance window automagically
        global exactMatch;
        
        if exactMatch == False: 
            exactMatch = True;
        else: 
            exactMatch = False;


    def process_line(self, line, token, exactMatch): 
        global WINDOW_CHARS;
        formattedLine = '';
        line = line.decode('utf-8');

        # Do this with a regular expression allowing for punctuation

        loc = line.find(token);
      
        if loc <= 0: 
            return line;

        lineLen = len(line);
        midPoint = lineLen / 2; 
        startPoint = loc - WINDOW_CHARS; 
        endPoint = loc + WINDOW_CHARS; 

        if startPoint < 0 and endPoint > lineLen: 
            diff = WINDOW_CHARS - loc;
            pad = '';
            for x in range(diff):
                pad = pad + ' ';
            formattedLine = pad + line[0:loc] + '  ' + line[loc:endPoint]

        elif startPoint > 0 and endPoint > lineLen:
            formattedLine = line[startPoint:loc] + '  ' + line[loc:endPoint]

        elif startPoint < 0 and endPoint < lineLen: 
            diff = WINDOW_CHARS - loc;
            pad = '';
            for x in range(diff):
                pad = pad + ' ';
            formattedLine = pad + line[0:loc] + '  ' + line[loc:endPoint]

        else: 
            diff = WINDOW_CHARS - loc;
            pad = '';
            for x in range(diff):
                pad = pad + ' ';
            formattedLine = pad + line[startPoint:loc] + '  ' + line[loc:endPoint]
	
        return formattedLine.strip('\n') + '\n';
            
    def freq_clicked(self, treeview, path, viewcolumn):
        """ Frequency click event handler """
        global exactMatch; 
        # Find out which frequency word was clicked
        clickedFrequency = self.freqList[path[0]].strip().split()[1]
        
        # Find sentences from our file containing this word
        global MAX_CONCORDANCES
        concList = []
        sentListHandler = open(self.sentListFile)
        
        while(len(concList) < MAX_CONCORDANCES) :
            myLine = sentListHandler.readline()
            
            if len(myLine):
                if clickedFrequency in myLine:
                    newLine = self.process_line(myLine, clickedFrequency, exactMatch);
                    if len(newLine.strip()) > 3: 
                        concList.append(newLine)
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
        print("Usage: python apertium-concord.py freqList sentences")
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
    
   

