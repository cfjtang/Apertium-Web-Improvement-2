/* 
 * An implementation of:
 * Improving Translation Quality by Discarding Most of the Phrasetable (Johnson,
 * Martin, Foster, Kuhn 2007)
 * 
 * LICENSING: as The Apertium Project (GNU Public License)
 *
 * -DDEBUG for debug symbols
 * -DOPTIM for the optimized version
 */
// Author: Gabriel Synnaeve
// Bits of code from Aurelien Mazurie (MIT License, about Fisher Exact Test).
//
// TODO replace "string"(s) by "string&" (dynamic alloc), to store only one time
// TODO optimized version with nested hashtables (-DOPTIM)
// TODO other optimizations ("lines", memory / disk access, loops)
// for instance, we could consume memory with lines not to reread the input

#include <fstream>
#include <iostream>
#include <sstream>
#include <map>
#include <vector>
#include <list>
#include <iterator>
#include <math.h>
#ifdef DEBUG
#include <assert.h>
#endif

using namespace std;

#ifdef DEBUG
// Nice print out of a map
void print_map(map<string, int>& m) 
{
    map<string, int>::iterator it;
    for (it = m.begin(); it != m.end(); it++) { 
        cout << it->first << " : " << it->second << endl;
    }
}
#endif

// Initialize or increment a map that count string occurences
void fill_map(map<string, int>& m, string s) 
{ 
    if (!m.count(s)) m[s] = 1;
    else m[s] += 1;
}

#ifdef OPTIM
// TODO
void update_map(map<string, int>& m, map<string, map<string, int> >& r) 
{
    for (map<string, int>::iterator it = m.begin(); it != m.end(); it++) {
        int tmp = 0;
        //map<string, bool> d;
        for (map<string, map<string, int> >::iterator i = r.begin(); 
                i != r.end(); i++) {
            if ((i->first).find(it->first) != string::npos
                    && (it->first).length() < (i->first).length()) { 
                tmp += i->second;
                //d[i->first] = true;
            }
        }
        it->second += tmp;
    }
}
#endif

// Logarithm of the number of combinations of 'n' objects taken 'k' at a time
double ln_n_choose_k (int n, int k)
{
    // TODO optimize that, low priority as it is not bottleneck
	return lgamma(n + 1) - lgamma(k + 1) - lgamma(n - k + 1);
}

/* Compute the hypergeometric distribution, or probability that a list of
 * n objects should contain i ones with a particular property when the
 * list has been selected randomly without replacement from a set of N
 * objects in which B exhibit the same property
 */
double hypergeometric_distribution (int i, int n, int B, int N)
{
	return exp(ln_n_choose_k(B, i) + ln_n_choose_k(N - B, n - i) - ln_n_choose_k(N, n));
}

// Computes the right_tailed pvalue for the Fisher's exact test
double fisher_exact_test (int b, int n, int B, int N)
{
#ifdef DEBUG
    // cout << b << ", " << n <<  ", " << B << ", " << N << ", " << endl;
	assert (b <= n);
	assert (n <= N);
	assert (B <= N);
	assert (b <= B);
#endif
	int um = min(n, B);
    int lm = max(0, n + B - N);
    // result(1.0, 1.0, 1.0);
    if (um == lm) return 1.0;
    double cutoff = hypergeometric_distribution(b, n, B, N);
    double right_tailed = 0.0;
    for (int i = lm; i <= um; ++i) {
        double p = hypergeometric_distribution(i, n, B, N);
		// if (i <= b) left_tailed += p; 
		if (i >= b) right_tailed += p;
		// if (p <= cutoff) two_tailed += p;
    }
    return min(right_tailed, 1.0);
}

// Makes sure that there is only ONE space in front and in queue of the string
void str_clean(string & s) 
{
    size_t p = s.find("  ");
    if (p != string::npos) s.replace(p, 2, " ");
    p = s.rfind("  ");
    if (p != string::npos) s.replace(p, 2, " ");
}

int main(int argc, char** argv) 
{
    map<string, int> count_s;
    map<string, int> count_t;
    map<pair<string, string>, list<int> > lines;
#ifdef OPTIM
    map<string, map<string, int> > dict_st;
    // TODO
#else
    map<pair<string, string>, int> count_st;
#endif
    if (argc != 3) {
        cerr << "Usage: ./pruning input_phrase_table output_name" << endl;
        return 1;
    }
    char* input_file = argv[1];
    char* output_file = argv[2];
    string input_line;
    int count_line = 0;
    size_t split1, split2;

    fstream file_in(input_file, ios::in);
    while(file_in) {
        getline(file_in, input_line);
        if (file_in.fail()) break; // finished, empty \n
        ++count_line; 
        split1 = input_line.find("|||");
        if (split1 == string::npos) { 
            cerr << "Wrong phrase-table format" << endl;
            return 1;
        }
        split2 = input_line.find("|||", split1 + 2);
        if (split2 == string::npos) { 
            cerr << "Wrong phrase-table format" << endl;
            return 1;
        }
        // prepare the lines with spaces in front and after
        string source = " " + input_line.substr(0, split1) + " ";
        string target = " " + input_line.substr(split1+3, split2 - split1 - 3)
            + " ";
        str_clean(source);
        str_clean(target);
        if (source.length() > 5 && target.length() > 5) {
            fill_map(count_s, source);
            fill_map(count_t, target);
            pair<string, string> p (source, target);
#ifdef OPTIM
            if (!dict_st.count(source)) {
                dict_st[source][target] = 1;
            } else { 
                // TODO
                fill_map(dict_st[source], target);
            }
#else
            if (!count_st.count(p)) {
                count_st[p] = 1;
            } else {
                count_st[p] += 1;
            }
#endif
            lines[p].push_back(count_line);
        }
    }
    file_in.close();
#ifdef OPTIM
    // TODO
    update_map(count_s, dict_st);
    update_map(count_t, dict_st);
#else
    for (map<string, int>::iterator it = count_s.begin(); 
            it != count_s.end(); it++) {
        int tmp = 0;
        for (map<pair<string, string>, int>::iterator i = count_st.begin(); 
                i != count_st.end(); i++) {
            if ((i->first.first).find(it->first) != string::npos
                    && (it->first).length() 
                    < (i->first.first).length()) { 
                tmp += i->second;
            }
        }
        it->second += tmp;
    }
    for (map<string, int>::iterator it = count_t.begin(); 
            it != count_t.end(); it++) {
        int tmp = 0;
        for (map<pair<string, string>, int>::iterator i = count_st.begin(); 
                i != count_st.end(); i++) {
            if ((i->first.second).find(it->first) != string::npos
                    && (it->first).length() 
                    < (i->first.second).length()) { 
                tmp += i->second;
            }
        }
        it->second += tmp;
    }
    map<pair<string, string>, int> next_count_st;
    for (map<pair<string, string>, int>::iterator it = count_st.begin(); 
            it != count_st.end(); it++) { 
        int tmp = 0;
        map<pair<string, string>, int>::iterator i;
        for (i = count_st.begin(); i != count_st.end(); i++) {
            if ((i->first.first).find(it->first.first) != string::npos
                    && (it->first.first).length() 
                    < (i->first.first).length()
                    && (i->first.second).find(it->first.second) != string::npos
                    && (it->first.second).length()
                    < (i->first.second).length()) { 
                tmp += i->second;
            }
        }
        next_count_st[it->first] = it->second + tmp;
    }
    count_st = next_count_st;
#endif


#ifdef DEBUG
    cout << " ==============COUNT_S============== " << endl; 
    print_map(count_s);
    cout << " ==============COUNT_T============== " << endl; 
    print_map(count_t);
#ifdef OPTIM
    cout << " ==============DICT_ST============== " << endl; 
    map<string, map<string, int> >::iterator i;
    for (i = dict_st.begin(); i != dict_st.end(); i++) { 
        cout << i->first << " -> ";
        print_map(i->second);
    }
#else
    cout << " ==============COUNT_ST============== " << endl; 
    for (map<pair<string, string>, int>::iterator i = count_st.begin(); i != count_st.end(); i++) { 
        cout << i->first.first << ", " << i->first.second 
            << " : " << i->second << endl;
    }
#endif // OPTIM
#endif // DEBUG
    double threshold = log(count_line) - 0.01;
    vector<int> to_delete;
#ifdef DEBUG
    cout << ">>> Threshold: " << threshold << endl;
#endif
#ifdef OPTIM
    // TODO
#else
    for (map<pair<string, string>, int>::iterator i = count_st.begin();
            i != count_st.end(); i++) {
        if (i->second > count_s[i->first.first]) {
            cout << i->first.first << ", " << i->first.second << endl;
            cout << i->second << ", " << count_s[i->first.first] << " | " << count_t[i->first.second] << endl;
            return 1;
        }
        if (-log(fisher_exact_test(i->second, count_s[i->first.first], 
                count_t[i->first.second], count_line)) > threshold) {
            list<int>::iterator il;
            for (il = lines[i->first].begin(); 
                    il != lines[i->first].end(); il++) { 
                to_delete.push_back(*il);
            }
        }
    }
#endif

    sort (to_delete.begin(), to_delete.end()); 
#ifdef DEBUG
    cout << ">>> To delete: ";
    for (vector<int>::iterator i = to_delete.begin(); 
            i != to_delete.end(); i++) {
        cout << *i << ", ";
    }
    cout << endl;
#endif

    fstream file_op(output_file, ios::out);
    file_in.open(input_file, ios::in);
    count_line = 0;
    while(file_in) {
        getline(file_in, input_line);
        if (cin.fail()) break; // finished, empty \n
        ++count_line; 
        if (!binary_search(to_delete.begin(), to_delete.end(), count_line))
            file_op << input_line << endl;
    }
    file_in.close();
    file_op.close();
    cout << "Pruned phrase-table: " << output_file << endl;
    return 0;
}
