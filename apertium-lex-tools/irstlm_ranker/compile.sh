g++ main.cpp -I /usr/local/include/lttoolbox-3.2/ -I /usr/local/include/apertium-3.2/ -I /usr/include/libxml2/ -llttoolbox3 -lxml2 -lapertium3 -lz -I include/ source/*.cpp -lirstlm -lz -lpcre -L/usr/local/lib -o irstlm-ranker

