g++ -L/usr/local/lib -o ApertiumServer src/*.cpp src/core/*.cpp -I/usr/include/libxml++-2.6/ \
-I/usr/local/include/lttoolbox-3.1/ -I/usr/local/include/apertium-3.1/ -I/usr/lib/libxml++-2.6/include/ \
-I/usr/include/libxml2 -I/usr/include/glibmm-2.4 -I/usr/lib/glibmm-2.4/include -I/usr/include/sigc++-2.0 \
-I/usr/lib/sigc++-2.0/include -I/usr/include/glib-2.0 -I/usr/lib/glib-2.0/include -I/usr/local/include/ -liqxmlrpc \
-llttoolbox3 -lapertium3 -lboost_regex-mt -lglib -lboost_program_options-mt -lboost_filesystem-mt -lxml2 \
-lgobject-2.0 -lboost_thread-mt -lsigc-2.0 -lglib-2.0 -lxml++-2.6 -liqxmlrpc-server -Isrc/ -Isrc/core/ -Isrc/format/
