./icetaggerClientApertium.sh $1 | apertium-pretransfer | apertium-transfer apertium-is-en.is-en.t1x  is-en.t1x.bin  is-en.autobil.bin  | apertium-interchunk apertium-is-en.is-en.t2x  is-en.t2x.bin | apertium-postchunk apertium-is-en.is-en.t3x is-en.t3x.bin | lt-proc -g is-en.autogen.bin | lt-proc -p is-en.autopgen.bin
