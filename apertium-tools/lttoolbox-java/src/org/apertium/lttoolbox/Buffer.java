package org.apertium.lttoolbox;

/**
 * Created by Nic Cottrell, Jan 27, 2009 4:47:04 PM
 */

public class Buffer {

  /**
   * Buffer size.
   */
  int size;

  /**
   * Buffer array.
   */
  char[] buf;

  /**
   * Buffer current position.
   */
  int currentpos;

  /**
   * Last position.
   */
  int lastpos;

  void copy(Buffer b) {
    currentpos = b.currentpos;
    lastpos = b.lastpos;
    size = b.size;
    buf = new char[size];
    System.arraycopy(buf, 0, b.buf, 0, size);
  }

  /**
   * Constructor
   *
   * @param buf_size buffer size
   */
  Buffer(int buf_size) {
    if (buf_size == 0) {
      throw new RuntimeException("Error: Cannot create empty buffer.");

    }
    buf = new char[buf_size];
    size = buf_size;
    currentpos = 0;
    lastpos = 0;
  }

  /**
   * Copy constructor.
   */
  Buffer(Buffer b) {
    copy(b);
  }

  /**
   * Add an element to the buffer.
   *
   * @param value the value.
   * @return reference to the stored object.
   */
  char add(char value) {
    if (lastpos == size) {
      lastpos = 0;
    }
    buf[lastpos++] = value;
    currentpos = lastpos;
    return buf[lastpos - 1];
  }


  /**
   * Add an element to the buffer and not modify its content, getting the
   * next free reference
   *
   * @return reference to the stored object.
   */
  char add() {
    if (lastpos == size) {
      lastpos = 0;
    }
    currentpos = lastpos;
    return buf[lastpos - 1];
  }


  /**
   * Consume the buffer's current value.
   *
   * @return the current value.
   */
  char next() {
    if (currentpos != lastpos) {
      if (currentpos == size) {
        currentpos = 0;
      }
      return buf[currentpos++];
    } else {
      return last();
    }
  }

  /**
   * Get the last element of the buffer.
   *
   * @return last element.
   */
  char last() {
    if (lastpos != 0) {
      return buf[lastpos - 1];
    } else {
      return buf[size - 1];
    }
  }

  /**
   * Get the current buffer position.
   *
   * @return the position.
   */
  int getPos() {
    return currentpos;
  }

  /**
   * Set the buffer to a new position.
   *
   * @param newpos the new position.
   */
  void setPos(int newpos) {
    currentpos = newpos;
  }

  /**
   * Return the range size between the buffer current position and a
   * outside stored given position that is previous to the current.
   *
   * @param prevpos the given position.
   * @return the range size.
   */
  int diffPrevPos(int prevpos) {
    if (prevpos <= currentpos) {
      return currentpos - prevpos;
    } else {
      return currentpos + size - prevpos;
    }
  }

  /**
   * Return the range size between the buffer current position and a
   * outside stored given position that is following to the current.
   *
   * @param postpos the given position.
   * @return the range size.
   */
  int diffPostPos(int postpos) {
    if (postpos >= currentpos) {
      return postpos - currentpos;
    } else {
      return postpos + size - currentpos;
    }
  }

  /**
   * Checks the buffer for emptyness.
   *
   * @return true if the buffer is empty.
   */
  boolean isEmpty() {
    return currentpos == lastpos;
  }

  /**
   * Gets back 'posback' positions in the buffer.
   *
   * @param posback the amount of position to get back.
   */
  void back(int posback) {
    if (currentpos > posback) {
      currentpos -= posback;
    } else {
      currentpos = size - (posback - currentpos);
    }
  }

}
