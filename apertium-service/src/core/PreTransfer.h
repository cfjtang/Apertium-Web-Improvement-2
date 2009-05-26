#ifndef PRETRANSFER_H_
#define PRETRANSFER_H_

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <libgen.h>
#include <string>

#include <lttoolbox/lt_locale.h>
#ifdef WIN32
#if defined(__MINGW32__)
#define __MSVCRT_VERSION__  0x0800
#endif
#include <io.h>
#include <fcntl.h>
#endif
#include <apertium/string_utils.h>

class PreTransfer {
public:
	PreTransfer();
	virtual ~PreTransfer();

	void processStream(FILE*, FILE*);

private:
	void readAndWriteUntil(FILE*, FILE*, int const);
	void procWord(FILE*, FILE*);
};

#endif /* PRETRANSFER_H_ */
