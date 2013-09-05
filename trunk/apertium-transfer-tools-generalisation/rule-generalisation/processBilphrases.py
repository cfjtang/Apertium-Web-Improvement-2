#!/usr/bin/python
# coding=utf-8
# -*- encoding: utf-8 -*-

import sys, ruleLearningLib, argparse,gzip
from ruleLearningLib import  AllowedStructuresSet

if __name__=="__main__":
    DEBUG=False
    parser = argparse.ArgumentParser(description='Selects bilingual phrases.')
    parser.add_argument('--extract_structures',action='store_true')
    parser.add_argument('--closed_categories',required=True)
    parser.add_argument('--ends_must_be_aligned',action='store_true')
    parser.add_argument('--allowed_structures')
    #value = dir in which bilphrases will be written
    #ATs = std output
    args = parser.parse_args(sys.argv[1:])
    
    
    closedCategoriesSet=set( [ rawcat.decode('utf-8').strip()[1:-1] for rawcat in open(args.closed_categories)] )
    print >> sys.stderr, str(closedCategoriesSet)
    
    
    allowedStructuresSet=AllowedStructuresSet()
    if args.allowed_structures:
        structfile=gzip.open(args.allowed_structures)
        for line in structfile:
            line=line.decode('utf-8').strip()
            at = ruleLearningLib.AlignmentTemplate()
            at.parse(line)
            allowedStructuresSet.add(at)
        structfile.close()
    
    for line in sys.stdin:
        line=line.decode('utf-8')
        pieces=line.split(u'|')
        freqstr=pieces[0]
        
        for i in range(4):
            pieces[i]=pieces[i].strip()
        
        atstr=u'|'.join(pieces[1:4])+" | "
        at = ruleLearningLib.AlignmentTemplate()
        at.parse(atstr)
        
        #add restrictions
        tllemmasFromDic=list()
        wordsFromBiling= [ w.strip() for w in pieces[4].split(u"\t") ]
        at.parsed_restrictions=[]
        for word in wordsFromBiling:
            restriction=ruleLearningLib.AT_Restriction()
            restriction.parse(word)
            at.parsed_restrictions.append(restriction)
            tllemmasFromDic.append(ruleLearningLib.get_lemma(word)) 
        
        atOK=True
        if len(at.parsed_restrictions) != len(at.parsed_sl_lexforms):
            print >> sys.stderr, "Discarding line due to different length of restrictions and SL lexical forms: "+line.encode('utf-8')
            atOK=False
        
        #check that aligned words of open categories match dictionary translation
        if atOK and at.aligned_words_of_open_categories_match_dictionary_translation(closedCategoriesSet,tllemmasFromDic):
            if not args.ends_must_be_aligned or at.is_ends_aligned() or allowedStructuresSet.is_at_allowed(at):
                sl_lemmas,tl_lemmas=at.extract_lemmas()
                
                if args.extract_structures:
                    at.remove_all_inflection_tags()
                    at.remove_all_lemmas()
                    print at
                else:
                    extensionMarker=""
                    #if not at.is_ends_aligned() and allowedStructuresSet.is_at_allowed(at):
                    #    extensionMarker=" | extension"
                    print freqstr.strip().encode('utf-8')+" | "+at.__repr__()+" | "+u"\t".join(sl_lemmas).encode('utf-8')+" | "+u"\t".join(tl_lemmas).encode('utf-8')+" | "+u"\t".join(tllemmasFromDic).encode('utf-8')+extensionMarker 
            