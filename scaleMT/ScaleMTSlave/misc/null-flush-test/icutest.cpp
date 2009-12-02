// ICU includes
#include <unicode/uchar.h>
#include <unicode/uclean.h>
#include <unicode/ustdio.h>
#include <unicode/utypes.h>
#include <unicode/uloc.h>
#include <unicode/uenum.h>
#include <unicode/ucnv.h>
#include <unicode/utrans.h>
#include <unicode/ustring.h>
#include <unicode/uregex.h>

using namespace std;

UChar u_fgetc_null(UFILE* file)
{
	UErrorCode error=U_ZERO_ERROR;
	UConverter* converter = ucnv_open("UTF-8",&error);
	if(U_FAILURE(error))
		fprintf(stderr,"Error creating converter: %d\n",error);

	int ch;
	char inputbuf[5];
	UChar outputbuf[5];
	int result;
	int inputsize=0;

	do
	{
		ch = fgetc(u_fgetfile(file));
		fprintf(stdout,"read %c(%d)\n",ch,ch);
		if(ch==0)
			return 0;
		else
		{
			inputbuf[inputsize]=ch;
			inputsize++;
			result = ucnv_toUChars(converter,outputbuf,5,inputbuf,inputsize,&error);
			if(U_FAILURE(error))
				fprintf(stderr,"Error conversion: %d\n",error);
			
			
		}
	}
	while(( ((result>=1 && outputbuf[0]==0xFFFD))  || U_FAILURE(error) )&& !u_feof(file));
	
	return outputbuf[0];
}


int 
main(int argc, char *argv[])
{
	/*
	char ch;
	while(!feof(stdin))
	{
		ch = fgetc(stdin);
		fprintf(stdout,"char=%c(%d)\n",ch,ch);
		fflush(stdout);
	}
*/	

	
	UFILE *ux_stdin = 0;
	UFILE *ux_stdout = 0;
	UFILE *ux_stderr = 0;

	const char *codepage_default = ucnv_getDefaultName();
	ucnv_setDefaultName("UTF-8");
	const char *locale_default = "en_US_POSIX"; //uloc_getDefault();

	ux_stdin = u_finit(stdin, locale_default, codepage_default);
	ux_stdout = u_finit(stdout, locale_default, codepage_default);
	ux_stderr = u_finit(stderr, locale_default, codepage_default);

	u_fprintf(ux_stdout, "%s %s\n",u_fgetcodepage(ux_stdin),u_fgetlocale(ux_stdin));


	UChar c;
	char ch;
	
	while(!u_feof(ux_stdin))
	{
		c=u_fgetc_null(ux_stdin);
		u_fprintf(ux_stdout, "c=%C(%d)\n", c,c);
		if(c==0)
			u_fflush(ux_stdout);
	}

	/*
	while(!u_feof(ux_stdin))
	{
		c=u_fgetcx(ux_stdin);
		if(c==U_EOF)
		{
			u_fprintf(ux_stdout, "EOF\n");
			u_fclose(ux_stdin);
			ch = fgetc(stdin);
			u_fprintf(ux_stdout,"char=%c(%d)\n",ch,ch);
			ux_stdin = u_finit(stdin, locale_default, codepage_default);	
		}
		else
			u_fprintf(ux_stdout, "c=%C(%d)\n", c,c);
		u_fflush(ux_stdout);
	}
	*/
}