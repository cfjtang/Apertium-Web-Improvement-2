package dictools.guessparadigm.paradigms;

import dics.elements.dtd.*;
import dictools.guessparadigm.suffixtree.Pair;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class which represents a paradigm from a dictionary of Apertium
 * @author Miquel Esplà i Gomis
 */
public class Paradigm implements Serializable{
    /** Name of the paradigm */
    private String name;

    /** List of suffixes generated by the paradigm */
    private Set<Suffix> suffixes;

    /**
     * Overloaded constructor of the class
     * @param par Pardef object from which the paradigm is read
     * @param dic Dictionary from which the paradigm is read
     */
    public Paradigm(Pardef par, Dictionary dic){
        this.name=par.name;
        suffixes=new LinkedHashSet<Suffix>();
        Set<Pair<StringBuilder,List<String>>> lstmp=
                new LinkedHashSet<Pair<StringBuilder,List<String>>>();
        lstmp.add(new Pair<StringBuilder,List<String>>(new StringBuilder(),new LinkedList<String>()));
        Set<Pair<String,List<String>>> sufftmp=BuildSuffixes(par.elements,lstmp,dic);
        for(Pair<String,List<String>> s: sufftmp){
            Suffix suf=new Suffix(s.getFirst(),s.getSecond());
            this.suffixes.add(suf);
        }
    }

    /**
     * Method that generates all the possible suffixes
     * @param elements List of elements from which the suffixes are read
     * @param currentLexicalForms List of paradigms pre-generated
     * @param dic Dictionary from which the paradigm is read
     * @return Returns the list of suffixes generated by the paradigm
     */
    private Set<Pair<String,List<String>>> BuildSuffixes(List<E> elements,
            Set<Pair<StringBuilder,List<String>>> currentLexicalForms, Dictionary dic)
    {
         List<Pair<String,List<String>>> localList=new LinkedList<Pair<String,List<String>>>();
         for(E element: elements)
         {
             Set<Pair<StringBuilder,List<String>>> listgeneratedByElement=
                     new LinkedHashSet<Pair<StringBuilder,List<String>>>();
             for (DixElement e: element.children)
             {
                 if (e instanceof P){
                     if(listgeneratedByElement.isEmpty())
                         listgeneratedByElement.add(new Pair(new StringBuilder(""),new LinkedList<String>()));
                     for(Pair<StringBuilder,List<String>> b: listgeneratedByElement){
                        b.getFirst().append(((P)e).l.getValueNoTags());
                        for(DixElement subde: ((P)e).r.children){
                            if(subde instanceof S)
                                b.getSecond().add(((S)subde).name);
                        }
                     }
                 }
                 else if (e instanceof Par)
                 {
                     List<E> parElements=dic.pardefs.getParadigmDefinition(((Par)e).name).elements;
                     Set<Pair<String,List<String>>> resultList=BuildSuffixes(parElements,
                             listgeneratedByElement, dic);
                     listgeneratedByElement.clear();
                     for(Pair<String,List<String>> r: resultList)
                         listgeneratedByElement.add(new Pair(new StringBuilder(r.getFirst()),
                         new LinkedList<String>()));
                 }
             }
             for(Pair<StringBuilder,List<String>> b: listgeneratedByElement)
                 localList.add(new Pair(b.getFirst().toString(),b.getSecond()));
         }

         //Combine lists
         Set<Pair<String,List<String>>> finalList = new LinkedHashSet<Pair<String,List<String>>>();
         for(Pair<StringBuilder,List<String>> lexHead: currentLexicalForms)
             for(Pair<String,List<String>> lexTail: localList){
                 List<String> newlist=new LinkedList<String>(lexHead.getSecond());
                 newlist.addAll(lexTail.getSecond());
                 finalList.add(new Pair<String,List<String>>(lexHead.getFirst()+
                         lexTail.getFirst(),newlist));
             }

         return finalList;
    }

    /**
     * Method that returns the name of the paradigm
     * @return Returns the name of the paradigm
     */
    public String getName() {
        return name;
    }

    /**
     * Method that returns the list of suffixes generated by the paradigm
     * @return Returns the list of suffixes generated by the paradigm
     */
    public Set<Suffix> getSuffixes(){
        return this.suffixes;
    }
}
