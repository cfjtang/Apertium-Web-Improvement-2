""" 
    First go at a concordancer for spectie
    Has a GLADE gui and show concordances for frequencies 
    Sebastian Clarke, Francis Tyers - 10/2010 

    Haiku are easy, 
    But somtimes they don't make sense, 
    Refrigerator


"""

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
WINDOW_WORDS = 10 # has now been replaced with words - SEB

# Create the main class that will contain and do everything
class ConcordGTK:
    """ This is our concordancer class, will show UI and respond to input """

    def __init__(self, freqList, sentListFile):
        """ The constructor for this class """
        
        # Set up instance variables
        self.freqList = freqList
        self.sentListFile = sentListFile
        self.exactMatch = True;
        self.currentFrequency = None;
        
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
        """ Return the number of tokens in the sentences file """
        
        # This almost certainly wants to be more efficient
        return len(file(sentFileName).read().split(' '));


    def process_line(self, line, token):
        """ Another process line which works on words not chars 
        This function strips a line down to a max amount of words
        and centers it on the token """
        
        global WINDOW_WORDS # how many words to display
        line = line.decode('utf-8')
        if self.exactMatch: token = ' '+token+' '
        else: token = ' '+token            
        word_loc = len(line.split(token)[0].strip().split(' '))
        line = line.strip().split(' ') # turn line into list of words
        
        # if our word is not in the first 10
        if not word_loc < WINDOW_WORDS:
            # we need to strip this down to 10 words, with ours in the middle
            half = WINDOW_WORDS/2
            line = line[word_loc - half:word_loc + half]

        # ensure it is only 10 long
        line = line[0:WINDOW_WORDS]       
        
        # split line into two segments, around word
        
        word_loc = len(' '.join(line).split(token)[0].strip().split(' '))
        if word_loc == len(line):
            word_loc -= 1
        front = line[0:word_loc]
        back = line[word_loc:len(line)]
       
        # add pad word to first segment       
        MAGIC_PAD_CHARS_VALUE = 8*WINDOW_WORDS
        diff = MAGIC_PAD_CHARS_VALUE - len(' '.join(front))
        pad = [' ' for i in range(diff)]
        front.insert(0,''.join(pad))
        
        # stick the two segments together
        front.extend(back)
        
        # return it as a string
        return ' '.join(front)+'\n'
        
        
    def update_conc_view(self, frequency):
        """ Function to update the concordance window for a given frequency """
        
        # Find sentences from our file containing this word
        global MAX_CONCORDANCES
        concList = []
        sentListHandler = open(self.sentListFile) # should prob change this to ro
        
        while(len(concList) < MAX_CONCORDANCES) :
            myLine = sentListHandler.readline()
            
            if len(myLine):
            
                # different kinds of matching, exact or not
                if self.exactMatch:
                    if ' '+frequency+' ' in myLine:
                        newLine = self.process_line(myLine, frequency);
                        if len(newLine.strip()) > 3: 
                            concList.append(newLine)
                else:
                    if ' '+frequency in myLine:
                        newLine = self.process_line(myLine, frequency);
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
        
        
    # CALL BACKS
        
    def freq_clicked(self, treeview, path, viewcolumn):
        """ Frequency click event handler """
        
        # Find out which frequency word was clicked, store
        clickedFrequency = ' '.join(self.freqList[path[0]].strip().split()[1:]) 
        self.currentFrequency = clickedFrequency
        # update the view
        self.update_conc_view(clickedFrequency) 
     
        
    def search_box_update(self, box): 
        """ Search box update callback """
           
        print 'Search box updated';


    def exact_match_check(self, treeview):
        """ Toggle exact match check status """
         
        # This should also update the concordance window automagically
        self.exactMatch = not self.exactMatch
        if not self.currentFrequency == None:
            self.update_conc_view(self.currentFrequency)
            

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
    
   

