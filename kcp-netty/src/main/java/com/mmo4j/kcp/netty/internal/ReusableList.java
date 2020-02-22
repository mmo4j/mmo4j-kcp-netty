package com.mmo4j.kcp.netty.internal;

import java.util.ListIterator;

/**
 * Reusable list iterator
 *
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public interface ReusableList<E> extends ListIterator<E>, ReusableIterator<E> {

  /**
   * This method is identical to {@code rewind(0)}.
   *
   * @return this iterator
   */
  ReusableList<E> rewind();

  /**
   * Reset the iterator and specify index of the first element to be returned from the iterator.
   *
   * @param index index of the first element to be returned from the iterator
   * @return this iterator
   */
  ReusableList<E> rewind(int index);

}
