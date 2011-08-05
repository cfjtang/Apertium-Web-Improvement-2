lt-expand apertium-eo-fr.eo-fr.dix  | sed 's/:>:/:/g' | sed 's/:<:/:/g' | cut -f2 -d':' | cut -f1 -d'<' | apertium-destxt | lt-proc fr-eo.automorf.bin  | grep '*'
