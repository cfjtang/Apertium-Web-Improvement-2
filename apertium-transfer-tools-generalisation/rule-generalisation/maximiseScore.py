'''
Created on 15/06/2013

@author: vitaka
'''
from beamSearchLib import RuleApplicationHypothesis,RuleList,ParallelSentence
from pulp.constants import LpStatusOptimal, LpStatus
import argparse
import ruleLearningLib
import sys,gzip
from ruleLearningLib import debug,AlignmentTemplate

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='select rules which maximise 1-BLEU score')
    parser.add_argument('--tag_groups_file_name',required=True)
    parser.add_argument('--tag_sequences_file_name',required=True)
    parser.add_argument('--only_n_first')
    parser.add_argument('--only_hyps_with_maximum_local',action='store_true')
    parser.add_argument('--discard_sentences_all_maximum',action='store_true')
    parser.add_argument('--beam',action='store_true')
    parser.add_argument('--beam_size',default='10000')
    parser.add_argument('--super_heuristic',action='store_true')
    parser.add_argument('--select_boxes_minimum',action='store_true')
    parser.add_argument('--debug', action='store_true')
    parser.add_argument('--final_boxes_index')
    parser.add_argument('--alignment_templates')
    parser.add_argument('--sentences')
    parser.add_argument('--supersegments_with_maximum_score')
    args = parser.parse_args(sys.argv[1:])
    
    if args.debug:
        DEBUG=True
        ruleLearningLib.DEBUG=True
        
    ruleLearningLib.AT_LexicalTagsProcessor.initialize(args.tag_groups_file_name,args.tag_sequences_file_name)
    
    ruleList=RuleList()
    #load alignment templates
    if args.alignment_templates:
        gfile=gzip.open(args.alignment_templates)
        for line in gfile:
            line=line.strip().decode('utf-8')
            at=AlignmentTemplate()
            at.parse(line)
            ruleList.add(at)
        gfile.close()
    
    #load sentences
    sentences=list()
    if args.sentences:
        gfile=gzip.open(args.sentences)
        for line in gfile:
            line=line.strip().decode('utf-8')
            parallelSentence=ParallelSentence()
            parallelSentence.parse(line, parseTlLemmasFromDic=True)
            parallelSentence.add_explicit_empty_tags()
            sentences.append(parallelSentence)
        gfile.close()
    
    boxesInvDic=dict()
    if args.final_boxes_index:
        for line in open(args.final_boxes_index):
            parts=line.split("\t")
            boxesInvDic[int(parts[0])]=parts[1].strip()
    
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
    
    print >> sys.stderr, "Maximising score of "+str(len(ll_hypothesis))+" sentences"
    
    if args.only_hyps_with_maximum_local or args.super_heuristic or args.select_boxes_minimum:
        
        #remove all non-maximum hypotheses
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
        
        
    if args.beam:
        appliedRules,valueOfSolution=RuleApplicationHypothesis.select_rules_maximize_score_with_beam_search(ll_hypothesis, beamSize=int(args.beam_size), isDiff=True)
        for ruleid in sorted(appliedRules):
            print str(ruleid)
        print >> sys.stderr, "Value: "+str(valueOfSolution)
    elif args.super_heuristic:
        appliedRules=RuleApplicationHypothesis.select_rules_maximize_score_with_super_heuristic(ll_hypothesis)
        for ruleid in sorted(appliedRules):
            print str(ruleid)
    elif args.select_boxes_minimum:
        
        supersegmentsWithMaxScore=list()
        if args.supersegments_with_maximum_score:
            #load supersegments
            gfile=gzip.open(args.supersegments_with_maximum_score)
            for line in gfile:
                line=line.strip().decode('utf-8')
                parts=line.split(u"|||")
                supersegmentsWithMaxScore.append([ RuleApplicationHypothesis.create_and_parse(part) for part in parts if len(part)>0])
            gfile.close()
        
        appliedRules=RuleApplicationHypothesis.select_rules_maximize_score_boxes_applied(ll_hypothesis,boxesInvDic,ruleList,sentences,supersegmentsWithMaxScore)
        for ruleid in sorted(appliedRules):
            print str(ruleid)
    else:
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