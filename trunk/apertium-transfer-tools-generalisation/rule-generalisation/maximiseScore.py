'''
Created on 15/06/2013

@author: vitaka
'''
from beamSearchLib import RuleApplicationHypothesis
from pulp.constants import LpStatusOptimal, LpStatus
import argparse
import ruleLearningLib
import sys
from ruleLearningLib import debug

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='select rules which maximise 1-BLEU score')
    parser.add_argument('--only_n_first')
    parser.add_argument('--only_hyps_with_maximum_local',action='store_true')
    parser.add_argument('--discard_sentences_all_maximum',action='store_true')
    parser.add_argument('--beam',action='store_true')
    parser.add_argument('--beam_size',default='10000')
    parser.add_argument('--debug', action='store_true')
    args = parser.parse_args(sys.argv[1:])
    
    if args.debug:
        DEBUG=True
        ruleLearningLib.DEBUG=True
    
    nfirst=None
    if args.only_n_first:
        nfirst=int(args.only_n_first)
    
    ll_hypothesis=list()
    for line in sys.stdin:
        line=line.decode('utf-8').strip()
        parts=line.split(u"|||")
        if nfirst != None:
            parts=parts[:nfirst]
        ll_hypothesis.append([ RuleApplicationHypothesis.create_and_parse(part) for part in parts])

    if args.only_hyps_with_maximum_local:
        
        for numSentence,l_hypothesis in enumerate(ll_hypothesis):
            firstNotMaximumIndex=len(l_hypothesis)
            maximumScore=l_hypothesis[0].get_score()
            for index in range(len(l_hypothesis)):
                if l_hypothesis[index].get_score() < maximumScore:
                    firstNotMaximumIndex=index
                    break
            if firstNotMaximumIndex == len(l_hypothesis) and args.discard_sentences_all_maximum:
                l_hypothesis[:]=[RuleApplicationHypothesis()]
            else:
                l_hypothesis[:]=l_hypothesis[:firstNotMaximumIndex]
            debug("Sentence "+str(numSentence)+": "+str(firstNotMaximumIndex)+" hypothesses with maximum BLEU")
        
    if not args.beam:
        status,solution,valueOfSolution=RuleApplicationHypothesis.select_rules_maximize_score(ll_hypothesis)
        if status == LpStatusOptimal:
            #print ids of rules
            appliedRules=set()
            for hyp in solution:
                appliedRules.update(hyp.get_applied_rules())
            appliedRulesSorted=sorted(appliedRules)
            for ruleid in appliedRulesSorted:
                print str(ruleid)
            print >> sys.stderr, "Value: "+str(valueOfSolution)
        else:
            print >> sys.stderr, "Wrong Status: "+str(LpStatus[status])
    else:
        appliedRules,valueOfSolution=RuleApplicationHypothesis.select_rules_maximize_score_with_beam_search(ll_hypothesis, beamSize=int(args.beam_size), isDiff=True)
        for ruleid in sorted(appliedRules):
            print str(ruleid)
        print >> sys.stderr, "Value: "+str(valueOfSolution)