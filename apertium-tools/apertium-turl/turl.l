%{
/*
 * Archivos de cabecera
 */
#include <iostream>
#include <string>
#include <cctype>

using namespace std;

/*
 * Definición de tipos
 */
enum etiqueta {ninguno=0,FRAME,A};

/*
 * Variables globales
 */
bool comentario=false;
etiqueta tagR=ninguno;
bool prefijo=false;
string url_traductora;
string url_codebase;

/*
 * Funciones
 */

void toupper(string &s)
{
  for(int i=0;i<s.size();i++)
    s[i]=toupper(s[i]);
}

bool endsWith(const string &s, const string &t){
  if(s.size()<t.size())
  {
    return false;
  }
  else if(s.substr(s.size()-t.size())==t)
  {
    return true;
  }
  else return false;
}

bool endsWithi(const string &s, const string &t)
{
  string a=s, b=t;
  toupper(a);
  toupper(b);
  return endsWith(a,b);
}

bool eqi(const string &s, const string &t){
  string a=s, b=t;
  toupper(a);
  toupper(b);
  return a==b;
}


string dominio(string s){
  for(unsigned int i=7;i<s.size();i++){
    if(s[i]=='/')
      return s.substr(0,i);
  }
  return s.substr(0,s.size()-1);
}

string path(string s){
  for(int i=s.size()-1;;i--){
    if(s[i]=='/')
      return s.substr(0,i+1);
  }
  return s;
}

/* elimina los directorios . y .. para formar una URL absoluta */
string simplificarPath(string s){
  for(unsigned int i=0;i<s.size()-3;i++){
    if(s[i]=='/' && s[i+1]=='.' && s[i+2]=='/')
      return simplificarPath(s.substr(0,i)+s.substr(i+2,s.size()));
    else if(s[i]=='/' && s[i+1]=='.' && s[i+2]=='.' && s[i+3]=='/'){
      for(int j=i-1;j>0;j--)
        if(s[j]=='/')
          return simplificarPath(s.substr(0,j)+s.substr(i+3,s.size()));
    }
  }
  return s;
}

/* codifica caracteres conflictivos y procesa las direcciones para
transformarlas a absolutas cuando sea posible y/o deseable */
string transformar(string s){
  /* si es un simbolo reservado (RFC 1808), codificarlo 
     salvo ':' y '/' porque son relevantes.  Tambien se ha añadido el '%' */
  for(unsigned int i=0;i<s.size();i++){
    if(s[i]==';'||s[i]=='@'||s[i]=='?'||s[i]=='='||s[i]=='%'||s[i]=='&'){
      char codigo[3];
      sprintf(codigo,"%x",s[i]);
      s=s.substr(0,i)+"\%"+codigo+s.substr(i+1);
      i++;
    }
  }
  
  /* no procesar ni href "mailto:" ni href "javascript:" ni "ftp:" o abrir
     conexiones https */
  /* ni que acaben en pdf o ps */
  if(eqi(s.substr(0,7),"mailto:") || eqi(s.substr(0,11),"javascript:") ||
     eqi(s.substr(0,4),"ftp:") || eqi(s.substr(0,6),"https:"))
     return s;

  else if(endsWithi(s,".pdf") || endsWithi(s,".exe") ||endsWithi(s,".ps") ||
          endsWithi(s,".tgz") || endsWithi(s,".tar") ||endsWithi(s,".gz") ||
          endsWithi(s,".bz2") || endsWithi(s,".zip") ||endsWithi(s,".ra") ||
          endsWithi(s,".ram") || endsWithi(s,".avi") ||endsWithi(s,".doc")||
          endsWithi(s,".mov") || endsWithi(s,".pps") ||endsWithi(s,".xls")||
          endsWithi(s,".asf") || endsWithi(s,".rtf") ||endsWithi(s,".gif")||
          endsWithi(s,".jpeg")|| endsWithi(s,".jpg") ||endsWithi(s,".png")||
          endsWithi(s,".bmp") || endsWithi(s,".rar"))
     return s;
/*
     ||s.substr(0,11)=="JavaScript:"||s.substr(0,4)=="ftp:") return s;
*/
  /* anclas */
  else if(s[0]=='#') return simplificarPath(url_traductora+url_codebase+s);
  
  /* relativas */
  if(s.substr(0,7)!="http://"){
    if(s[0]=='/') return simplificarPath(url_traductora+dominio(url_codebase)+s);
    else return simplificarPath(url_traductora+path(url_codebase)+s);
  }
  
  /* absolutas */
  else return simplificarPath(url_traductora+s);
}

%}

BLANCOS		[ \t\n\x0d]*
SEQESCAPE	\\[\x00-\xff]

%option noyywrap

%%
"<!--"([^-]|"-"[^-]|"--"[^>])*"-->"		{prefijo=false; /*comentario=true;*/ cout<<yytext;}
"<"[Ss][Cc][Rr][Ii][Pp][Tt][^\>]*">"([^\<]|"<"[^\/]|"</"[^Ss]|"</"[Ss][^Cc]|"</"[Ss][Cc][^Rr]|"</"[Ss][Cc][Rr][^Ii]|"</"[Ss][Cc][Rr][Ii][^Pp]|"</"[Ss][Cc][Rr][Ii][Pp][^Tt]|"</"[Ss][Cc][Rr][Ii][Pp][Tt][^\>])*"</"[Ss][Cc][Rr][Ii][Pp][Tt]">"	{prefijo=false; cout<<yytext;}
[Hh][Rr][Ee][Ff]{BLANCOS}"="{BLANCOS}	{if(comentario) cout<<yytext; else{prefijo=false; cout<<yytext; if(tagR==A) prefijo=true;}}
[Ss][Rr][Cc]{BLANCOS}"="{BLANCOS}	{if(comentario) cout<<yytext; else{prefijo=false; cout<<yytext; if(tagR==FRAME&&!comentario) prefijo=true;}}
\"({SEQESCAPE}|[^\"])*\"|\'({SEQESCAPE}|[^\'])*\'	{if(comentario) cout<<yytext; else{if(prefijo){yytext[strlen(yytext)-1]=0; cout<<"\""<<transformar(yytext+1)<<"\"";} else cout<<yytext; if(!comentario) prefijo=false;}}
"<"[Aa]				{if(comentario) cout<<yytext; else{prefijo=false; cout<<yytext; tagR=A;}}
"<"[Ff][Rr][Aa][Mm][Ee]		{if(comentario) cout<<yytext; else{prefijo=false; cout<<yytext; tagR=FRAME;}}
">"				{if(comentario) cout<<yytext; else{prefijo=false; cout<<yytext; tagR=ninguno;}}
({SEQESCAPE}|[^ \n\t\x0d=<>])+	{if(comentario) cout<<yytext; else{if(prefijo) cout<<"\""<<transformar(yytext)<<"\""; else cout<<yytext; prefijo=false;}}
{BLANCOS}|.	{cout<<yytext;}
%%

/* 
 * Función principal 
 */
int main(int argc, char *argv[])	
	{

	  
          //
          // Comprobar los parámetros y ejecutar el análisis
          //
          ++argv; --argc;


          if(argc==2){
            url_traductora=argv[0];
            url_codebase=argv[1];
            yyin=stdin;
          }
          else{
            yyin=fopen(argv[2],"r");
            if(yyin==NULL){
	      cerr<<"US: turl URL_TRAD URL_CODEBASE [arxiu]"<<endl;
              exit(0);
	    }
            url_traductora=argv[0];
            url_codebase=argv[1];
          } 
	  yylex();
          return 0;
	}
