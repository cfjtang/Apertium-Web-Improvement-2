#!/usr/bin/env python3

import argparse, subprocess, logging, urllib.request, tempfile

magicNumbers = {
    '\x1f\x8b\x08': 'gz',
    '\x42\x5a\x68': 'bz2',
    '\x50\x4b\x03\x04': 'zip'
}

catCommands = {
    'txt': 'cat',
    'gz': 'zcat',
    'bz2': 'bzcat',
    'zip': 'unzip -p'
}

def getFileType(filePath):
    with open(filePath, 'rb') as f:
        fileStart = f.read(max(map(lambda x: len(x), magicNumbers.values())))
    for magicNumber, fileType in magicNumbers.items():
        if fileStart.decode('utf-8').startswith(magicNumber):
            return fileType

    return 'txt'

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Determine coverage of a transducer on a corpus')
    parser.add_argument('automorfPath', help='path to automorf binary')
    parser.add_argument('corpusUri', help='corpus uri')
    args = parser.parse_args()

    logger = logging.getLogger("coverage")
    logging.basicConfig(level=logging.INFO)

    if 'http' in args.corpusUri:
        try:
            corpusPath, _ = urllib.request.urlretrieve(args.corpusUri)
        except urllib.error.HTTPError:
            logger.critical('Corpus %s not found' % args.corpusUri)
            sys.exit(-1)
    else:
        corpusPath = args.corpusUri

    fileType = getFileType(corpusPath)
    logger.info('Treating corpus as %s' % fileType)
    tempFile = tempfile.TemporaryFile()

    p1 = subprocess.Popen([catCommands[fileType], corpusPath], stdout=subprocess.PIPE)
    p2 = subprocess.Popen(['apertium-destxt'], stdin=p1.stdout, stdout=subprocess.PIPE)
    del p1
    p3 = subprocess.Popen(['lt-proc', '-w', args.automorfPath], stdin=p2.stdout, stdout=subprocess.PIPE)
    del p2
    p4 = subprocess.Popen(['apertium-retxt'], stdin=p3.stdout, stdout=subprocess.PIPE)
    del p3
    subprocess.Popen(['sed', r's/\$\W*\^/$\n^/g'], stdin=p4.stdout, stdout=tempFile).communicate()
    del p4

    tempFile.seek(0)
    total = int(subprocess.check_output(['wc', '-l'], stdin=tempFile))
    print('Total: %s' % total)

    tempFile.seek(0)
    p1 = subprocess.Popen(['grep', '-v', '*'], stdout=subprocess.PIPE, stdin=tempFile)
    known = int(subprocess.check_output(['wc', '-l'], stdin=p1.stdout))
    print('Known: %s' % known)

    print('Coverage: %.10f%%' % float(known / total * 100.0))

    tempFile.close()
