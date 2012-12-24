def call(cmdline, _in):
    from subprocess import Popen, PIPE

    """A convenience function to invoke a subprocess with the
    parameter list name (where the first argument is the name
    of an executable). The subprocess is fed the contents of
    input via stdin. We collect the output of both stdout and
    stderr from the subprocess. If stderr is not empty, we
    raise an exception, otherwise we return the contents of
    stdout."""
    
    #Refer to http://docs.python.org/3.3/library/subprocess.html
    p = Popen(" ".join(cmdline), shell=True,
        stdin=PIPE, stdout=PIPE, stderr=PIPE,
        close_fds=True)
    
    (child_in, child_out, child_err) = (p.stdin, p.stdout, p.stderr)
    #child_in, child_out, child_err = os.popen3(" ".join(cmdline))

    #p.communicate(input=_in)
    #(out, err) = p.communicate()
    child_in.write(bytes(_in))
    child_in.close() # You MUST close the child's stdin to get output from some programs

    out = child_out.read()
    child_out.close()
    err = child_err.read()
    child_err.close()

    return out, err

class OsCommand(object):
    def __init__(self, cmd):
        self.cmd = cmd

    def communicate(self, _input):
        print("calling", self.cmd)
        return call(self.cmd, _input)

