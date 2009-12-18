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
        string const serverUrl("http://localhost:8080/RPC2");
        string const methodName("translate");

        xmlrpc_c::clientSimple myClient;
        xmlrpc_c::value result;

        myClient.call(serverUrl, methodName, "sss", &result, "test", "en", "es");

        string ret = xmlrpc_c::value_string(result);
            // Assume the method returned an integer; throws error if not

        cout << "Translation: " << ret << endl;

    } catch (exception const& e) {
        cerr << "Client threw error: " << e.what() << endl;
    } catch (...) {
        cerr << "Client threw unexpected error." << endl;
    }

    return 0;
}
