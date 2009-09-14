package org.apache.lucene.analysis;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

/**
 * Sets the position increment of each token according to it
 * offsets. Two or more tokens with the same start offsets will be in
 * the same position, the first one will have a position increment
 * equals to 1, the rest of token will have a position increment
 * equals to 0.
 *
 * @author Felipe Sanchez Martinez (fsanchez [at] dlsi [dot] ua [dot]
 * es)<br/>&copy; 2008 Universitat d'Alacant/Universidad de Alicante 
 */

public final class PositionsAccordingToOffsetsFilter extends TokenFilter {

  private int startOffset;
  
  /**
   * Construct a token stream filtering the given input.
   */
  public PositionsAccordingToOffsetsFilter(TokenStream input) {
    super(input);
    reset();
  }

  /**
   * Resets the status of this filter. The next token will have a
   * position increment equals to 1.
   */
  public final void reset() {
    startOffset = -1;
  }

  /**
   * Returns the next input Token after setting the postion increment
   * accordingly.
   */
  public final Token next() throws IOException {
    Token  token = input.next();

    if (token == null) {
      reset();
      return null;
    }

    if (token.startOffset() == startOffset)
      token.setPositionIncrement(0);

    startOffset = token.startOffset();

    return token;
  }
}
