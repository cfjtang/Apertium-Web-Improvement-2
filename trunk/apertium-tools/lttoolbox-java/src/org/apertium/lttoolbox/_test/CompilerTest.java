package org.apertium.lttoolbox._test;

import junit.framework.TestCase;
import org.apertium.lttoolbox.Compiler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Nic Cottrell, Feb 8, 2009 5:19:44 PM
 */

public class CompilerTest extends TestCase {

  public void test_compile1() throws IOException, SAXException {

    org.apertium.lttoolbox.Compiler c = new Compiler();
    c.parse("test/sample.eo-en.dix", Compiler.COMPILER_RESTRICTION_LR_VAL);

    final StringWriter sw = new StringWriter();
    c.write(sw);
    sw.close();

    System.out.println(sw.toString());
  }

}
