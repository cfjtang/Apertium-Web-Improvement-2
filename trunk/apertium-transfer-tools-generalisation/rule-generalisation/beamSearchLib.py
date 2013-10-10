'''

Created on 07/06/2013

@author: vitaka
'''
from pulp import constants
from pulp.constants import LpStatusOptimal, LpMaximize
from pulp.pulp import LpProblem, LpVariable, LpAffineExpression, LpConstraint, \
    value
from ruleLearningLib import AT_LexicalForm, debug
from tempfile import mkstemp, NamedTemporaryFile
import os
import ruleLearningLib
import subprocess
import sys,numpy
from collections import defaultdict

class RuleList(object):
    
    isRestrictionsIncluded=False
    
    @classmethod
    def is_restrictions_included(cls):
        return cls.isRestrictionsIncluded
    @classmethod
    def set_restrictions_included(cls,p_resIncluded):
        cls.isRestrictionsIncluded=p_resIncluded
    
    def __init__(self):
        self.catSequenceDict=dict()
        self.maxLength=0
        self.ruleList=[None]
    
    def add(self,at):
        poslistStr=str(at.get_pos_list(self.__class__.is_restrictions_included()))
        if not poslistStr in self.catSequenceDict:
            self.catSequenceDict[poslistStr]=list()
        at.id=len(self.ruleList)
        self.catSequenceDict[poslistStr].append(at)
        self.maxLength=max(self.maxLength,len(at.parsed_sl_lexforms))
        self.ruleList.append(at)
    
    def get_max_length(self):
        return self.maxLength
    
    def get_by_id(self,id):
        if id <=0 or id >= len(self.ruleList):
            raise RuntimeError("ID not in range")
        return self.ruleList[id]
    
    #returns the rules matching the segment, sorted in decreasing priority order
    def get_rules_matching_segment(self,segment,restrictions,exactMatch=False):
        rulesMatching=list()
        
        if len(segment) > 0 and not segment[0].is_unknown():
            listOfCats=list()
            for i in range(len(segment)):
                sllex=segment[i]
                restriction=restrictions[i]
                if self.__class__.is_restrictions_included():
                    listOfCats.append((sllex.get_pos(),restriction.get_tags()))
                else:
                    listOfCats.append(sllex.get_pos())
            
            
            if exactMatch:
                prefixes=[len(segment)-1]
            else:
                prefixes=reversed(range(len(segment)))
            
            for i in prefixes:
                prefixCatsStr=str(listOfCats[:i+1])
                prefixSegment=segment[:i+1]
                prefixRestriction=restrictions[:i+1]
                if prefixCatsStr in self.catSequenceDict:
                    for at in self.catSequenceDict[prefixCatsStr]:
                        if at.matches_segment(prefixSegment,prefixRestriction, self.__class__.is_restrictions_included()):
                            rulesMatching.append(at)
        return rulesMatching
    
    
class RuleApplicationHypothesis(object):
    
    totalNumRules=1
    
    maxLength=1
    config_tl="ca"
    apertium_data_dir=None
    minimumCoverdedWords=False
    boxesInvDict=dict()
    
    WORDFORWORDRULEID=-1
    
    def __init__(self,p_lengthOfSentence=1):
        self.appliedRules=set()
        self.discardedRules=set()
        self.source=list()
        self.translation=list()
        self.reference=list()
        self.score=0.0
        self.processedSlWords=0
        self.lengthOfSentence=p_lengthOfSentence
        self.rulesList=list()
    
    def is_empty(self):
        return len(self.appliedRules) == 0 and len(self.discardedRules) == 0
    
    def get_applied_rules(self):
        return self.appliedRules
    
    def get_discarded_rules(self):
        return self.discardedRules
    
    def set_translation(self,p_translation):
        self.translation=p_translation
    
    def get_translation(self):
        return self.translation
    
    def set_reference(self,p_reference):
        self.reference=p_reference
    
    def get_reference(self):
        return self.reference
    
    def set_source(self,p_source):
        self.source=p_source
    
    def get_source(self):
        return self.source
    
    def set_score(self,p_score):
        self.score=p_score
    def get_score(self):
        return self.score
    
    def get_score_with_num_rules(self):
        if RuleApplicationHypothesis.minimumCoverdedWords:
            #assuming a maximum of 
            #100 words per sentence,
            #the minimum difference between two rule applications
            #of the proportion of words covered would be:
            # 1/100 = 0.01
            #So we only need 2 digits to represent it. Anyway, we will use 3
            #just in case there is a sentence longer than 100 words
            ruleApps=[ruleApp[1] for ruleApp in self.rulesList if ruleApp[0] != RuleApplicationHypothesis.WORDFORWORDRULEID] 
            mean=0
            if len(ruleApps) > 0:
                mean =numpy.mean( [ len(ruleApp) for ruleApp in ruleApps ] )
            return self.score*10000+ 0.1*(1 - (sum( len(ruleApp) for ruleApp in ruleApps )*1.0/self.lengthOfSentence)) + 0.0001*( 1 - (mean*1.0/RuleApplicationHypothesis.maxLength ))
        else:
            return self.score*10000+ 0.1*(1 - (len(self.discardedRules)*1.0/RuleApplicationHypothesis.totalNumRules))

    def get_processed_sl_words(self):
        return self.processedSlWords
    def set_processed_sl_words(self,p_numpslwords):
        self.processedSlWords=p_numpslwords
    
    def can_be_combined_with(self,otherhyp):
        return len(self.appliedRules & otherhyp.discardedRules) == 0 and len(otherhyp.appliedRules & self.discardedRules)==0 
    
    def add_to_rules_list(self,ruleid,posSequence):
        self.rulesList.append((ruleid,posSequence))
    
    #Concatenate two hypothesis
    def create_new_combined_with(self,otherhyp):
        combined=RuleApplicationHypothesis(self.lengthOfSentence)
        combined.appliedRules=self.appliedRules | otherhyp.appliedRules
        combined.discardedRules=self.discardedRules | otherhyp.discardedRules
        combined.translation=self.translation + otherhyp.translation
        combined.processedSlWords=self.processedSlWords+otherhyp.processedSlWords
        combined.rulesList.extend(self.rulesList)
        combined.rulesList.extend(otherhyp.rulesList)
        return combined
    
    def to_str_for_scoring(self):
        source=u" ".join(slf.unparse(replaceSpacesWithUnderscore=True,removeEmptyTags=True) for slf in self.source)
        test=u" ".join(u"^"+lf.unparse(replaceSpacesWithUnderscore=False,removeEmptyTags=True)+u"$" for lf in self.translation)
        ref=u" ".join(u"^"+lf.unparse(replaceSpacesWithUnderscore=False,removeEmptyTags=True)+u"$" for lf in self.reference)
        return u"|".join([source,test,ref]).encode('utf-8')
        
    def __repr__(self):
        return unicode(self.score)+u" | "+unicode(self.appliedRules)+u" | "+unicode(self.discardedRules)+u" | "+unicode(self.rulesList)+u" | "+u"\t".join([ lf.unparse() for lf in self.translation])
    
    def to_str_for_debug(self):
        return unicode(self.score)+u" | "+unicode(self.appliedRules)+u" | "+unicode(self.discardedRules)+u" | "+unicode(self.rulesList)+u" | "+ u" ".join([ lf.unparse() for lf in self.source]) +" | " + u" ".join([ lf.unparse() for lf in self.translation])+" |ref: "+u" ".join([ lf.unparse() for lf in self.reference])
    def parse(self,rawstr,parseTranslation=False):
        parts=rawstr.split(u"|")
        if len(parts) >=4:
            self.score=float(parts[0].strip())
            self.appliedRules=eval(parts[1].strip())
            self.discardedRules=eval(parts[2].strip())
            self.rulesList=eval(parts[3].strip())
            if parseTranslation and len(parts) >= 5:
                lexicalFormsStr=parts[4].strip().split(u"\t")
                for lxStr in lexicalFormsStr:
                    lf =ruleLearningLib.AT_LexicalForm()
                    lf.parse(lxStr)
                    self.translation.append(lf)
                
        else:
            raise Exception()
    
    @classmethod
    def create_and_parse(cls,rawstr,parseTranslation=False):
        hyp=RuleApplicationHypothesis()
        hyp.parse(rawstr,parseTranslation)
        return hyp

    @classmethod
    def set_target_language(cls,p_tl):
        cls.config_tl=p_tl
    
    @classmethod
    def set_num_total_rules(cls,p_totalNumRules):
        cls.totalNumRules=p_totalNumRules
    
    @classmethod
    def set_apertium_data_dir(cls,p_datadir):
        if p_datadir != "":
            cls.apertium_data_dir=p_datadir
    
    @classmethod
    def set_minimum_covered_words(cls,p_isminimum):
        cls.minimumCoverdedWords=p_isminimum
    
    @classmethod
    def set_max_length(cls,p_maxLength):
        cls.maxLength=p_maxLength
    
    @classmethod
    def set_inv_boxes_dict(cls,p_boxesdict):
        cls.boxesInvDict=p_boxesdict
    
    @classmethod
    def score_hypotheses(cls,hypothesisList):
        #create tmp file
        fileobj=NamedTemporaryFile(delete=False)
        
        listOfstrHyps=list(set([hyp.to_str_for_scoring() for hyp in hypothesisList]))
        
        debug("\nScoring hypotheses")
        
        for strhyp in listOfstrHyps:
            fileobj.write(strhyp+"\n")
        fileobj.close()
        
        command="bash "+os.getcwdu()+"/evaluateBeamSearchHypothesis.sh -f "+fileobj.name+" -t "+cls.config_tl
        if cls.apertium_data_dir:
            command+=" -d "
            command+=cls.apertium_data_dir

        output,error = subprocess.Popen(command,stdout= subprocess.PIPE ,stderr= subprocess.PIPE, shell=True).communicate()
        
        os.remove(fileobj.name)
        
        lines=output.strip().split("\n")
        if len(lines) != len(listOfstrHyps):
            print >> sys.stderr, "ERROR. NUmber of lines ("+str(len(lines))+") does not match hypothesis list ("+str(len(listOfstrHyps))+")"
            print >> sys.stderr, "Dump:"
            print >> sys.stderr, output
            print >> sys.stderr, "Error output:"
            print >> sys.stderr, error
            exit()
        
        resultsDictionary=dict()
        for i in range(len(lines)):
            if len(lines[i]) == 0:
                print >> sys.stderr, "Line with length 0"
                print >> sys.stderr, "command output"
                print >> sys.stderr, error
                exit()
            resultsDictionary[listOfstrHyps[i]]=float(lines[i])
            debug(str(float(lines[i]))+" | "+listOfstrHyps[i])
        
        for hyp in hypothesisList:
            hyp.set_score(resultsDictionary[hyp.to_str_for_scoring()])
    
    @classmethod
    def select_rules_maximize_score(cls,ll_hypothesis):
        #Create linear programming problem
        prob = LpProblem("maxbleu", LpMaximize)
        
        numSentences=len(ll_hypothesis)
        maxExecutedRules=max( max(len(hyp.get_applied_rules()) for hyp in hypothesesOfSentence ) for hypothesesOfSentence in ll_hypothesis)
        
        debug("Max executed rules per hypothesis: "+str(maxExecutedRules))
        
        varList=list()
        hypList=list()
        inverseVarDict=dict()
        numSentenceDict=dict()
        
        #one variable per rule set
        for numSentence,hypothesesOfSentence in enumerate(ll_hypothesis):
            for hyp in hypothesesOfSentence:
                hyp.id=len(varList)
                var=LpVariable("x"+str(hyp.id), 0, 1,cat='Integer')
                varList.append(var)
                hypList.append(hyp)
                inverseVarDict[hyp]=var
                numSentenceDict[hyp.id]=numSentence
        
        #expression to maximise: sum of chosen rule sets
        myexpression=LpAffineExpression( [ (varList[i],hypList[i].get_score()*10000 + (1.0*maxExecutedRules-len(hypList[i].get_applied_rules()))/(numSentences*maxExecutedRules+1)  )   for i in range(len(varList) )  ] )
        prob.objective=myexpression
        
        #restriction: at most one rule set per sentence   
        for i,hypothesesOfSentence in enumerate(ll_hypothesis):
            cname="atmost"+str(i)
            myexpression=LpAffineExpression( [ (varList[hyp.id],1) for hyp in hypothesesOfSentence ] )
            constraint = LpConstraint(myexpression,sense=constants.LpConstraintLE,name=cname,rhs=1) 
            prob.constraints[cname]=constraint
        
        #restriction: compatible rules
        for i,hyp in enumerate(hypList):
            incompHyps=[ ihyp for ihyp in hypList if hyp != ihyp and numSentenceDict[hyp.id] != numSentenceDict[ihyp.id] and len(hyp.appliedRules & ihyp.discardedRules) > 0 ]
            cname="incompatible"+str(i)
            myexpression=LpAffineExpression( [ (varList[ihyp.id],1) for ihyp in incompHyps ] + [(varList[hyp.id],len(incompHyps))] )
            constraint = LpConstraint(myexpression,sense=constants.LpConstraintLE,name=cname,rhs=len(incompHyps)) 
            prob.constraints[cname]=constraint
        
        #solve
        status = prob.solve()
        
        solution=list()
        valueOfSolution=0.0
        if status == LpStatusOptimal :
            for i in range(len(hypList)):
                if value(varList[i])==1:
                    solution.append(hypList[i])
                    valueOfSolution+=hypList[i].get_score()*10000 + (1.0*maxExecutedRules-len(hypList[i].get_applied_rules()))/(numSentences*maxExecutedRules+1)
            
            print >> sys.stderr, "Chosen rule applications" 
            for hypsol in solution:
                print >> sys.stderr, "Sentence "+str(numSentenceDict[hypsol.id])+": "+unicode(hypsol).encode('utf-8') 
            return status,solution,valueOfSolution
        else:
            return status,None
    
    @classmethod
    def select_rules_maximize_score_boxes_applied(cls,ll_hypothesis,boxesInvDict,allruleList,sentences,supersegmentsWithMaxScore):
        
        if len(ll_hypothesis) != len(sentences):
            print >> sys.stderr, "ERROR: different length of sentences and hypothesis"
            return []
        
        usePrecomputedSupersegments=False
        if len(supersegmentsWithMaxScore) > 0:
            usePrecomputedSupersegments=True
            if len(ll_hypothesis) != len(supersegmentsWithMaxScore):
                print >> sys.stderr, "ERROR: different length of supersegments and hypothesis"
                return []
        
        if allruleList.get_max_length() > 1:
            MAX_LENGTH=allruleList.get_max_length()
        else:
            #if the alignment template parameter was not provided
            MAX_LENGTH=5
            
        boxesDict=dict()
        for key in boxesInvDict:
            boxesDict[boxesInvDict[key]]=key
        
        countsApplied=defaultdict(int)
        countsBanned=defaultdict(int)
        rulesMinimumAtLeastOneTime=set()
        
        for numSentence,hypothesesOfSentence in enumerate(ll_hypothesis):
            bestHyp=hypothesesOfSentence[0]
            currentSentence=sentences[numSentence]
            #for ruleid in bestHyp.get_applied_rules():
            #    countsApplied[ruleid]+=1
            ruleList=bestHyp.rulesList
            
            optimalRuleApplicationsSet=set()
            if usePrecomputedSupersegments:
                hypsWithSupersegments=supersegmentsWithMaxScore[numSentence]
            else:
                hypsWithSupersegments=hypothesesOfSentence[1:]
            
            for otherHyp in hypsWithSupersegments:
                currentPosition=0
                for ruleApp in otherHyp.rulesList:
                    if ruleApp[0] != RuleApplicationHypothesis.WORDFORWORDRULEID:
                        optimalRuleApplicationsSet.add((currentPosition,len(ruleApp[1]),ruleApp[0]))
                    currentPosition+=len(ruleApp[1])
            
            #build list of lexical categories
            bannedStartPositions=set()
            bannedEndPositions=set()
            listOfPos=list()
            positionsWhereRuleStartAndLength=list()
            currentPosition=0
            for ruleApp in ruleList:
                listOfPos.extend(ruleApp[1])
                if ruleApp[0] != RuleApplicationHypothesis.WORDFORWORDRULEID:
                    countsApplied[ruleApp[0]]+=1
                    rulesMinimumAtLeastOneTime.add(ruleApp[0])
                    positionsWhereRuleStartAndLength.append((currentPosition,len(ruleApp[1])))
                    
                    for l in range(len(ruleApp[1])-1):
                        bannedEndPositions.add(currentPosition+l)
                        bannedStartPositions.add(currentPosition+l+1)
                    
                currentPosition+=len(ruleApp[1])
            
            debug("List of lexical categories of sentence "+str(numSentence+1)+": "+" ".join(listOfPos))
            debug("positionsWhereRuleStartAndLength: "+str(positionsWhereRuleStartAndLength))
            
            debug("Supersets added:")
            #increase also frequency of supersegments of minimum set
            for pos,length,ruleid in optimalRuleApplicationsSet:
                if length > 1 and pos not in bannedStartPositions and pos+length-1 not in bannedEndPositions:
                    #it must be a superset of an existing sequence
                    isSuperset=False
                    for minStartPosition,minLength in positionsWhereRuleStartAndLength:
                        if pos<=minStartPosition and pos+length >= minStartPosition+minLength:
                            isSuperset=True
                    if isSuperset:
                        countsApplied[ruleid]+=1
                        debug("\t" +str(pos)+": "+"__".join(listOfPos[pos:pos+length]))
            
            #for each applied rule, compute 
            #rules left-intersecting with it 
            #rulesLeftIntersecting=set()
            #TODO: Check that they match!!!!!!!!!!!!!!!!!!!!!!!!!
            for startPosition,length in positionsWhereRuleStartAndLength:
                debug("Discarded rules for ("+str(startPosition)+") "+"__".join(listOfPos[startPosition:startPosition+length]))
                for endingPositionOffset in range(length-1):
                    endingPositionIncluded=startPosition+endingPositionOffset
                    for lengthOfLeftIntersectinRule in range(2,MAX_LENGTH+1):
                        startingPositinoOfIntersecting=endingPositionIncluded-lengthOfLeftIntersectinRule +1
                        if startingPositinoOfIntersecting >= 0 and startingPositinoOfIntersecting < startPosition:
                            seqOfPosOfIntersecting=listOfPos[startingPositinoOfIntersecting:endingPositionIncluded+1]
                            
                            #check whether the intersecting segment matches any AT
                            ruleMatchingList=allruleList.get_rules_matching_segment(currentSentence.parsed_sl_lexforms[startingPositinoOfIntersecting:endingPositionIncluded+1],currentSentence.parsed_restrictions[startingPositinoOfIntersecting:endingPositionIncluded+1])
                            
                            if len(ruleMatchingList) > 0:
                                strForm="__".join(seqOfPosOfIntersecting)
                                debug("\t"+strForm)
                                if strForm in boxesDict:
                                    ruleid=boxesDict[strForm]
                                    #rulesLeftIntersecting.add(ruleid)
                                    countsBanned[ruleid]+=1
            #for ruleid in rulesLeftIntersecting:
                #countsBanned[ruleid]+=1
                    
        if ruleLearningLib.DEBUG:
            debug("Frequencies of applied pos sequences:")
            for key in countsApplied.keys():
                posSeq=""
                if key in boxesInvDict:
                    posSeq=boxesInvDict[key]
                isMininimumStr=""
                if key in rulesMinimumAtLeastOneTime:
                    isMininimumStr="m "
                debug(isMininimumStr+"id="+str(key)+" "+posSeq+" : "+str(countsApplied[key])+" - "+str(countsBanned[key])+" = "+str(countsApplied[key]-countsBanned[key]))

        return [ key for key in countsApplied.keys() if countsApplied[key] > countsBanned[key] and key in rulesMinimumAtLeastOneTime ]
        
    @classmethod
    def select_rules_maximize_score_with_super_heuristic(cls,ll_hypothesis):
        #hypothesis are already sorted by score and num. of discarded rules
        
        #create set of frozensets of rules to be removed to obtain the maximum score
        countsApplied=defaultdict(int)
        countsDiscarded=defaultdict(int)
        totalRules=set()
        setsOfRulesToDiscard=set()
        for hypothesesOfSentence in ll_hypothesis:
            for hyp in hypothesesOfSentence:
                totalRules.update(set(hyp.get_applied_rules()))
            bestHyp=hypothesesOfSentence[0]
            for ruleid in bestHyp.get_applied_rules():
                countsApplied[ruleid]+=1
            for ruleid in bestHyp.get_discarded_rules():
                countsDiscarded[ruleid]+=1
        
        finalDiscardedRules=set()
        for discardedRule in countsDiscarded.keys():
            if countsDiscarded[discardedRule] > countsApplied[discardedRule]:
                finalDiscardedRules.add(discardedRule) 
        
        debug("Final discarded rules: "+str(finalDiscardedRules))
        
        return totalRules - finalDiscardedRules
        
    @classmethod
    def select_rules_maximize_score_with_beam_search(cls,ll_hypothesis,beamSize=10000, isDiff=True):
        
        totalRules=set()
        for hypothesesOfSentence in ll_hypothesis:
            for hyp in hypothesesOfSentence:
                totalRules.update(set(hyp.get_discarded_rules()))

        #numSentences=len(ll_hypothesis)
        #PartitionSelectionHypothesis.set_parameters_for_minimised_num_rules(numSentences, maxExecutedRules)
        RuleApplicationHypothesis.set_num_total_rules(len(totalRules))
        
        
        #debug("Max executed rules per hypothesis: "+str(maxExecutedRules))
        debug("Total rules: "+str(len(totalRules)))
        
        hypothesisList=set()
        hypothesisList.add(PartitionSelectionHypothesis())
        
        for i in range(len(ll_hypothesis)):         
            ##### DEBUG ####
            debug("")
            debug("Starting beam search step "+str(i)) 
            debug(str(len(hypothesisList))+" hypothesis from previous steps before pruning")
            ##### DEBUG ####
            
            #prune hypothesis
            sortedHypothesisList=sorted(list(hypothesisList),key=lambda h: h.get_total_score() ,reverse=True)
            
            if isDiff:
                #find index whose difference exceeds threshold
                firstOutIndex=len(sortedHypothesisList)
                bestScore=sortedHypothesisList[0].get_total_score()
                
                #only perform search if necessary
                if  bestScore - sortedHypothesisList[-1].get_total_score() > beamSize:
                    index=1
                    while index < len(sortedHypothesisList):
                        if bestScore - sortedHypothesisList[index].get_total_score() > beamSize:
                            firstOutIndex=index
                            break
                        else:
                            index+=1  
                hypothesisList=sortedHypothesisList[:firstOutIndex]
            else:
                hypothesisList=sortedHypothesisList[:beamSize]
            
            ##### DEBUG ####
            debug(str(len(hypothesisList))+" hypothesis from previous steps after pruning")
            debug("Showing some of them:")
            if ruleLearningLib.DEBUG:
                for hyp in hypothesisList[:10]:
                    debug("\t"+unicode(hyp.get_total_score())+": "+unicode(hyp.get_total_applied_rules())+" | "+unicode(hyp.get_total_discarded_rules()))
            ##### DEBUG ####    
            
            ruleApplicationHypothesesInThisStep=ll_hypothesis[i]+[RuleApplicationHypothesis()]
            
            newMembers=set()
            for hyp in hypothesisList:
                for raph in ruleApplicationHypothesesInThisStep:
                    if raph.is_empty() or hyp.can_be_combined_with(raph):
                        newMembers.add(hyp.create_new_combined_with(raph))
            
            hypothesisList=newMembers
        
        winner=sorted(list(hypothesisList),key=lambda h: h.get_total_score() ,reverse=True)[0]
        
        print >> sys.stderr, "Chosen rule applications" 
        for numSentence,rahyp in enumerate(winner.ruleAppliccationHyps):
            print >> sys.stderr, "Sentence "+str(numSentence)+": "+unicode(rahyp).encode('utf-8')
        return winner.get_total_applied_rules(),winner.get_total_score()
    
    @classmethod
    def create_by_translating_segments(cls,ruleList,parsed_sl_lexforms,parsed_restrictions,listOfChunks,parsed_tl_lexforms,tllemmas_from_dictionary,boxesDic):
        
        newHyp=RuleApplicationHypothesis()
        foundEmptyMatchingRule=False
        
        #sort chunks by starting position
        sortedChunks= sorted(listOfChunks,key= lambda c: c[0])
        
        #obtain target lexical forms
        target_lexforms=list()
        nextChunk=sortedChunks[0]
        sortedChunks=sortedChunks[1:]
        index=0
        while index < len(parsed_sl_lexforms):
            debug("Index: "+str(index))
            debug("next chunk"+str(nextChunk))
            debug("list of chunks"+str(sortedChunks))
            if nextChunk != None:
                if index==nextChunk[0]:
                    length=nextChunk[1]
                    
                    #go to next chunk
                    if len(sortedChunks) > 0:
                        nextChunk=sortedChunks[0]
                        sortedChunks=sortedChunks[1:]
                    else:
                        nextChunk=None
                    
                    while nextChunk!=None and nextChunk[0]==index:
                        if nextChunk[1] > length:
                            length=nextChunk[1]    
                        #go to next chunk
                        if len(sortedChunks) > 0:
                            nextChunk=sortedChunks[0]
                            sortedChunks=sortedChunks[1:]
                        else:
                            nextChunk=None
                        
                    
                    matchingRules=ruleList.get_rules_matching_segment(parsed_sl_lexforms[index:index+length],parsed_restrictions[index:index+length],exactMatch=True)
                    if len(matchingRules) == 0:
                        foundEmptyMatchingRule=True
                        break
                    at=matchingRules[0]
                    tlsegment=at.apply(parsed_sl_lexforms[index:index+length],tllemmas_from_dictionary[index:index+length],parsed_restrictions[index:index+length])
                    target_lexforms.extend(tlsegment)
                    
                    boxid=boxid=boxesDic[at.get_pos_list_str()]
                    newHyp.appliedRules.add(boxid)
                    newHyp.add_to_rules_list(boxid, at.get_pos_list_str().split("__"))
                    
                    index+=length
                    continue
            
            #translate word for word
            if parsed_sl_lexforms[index].is_unknown():
                bilDicTranslation=parsed_sl_lexforms[index]
            else:
                bilDicTranslation=AT_LexicalForm()
                bilDicTranslation.set_lemma(tllemmas_from_dictionary[index])
                bilDicTranslation.set_pos(parsed_sl_lexforms[index].get_pos())
                bilDicTags=list()
                bilDicTags.extend(parsed_restrictions[index].get_tags())
                bilDicTags.extend(parsed_sl_lexforms[index].get_tags()[len(bilDicTags):])
                bilDicTranslation.set_tags(bilDicTags)
            target_lexforms.append(bilDicTranslation)
            newHyp.add_to_rules_list(RuleApplicationHypothesis.WORDFORWORDRULEID, [ parsed_sl_lexforms[index].get_pos() ])
            index+=1
        
        if not foundEmptyMatchingRule:
            newHyp.set_source(parsed_sl_lexforms)
            newHyp.set_translation(target_lexforms)
            newHyp.set_reference(parsed_tl_lexforms)
        else:
            newHyp.appliedRules=set()
        return newHyp
    
    def compute_supersets_with_maximum_score(self,sentence,ruleList,boxesDic):
        
        maxLength=ruleList.get_max_length()
        
        newRuleApplicationHypotheses=list()
        
        if ruleLearningLib.DEBUG:
            debug("Computing supersets for hypothesis: "+unicode(self).encode('utf-8'))
        
        #obtain more information about key segments
        keysegments=list()
        currentPosition=0
        for ruleApp in self.rulesList:
            if ruleApp[0] != RuleApplicationHypothesis.WORDFORWORDRULEID:
                #keysegment= ( position, length, ruleid )
                keysegments.append((currentPosition,len(ruleApp[1]),ruleApp[0]))
            currentPosition+=len(ruleApp[1])
        
        #compute supersets of each key segment
        #each position of the list: list of supersets which keep the maximum score
        
        #first, compute all supersets
        allSuperSetsOfKeySegments=list()
        for ksPos,ksLen,ksId in keysegments:
            supersetsOfThisKey=list()
            maxLengthToAdd=maxLength-ksLen
            for lengthToAdd in range(1,maxLengthToAdd+1):
                for leftSideLength in range(lengthToAdd+1):
                    #rightSideLengh=lengthToAdd-leftSideLength
                    startingPos=ksPos-leftSideLength
                    if startingPos >= 0 and  startingPos+ksLen+lengthToAdd <= len(sentence.parsed_sl_lexforms):
                        supersetsOfThisKey.append((startingPos,ksLen+lengthToAdd))
            allSuperSetsOfKeySegments.append(supersetsOfThisKey)       
        
        #remove supersets intersecting with other key segments.
        #no problem if they totally enclose other key segments
        notIntersectingSuperSetsOfKeySegments=list()
        for keySegmentIndex,allSupersets in enumerate(allSuperSetsOfKeySegments):
            notIntersectingOfThisKey=list()
            for startingPos,length in allSupersets:
                valid=True
                for otherKeySegmentIndex,keySegmentData in enumerate(keysegments):
                    if otherKeySegmentIndex != keySegmentIndex:
                        ksPos=keySegmentData[0]
                        ksLen=keySegmentData[1]
                        if ksPos < startingPos and startingPos < ksPos+ksLen :
                            valid=False
                            break
                        if ksPos < startingPos+length and ksPos +ksLen > startingPos+length:
                            valid=False
                            break
                if valid:
                    notIntersectingOfThisKey.append((startingPos,length))
            notIntersectingSuperSetsOfKeySegments.append(notIntersectingOfThisKey)
        
        if ruleLearningLib.DEBUG:
            debug("Supersets not intersecting: ")
            for keyindex,supersets in enumerate(notIntersectingSuperSetsOfKeySegments):
                debug("For ks "+str(keyindex)+": "+str(supersets))
            
        
        #check whether, when translating the SL sentence with a supersegment
        #and the remaining key segments, the same score is obtained
        for keySegmentIndex,allSupersets in enumerate(notIntersectingSuperSetsOfKeySegments):
            otherKeySegments=list()
            for otherKeySegmentIndex,keySegment in enumerate(keysegments):
                if otherKeySegmentIndex != keySegmentIndex:
                    otherKeySegments.append((keySegment[0],keySegment[1]))
                                            
            for keySegmentSuperset in allSupersets:
                listOfChunks=list()
                listOfChunks.append(keySegmentSuperset)
                listOfChunks.extend(otherKeySegments)
                
                debug("Creating hypothesis with these segments: "+str(listOfChunks))
                
                ruleAppWithSuperset=RuleApplicationHypothesis.create_by_translating_segments(ruleList,sentence.parsed_sl_lexforms,sentence.parsed_restrictions,listOfChunks,sentence.parsed_tl_lexforms,sentence.tl_lemmas_from_dictionary,boxesDic)
                if not ruleAppWithSuperset.is_empty():
                    debug(unicode(ruleAppWithSuperset).encode('utf-8'))
                    newRuleApplicationHypotheses.append(ruleAppWithSuperset)
                else:
                    debug("RULES NOT MATCHING")
        
        #it is not necessary to combine the different supersegments for 
        #each key segment. see how this result is used by the maximiseScore.py program
        
        if ruleLearningLib.DEBUG and False:
            debug("Rule application hypotheses to be scored:")
            for index,hyp in enumerate(newRuleApplicationHypotheses):
                debug("hyp "+str(index)+": "+unicode(hyp).encode('utf-8'))
            debug("")
        RuleApplicationHypothesis.score_hypotheses([self]+newRuleApplicationHypotheses)
        
        return [hyp for hyp in newRuleApplicationHypotheses if hyp.get_score() >= self.get_score()]

class PartitionSelectionHypothesis(object):
    
    numSentences=1
    maxExecutedRules=1
    
    def __init__(self):
        self.ruleAppliccationHyps=list()
        self.totalScore=0.0
        self.totalScoreWithoutNum=0.0
        self.totalAppliedRules=frozenset()
        self.totalDiscardedRules=frozenset()
    
    def can_be_combined_with(self,ruleApplicationHypothesis):
        return len(self.totalAppliedRules & ruleApplicationHypothesis.get_discarded_rules()) == 0 and len(self.totalDiscardedRules & ruleApplicationHypothesis.get_applied_rules()) == 0
    
    def create_new_combined_with(self,ruleApplicationHypothesis):
        partitionSelectionHypothesis= PartitionSelectionHypothesis()
        partitionSelectionHypothesis.ruleAppliccationHyps = self.ruleAppliccationHyps +  [ruleApplicationHypothesis]
        partitionSelectionHypothesis.totalScoreWithoutNum= self.totalScoreWithoutNum +ruleApplicationHypothesis.get_score()
        #partitionSelectionHypothesis.totalScore = self.totalScore+ (ruleApplicationHypothesis.get_score()*10000 + (1.0*PartitionSelectionHypothesis.maxExecutedRules-len(ruleApplicationHypothesis.get_applied_rules()))/(PartitionSelectionHypothesis.numSentences*PartitionSelectionHypothesis.maxExecutedRules+1))
        partitionSelectionHypothesis.totalAppliedRules = frozenset(self.totalAppliedRules | ruleApplicationHypothesis.get_applied_rules())
        partitionSelectionHypothesis.totalDiscardedRules = frozenset(self.totalDiscardedRules | ruleApplicationHypothesis.get_discarded_rules())
        partitionSelectionHypothesis.compute_score_with_num_rules()
        return partitionSelectionHypothesis
    
    def compute_score_with_num_rules(self):
        self.totalScore=self.totalScoreWithoutNum+  0.1*(RuleApplicationHypothesis.totalNumRules-len(self.totalDiscardedRules))/RuleApplicationHypothesis.totalNumRules
    
    def get_total_score(self):
        return self.totalScore
    
    def get_total_applied_rules(self):
        return self.totalAppliedRules
    
    def get_total_discarded_rules(self):
        return self.totalDiscardedRules
    
    @classmethod
    def set_parameters_for_minimised_num_rules(cls,p_numSentences,p_maxExecutedRules):
        cls.numSentences=p_numSentences
        cls.maxExecutedRules=p_maxExecutedRules
    
    def __hash__(self):
        return hash((self.totalAppliedRules,self.totalDiscardedRules,len(self.ruleAppliccationHyps)))
        #return hash(self.__repr__())
    
    def __cmp__(self,obj):
        return cmp(self.__hash__(),obj.__hash__())
    
class ParallelSentence(ruleLearningLib.AlignmentTemplate):
    def compute_coverages_and_bleu(self,ruleList,beamSize,boxesCoverage=False,boxesDic=dict(),allowIncompatibleRules=False):
        
        #debug("Keys in boxesDic: "+str(boxesDic.keys()))
        
        if boxesCoverage:
            RuleApplicationHypothesis.set_num_total_rules(len(boxesDic))
        else:
            RuleApplicationHypothesis.set_num_total_rules(len(ruleList))
        RuleApplicationHypothesis.set_max_length(ruleList.get_max_length())
        
        boxesInverseDic=dict()
        for key in boxesDic.keys():
            boxesInverseDic[boxesDic[key]]=key
        RuleApplicationHypothesis.set_inv_boxes_dict(boxesInverseDic) 
        
        #Compute rightmost TL word aligned with any SL word for each SL prefix
        tlprefixes=list()
        for i in range(len(self.sl_lexforms)):
            tlWordsAligned=[ a[1] for a in self.alignments if a[0] <= i ]
            rightmostTL=max(tlWordsAligned) if len(tlWordsAligned) > 0 else -1
            tlprefixes.append(self.parsed_tl_lexforms[:rightmostTL+1])
        
        #initialise pools
        #pool[i] is the result of processing the SL words in position < i
        #pool[0] contains the initial, empty hypothesis
        ruleApplicationPools=list()
        for i in range(len(self.parsed_sl_lexforms)+1):
            ruleApplicationPools.append([])
        ruleApplicationPools[0].append(RuleApplicationHypothesis(len(self.parsed_sl_lexforms)))

        #Beam search
        for i in range(len(self.parsed_sl_lexforms)):
            
            ##### DEBUG ####
            debug("")
            debug("Starting beam search step "+str(i))
            debug("Processed SL prefix:")
            debug(" ".join( lf.unparse().encode('utf-8') for lf in self.parsed_sl_lexforms[:i])) 
            debug(str(len(ruleApplicationPools[i]))+" hypothesis from previous steps before pruning")
            if i > 0:
                #score previous hypothesis and remove
                RuleApplicationHypothesis.score_hypotheses(ruleApplicationPools[i])
                ruleApplicationPools[i]=sorted(ruleApplicationPools[i],key=lambda h: h.get_score_with_num_rules() ,reverse=True)[:beamSize]
            debug(str(len(ruleApplicationPools[i]))+" hypothesis from previous steps after pruning")
            debug("Showing them:")
            if ruleLearningLib.DEBUG:
                position=0
                for hyp in ruleApplicationPools[i]:
                    position+=1
                    debug("\t"+str(position)+" "+hyp.to_str_for_debug().encode('utf-8'))
                    for id in hyp.get_applied_rules():
                        if boxesCoverage:
                            debug("\t\t"+str(id)+" "+boxesInverseDic[id])
                        else:
                            debug("\t\t"+str(id)+" "+unicode(ruleList.get_by_id(id)).encode('utf-8'))
            ##### DEBUG ####            
            
            #compute rules which match
            ruleMatchingList=ruleList.get_rules_matching_segment(self.parsed_sl_lexforms[i:],self.parsed_restrictions[i:])
            debug(str(len(ruleMatchingList))+" new rules/boxes can be applied")
            
            newPartialHypotheses=list()
            #for each rule, compute ats to be added to applied rules, ats to be discarded and resulting segment
            
            prevAppliedBoxes=set()
            for j in range(len(ruleMatchingList)):
                at=ruleMatchingList[j]
                if boxesCoverage:
                    boxid=boxesDic[at.get_pos_list_str()]
                    if not boxid in prevAppliedBoxes:
                        hyp=RuleApplicationHypothesis(len(self.parsed_sl_lexforms))
                        hyp.appliedRules.add(boxid)
                        for prevBox in prevAppliedBoxes:
                            hyp.discardedRules.add(prevBox)
                        hyp.set_processed_sl_words(len(at.parsed_sl_lexforms))
                        hyp.add_to_rules_list(boxid, at.get_pos_list_str().split("__"))
                        
                        #apply at to matching segment
                        tlsegment=at.apply(self.parsed_sl_lexforms[i:],self.tl_lemmas_from_dictionary[i:],self.parsed_restrictions[i:])
                        hyp.set_translation(tlsegment)
                        newPartialHypotheses.append(hyp)
                        
                        prevAppliedBoxes.add(boxid)
                else:
                    hyp=RuleApplicationHypothesis(len(self.parsed_sl_lexforms))
                    hyp.appliedRules.add(at.id)
                    for discAt in ruleMatchingList[:j]:
                        hyp.discardedRules.add(discAt.id)
                    hyp.set_processed_sl_words(len(at.parsed_sl_lexforms))
                    
                    #apply at to matching segment
                    tlsegment=at.apply(self.parsed_sl_lexforms[i:],self.tl_lemmas_from_dictionary[i:],self.parsed_restrictions[i:])
                    hyp.set_translation(tlsegment)
                    
                    newPartialHypotheses.append(hyp)

            #take into account also the case in which no rule is applied
            hyp=RuleApplicationHypothesis(len(self.parsed_sl_lexforms))
            if boxesCoverage:
                for boxid in prevAppliedBoxes:
                    hyp.discardedRules.add(boxid)
            else:
                for at in ruleMatchingList:
                    hyp.discardedRules.add(at.id)
            hyp.add_to_rules_list(RuleApplicationHypothesis.WORDFORWORDRULEID, [ self.parsed_sl_lexforms[i].get_pos() ])
            tlsegment=list()
            if self.parsed_sl_lexforms[i].is_unknown():
                bilDicTranslation=self.parsed_sl_lexforms[i]
            else:
                bilDicTranslation=AT_LexicalForm()
                bilDicTranslation.set_lemma(self.tl_lemmas_from_dictionary[i])
                bilDicTranslation.set_pos(self.parsed_sl_lexforms[i].get_pos())
                bilDicTags=list()
                bilDicTags.extend(self.parsed_restrictions[i].get_tags())
                bilDicTags.extend(self.parsed_sl_lexforms[i].get_tags()[len(bilDicTags):])
                bilDicTranslation.set_tags(bilDicTags)
            tlsegment.append(bilDicTranslation)
            hyp.set_translation(tlsegment)
            hyp.set_processed_sl_words(1)
            newPartialHypotheses.append(hyp)
            
            debug(str(len(newPartialHypotheses))+" new hypotheses to be combined with prev ones")
            
            #combine previous hypothesis
            for hyp in ruleApplicationPools[i]:
                for hyp2 in newPartialHypotheses:
                    if allowIncompatibleRules or hyp.can_be_combined_with(hyp2):
                        newhyp=hyp.create_new_combined_with(hyp2)
                        newhyp.set_source(self.parsed_sl_lexforms[:newhyp.get_processed_sl_words()])
                        newhyp.set_reference(tlprefixes[newhyp.get_processed_sl_words()-1])
                        ruleApplicationPools[newhyp.get_processed_sl_words()].append(newhyp)
        
        #final step
        ##### DEBUG ####
        debug("")
        debug("Finishing beam search")
        debug("Processed SL prefix: all")
        debug(" ".join( lf.unparse().encode('utf-8') for lf in self.parsed_sl_lexforms)) 
        debug(str(len(ruleApplicationPools[-1]))+" hypothesis from previous steps before pruning")
        #score previous hypothesis and remove
        RuleApplicationHypothesis.score_hypotheses(ruleApplicationPools[-1])
        ruleApplicationPools[-1]=sorted(ruleApplicationPools[-1],key=lambda h: h.get_score_with_num_rules(),reverse=True)[:beamSize]
        debug(str(len(ruleApplicationPools[-1]))+" hypothesis from previous steps after pruning")
        
        if ruleLearningLib.DEBUG:
            debug("Final result with score with num. rules")
            for ryp in ruleApplicationPools[-1]:
                debug(str(ryp.get_score_with_num_rules())+" -> "+unicode(ryp).encode('utf-8'))
        
        return ruleApplicationPools[-1]
