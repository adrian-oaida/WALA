/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.cast.js.translator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.cast.tree.impl.AbstractSourcePosition;

public class RangePosition extends AbstractSourcePosition implements Position {
  private final static int TABSIZE = 4;
  private final URL url;
  private final int line;
  private final int startOffset;
  private final int endOffset;

  private int col = -1;
  private int lastLine = -1;
  private int lastCol = -1;

  public RangePosition(URL url, int line, int startOffset, int endOffset) {
    super();
    this.url = url;
    this.line = line;
    this.startOffset = startOffset;
    this.endOffset = endOffset;
  }

  @Override
  public int compareTo(Object o) {
    Position other = (Position) o;
    if (startOffset != other.getFirstOffset()) {
      return startOffset - other.getFirstOffset();
    } else {
      return endOffset - other.getLastOffset();
    }
  }

  @Override
  public int getFirstLine() {
    return line;
  }

  @Override
  public int getLastLine() {
    if (lastLine == -1) {
      String content;
      Reader reader = null;
      try {
        reader = getReader();
        content = IOUtils.toString(reader).substring(startOffset, endOffset);
        int nrOfNewlines = content.length() - content.replace("\n", "").length();
        lastLine = line + nrOfNewlines;
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.closeQuietly(reader);
      }
    }

    return lastLine;
  }

  @Override
  public int getFirstCol() {
    if (col == -1) {
      col = getCol(getFirstLine(), startOffset);
    }

    return col;
  }

  @Override
  public int getLastCol() {
    if (lastCol == -1) {
      lastCol = getCol(getLastLine(), endOffset);
    }

    return lastCol;
  }

  private int getCol(int line, int offset) {
    int col = -1;
    Reader reader = null;
    try {
      reader = getReader();
      col = getCol(IOUtils.toString(reader), line, offset);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(reader);
    }

    return col;
  }

  public static int getCol(String content, int line, int offset) {
    if (line == 1) {
      return offset + 1;
    }
    int pos = -1;
    for (int i = 0; i < line - 1; i++) {
      pos = content.indexOf('\n', pos + 1);
    }

    try {
      String lineBeginning = content.substring(pos, offset);
      int nrOfTabs = lineBeginning.length() - lineBeginning.replace("\t", "").length();
      return offset - pos + (TABSIZE - 1) * nrOfTabs;
    } catch (StringIndexOutOfBoundsException e) {
      e.printStackTrace();
      return -1;
    }
  }

  @Override
  public int getFirstOffset() {
    return startOffset;
  }

  @Override
  public int getLastOffset() {
    return endOffset;
  }

  @Override
  public URL getURL() {
    return url;
  }

  @Override
  public Reader getReader() throws IOException {
    return new InputStreamReader(url.openStream());
  }
}
