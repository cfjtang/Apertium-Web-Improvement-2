/*
 * g++ test.cc -o test -lxmlrpc_client++ -lxmlrpc++ -lxmlrpc_client -lxmlrpc_cpp -lxmlrpc_xmlparse -lxmlrpc_xmltok -lxmlrpc_server
 */

#include <cstdlib>
#include <string>
#include <iostream>
#include <xmlrpc-c/girerr.hpp>
#include <xmlrpc-c/base.hpp>
#include <xmlrpc-c/client_simple.hpp>

using namespace std;

int
main(int argc, char **) {

    if (argc-1 > 0) {
        cerr << "This program has no arguments" << endl;
        exit(1);
    }

    try {
        string const serverUrl("http://localhost:6173/RPC2");
        string const methodName("translate");

        xmlrpc_c::clientSimple myClient;
        xmlrpc_c::value result;

        myClient.call(serverUrl, methodName, "sss", &result, "test", "en", "es");

        map<string, xmlrpc_c::value> const resultStruct = xmlrpc_c::value_struct(result);
        map<string, xmlrpc_c::value>::const_iterator iter = resultStruct.find("translation");

        string ret = (string)xmlrpc_c::value_string(iter->second);

        cout << "Translation: " << ret << endl;

    } catch (exception const& e) {
        cerr << "Client threw error: " << e.what() << endl;
    } catch (...) {
        cerr << "Client threw unexpected error." << endl;
    }

    return 0;
}
