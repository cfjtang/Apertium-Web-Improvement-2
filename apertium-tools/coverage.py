#!/usr/bin/env python3

import argparse, subprocess, logging, urllib.request, tempfile

def getFileType(filePath):
    magicNumbers = {
        '\x1f\x8b\x08': 'gz',
        '\x42\x5a\x68': 'bz2',
        '\x50\x4b\x03\x04': 'zip'
    }

    with open(filePath, 'rb') as f:
        fileStart = f.read(max(map(lambda x: len(x), magicNumbers.values())))
    for magicNumber, fileType in magicNumbers.items():
        if fileStart.decode('utf-8').startswith(magicNumber):
            return fileType

    return 'txt'

def getFile(corpusUri):
    if 'http' in corpusUri:
        try:
            corpusPath, _ = urllib.request.urlretrieve(args.corpusUri)
        except urllib.error.HTTPError:
            logger.critical('Corpus %s not found' % args.corpusUri)
            sys.exit(-1)
    else:
        corpusPath = corpusUri

    return corpusPath

def getCoverage(fileType, corpusPath):
    catCommands = {
        'txt': 'cat',
        'gz': 'zcat',
        'bz2': 'bzcat',
        'zip': 'unzip -p'
    }

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

    tempFile.seek(0)
    p1 = subprocess.Popen(['grep', '-v', '*'], stdout=subprocess.PIPE, stdin=tempFile)
    known = int(subprocess.check_output(['wc', '-l'], stdin=p1.stdout))

    tempFile.close()

    return known, total

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Determine coverage of a transducer on a corpus')
    parser.add_argument('automorfPath', help='path to automorf binary')
    parser.add_argument('corpusUri', help='corpus uri')
    args = parser.parse_args()

    logger = logging.getLogger("coverage")
    logging.basicConfig(level=logging.INFO)

    corpusPath = getFile(args.corpusUri)
    fileType = getFileType(corpusPath)
    logger.info('Treating corpus as %s' % fileType)
    known, total = getCoverage(fileType, corpusPath)

    print('Total: %s' % total)
    print('Known: %s' % known)
    print('Coverage: %.10f%%' % float(known / total * 100.0))

