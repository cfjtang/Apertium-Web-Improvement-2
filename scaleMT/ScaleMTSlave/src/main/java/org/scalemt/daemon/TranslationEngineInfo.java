/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.daemon;

import org.scalemt.rmi.transferobjects.DaemonConfiguration;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author vitaka
 */
public class TranslationEngineInfo implements Serializable{

    private String name;
    private Set<DaemonConfiguration> configurations;
    private boolean deformatOutFromPipeline;
    private Map<Integer,VariableType> variables;
    private List<Program> programs;
    private TranslationCore translationCore;

    public TranslationEngineInfo() {
        deformatOutFromPipeline=false;
        variables=new HashMap<Integer,VariableType>();
        programs=new ArrayList<Program>();
        configurations=new HashSet<DaemonConfiguration>();
    }

    public boolean isDeformatOutFromPipeline() {
        return deformatOutFromPipeline;
    }

    public void setDeformatOutFromPipeline(boolean deformatOutFromPipeline) {
        this.deformatOutFromPipeline = deformatOutFromPipeline;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, VariableType> getVariables() {
        return variables;
    }

    public void setVariables(Map<Integer, VariableType> variables) {
        this.variables = variables;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DaemonConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<DaemonConfiguration> configurations) {
        this.configurations = configurations;
    }


    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public TranslationCore getTranslationCore() {
        return translationCore;
    }

    public void setTranslationCore(TranslationCore translationCore) {
        this.translationCore = translationCore;
    }

}
