#!/usr/bin/python2.5 -Wall
# coding=utf-8
# -*- encoding: utf-8 -*-

import web, os, codecs, sys;
from config import Config;
from pair import Pair;
from interface import Interface;

sys.stdout = codecs.getwriter('utf-8')(sys.stdout);
sys.stderr = codecs.getwriter('utf-8')(sys.stderr);

class Globals: #{
	config = Config(os.getcwd() + '/config/config.xml');	
#}

Globals.config.parse_config();
i = Interface();

urls = (
    '/add', 'add',
    '/commit/(.*)', 'commit',
    '/(.*)', 'form'
)

class commit: #{
    
    def GET(self, name): #{
        print >> sys.stderr , 'Commit called for ' + name;
        pairs = Globals.config.get_pairs();

	if name == 'all': #{
	    print '<html>';
            print 'Commit called for ' + name + '<br />';
	    for pair in pairs: #{
                print '<h3>' + pair + '</h3>';
		pare = pairs[pair];
	    	print 'Comitting...<br />';
            	pare.commit();
	    	print 'Committed.';
		print '<p/>';
		sys.stdout.flush();
	    #}
	    print '<p/><a href="http://xixona.dlsi.ua.es:8080/">Return</a>';
            print '</html>';
	    return;

        elif name in pairs: #{
	    print '<html>';
            print 'Commit called for ' + name + '<br />';
            pair = pairs[name];
	    print 'Comitting...<br />';
            pair.commit();
	    print 'Committed.</html>';
	    print '<p/><a href="http://xixona.dlsi.ua.es:8080/">Return</a>';
	    return;

	else: #{
	    print '<html><body>Pair does not exist.<p/>';
	    print 'Error in commit.';
	    print '<p/><a href="http://xixona.dlsi.ua.es:8080/">Return</a>';
	    print '</body></html>';
	    return;
        #}

        sys.stderr.flush();

    #}
#}

class form: #{

    def GET(self, name): #{
	pairs = Globals.config.get_pairs();
	default_pair = pairs.keys()[0];
	tags = Globals.config.pairs[default_pair].get_tags();
	default_tag = tags.keys()[0];

	if name != type(None) and name != '' and len(name) > 1: #{
		print >> sys.stderr , 'Restarting with: ' + name;
		default_pair = name;
		tags = Globals.config.pairs[default_pair].get_tags();
		default_tag = tags.keys()[0];
	#}

	dictionary_left = Globals.config.pairs[default_pair].dictionary['left'];
	dictionary_bidix = Globals.config.pairs[default_pair].dictionary['bidix'];
	dictionary_right = Globals.config.pairs[default_pair].dictionary['right'];

	paradigms_left = Globals.config.pairs[default_pair].dictionary['left'].get_paradigms_by_tag(default_tag);
	paradigms_right = Globals.config.pairs[default_pair].dictionary['right'].get_paradigms_by_tag(default_tag);

	glosses_left = Globals.config.pairs[default_pair].dictionary['left'].get_glosses();
	glosses_right = Globals.config.pairs[default_pair].dictionary['right'].get_glosses();

	alternatives_left = dictionary_left.get_alternatives();
	alternatives_bidix = dictionary_bidix.get_alternatives();
	alternatives_right = dictionary_right.get_alternatives();

	post_data = {
	    'selected_pair': default_pair, 
	    'selected_tag': default_tag, 
	    'tags': tags, 
	    'committing': 'no', 
	    'previewing': 'off', 
	    'left_lemma': '', 
	    'right_lemma': '',
	    'left_comment': '', 
	    'right_comment': '',
	    'left_paradigm': '',
	    'right_paradigm': '',
	    'left_alternative': '',
	    'bidix_alternative': '',
	    'right_alternative': '',
	    'left_alternatives': alternatives_left,
	    'bidix_alternatives': alternatives_bidix,
	    'right_alternatives': alternatives_right,
	    'left_glosses': glosses_left,
	    'right_glosses': glosses_right,
	    'left_display_mode': dictionary_left.get_display_by_tag(default_tag),
	    'right_display_mode': dictionary_right.get_display_by_tag(default_tag),
	    'left_paradigms': paradigms_left,
	    'right_paradigms': paradigms_right,
	    'restriction': '',
	    'pairs': pairs
	};

	print i.display(post_data);
        sys.stderr.flush();
    #}

#}

class add: #{

    def POST(self): #{
        post_data = web.input(name = []);
	pairs = Globals.config.get_pairs();

	current_pair = post_data['selected_pair'];
	current_tag = post_data['selected_tag'];
	current_left_alternative = '';
	current_bidix_alternative = '';
	current_right_alternative = '';
	left_paradigm = '';
	right_paradigm = '';

	try: #{
            left_paradigm = post_data['left_paradigm'];
        except: #{
	    print >> sys.stderr, 'Error';
        #}
	try: #{
            right_paradigm = post_data['right_paradigm'];
        except: #{
	    print >> sys.stderr, 'Error';
        #}
	try: #{
	    current_left_alternative = post_data['left_alternative'];
	except: #{
	    print >> sys.stderr, 'Error';
	#}
	try: #{
	    current_bidix_alternative = post_data['bidix_alternative'];
	except: #{
	    print >> sys.stderr, 'Error';
	#}
	try: #{
	    current_right_alternative = post_data['right_alternative'];
	except: #{
	    print >> sys.stderr, 'Error';
	#}

	dictionary_left = Globals.config.pairs[current_pair].dictionary['left'];
	dictionary_right = Globals.config.pairs[current_pair].dictionary['right'];
	dictionary_bidix = Globals.config.pairs[current_pair].dictionary['bidix'];

	tags = Globals.config.pairs[current_pair].get_tags();

	paradigms_left = dictionary_left.get_paradigms_by_tag(current_tag);
	paradigms_right = dictionary_right.get_paradigms_by_tag(current_tag);

	glosses_left = dictionary_left.get_glosses();
	glosses_right = dictionary_right.get_glosses();

	alternatives_left = dictionary_left.get_alternatives();
	alternatives_bidix = dictionary_bidix.get_alternatives();
	alternatives_right = dictionary_right.get_alternatives();

	committing = 'no';
	try: #{
		print >> sys.stderr, 'commit box: ' , post_data['commit_box'];
		if post_data['commit_box'] == 'Commit': #
			committing = 'yes';
		#}
	#}
	except: #{
		print >> sys.stderr, 'commit box error';
	#}

	post_data = {
	    'commit_box': '',
	    'committing': committing,
	    'selected_pair': current_pair, 
	    'selected_tag': current_tag, 
	    'tags': tags, 
	    'previewing': 'on', 
	    'left_comment': post_data['left_comment'], 
	    'right_comment': post_data['right_comment'],
	    'left_lemma': post_data['left_lemma'], 
	    'right_lemma': post_data['right_lemma'],
	    'left_alternative': current_left_alternative,
	    'bidix_alternative': current_bidix_alternative,
	    'right_alternative': current_right_alternative,
	    'left_dictionary': dictionary_left,
	    'bidix_dictionary': dictionary_bidix,
	    'right_dictionary': dictionary_right,
	    'left_alternatives': alternatives_left,
	    'bidix_alternatives': alternatives_bidix,
	    'right_alternatives': alternatives_right,
	    'left_paradigm': left_paradigm,
	    'right_paradigm': right_paradigm,
	    'left_paradigms': paradigms_left,
	    'left_glosses': glosses_left,
	    'right_glosses': glosses_right,
	    'left_display_mode': dictionary_left.get_display_by_tag(current_tag),
	    'right_display_mode': dictionary_right.get_display_by_tag(current_tag),
	    'right_paradigms': paradigms_right,
	    'restriction': post_data['restriction'],
	    'pairs': pairs
	};

	print >> sys.stderr , str(post_data);
        sys.stderr.flush();

	print i.display(post_data);
        sys.stderr.flush();
    #}
#}

if __name__ == "__main__": #{

    if len(sys.argv) < 2: #{

        try: #{
            pid = os.fork();
            if pid > 0: #{
                sys.exit(0);
            #}
        except OSError, e: #{
           print >>sys.stderr, "fork #1 failed: %d (%s)" % (e.errno, e.strerror); 
           sys.exit(1);
        #}

        #os.chdir("/");
        os.setsid();
        os.umask(0);
    
        try: #{
            pid = os.fork();
            if pid > 0: #{
                print "Daemon PID %d" % pid;
                sys.exit(0);
	    #}
        except OSError, e: #{
            print >>sys.stderr, "fork #2 failed: %d (%s)" % (e.errno, e.strerror);
            sys.exit(1);
        #}

        sys.stderr = open(Globals.config.log_file, 'a+')

    #}

    web.run(urls, globals());
#}
