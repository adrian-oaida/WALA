/*******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.util.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 *
 * A debugging aid.  When HashSetFactory.DEBUG is set, this class creates ParanoidHashSets.  Otherwise,
 * it returns {@link LinkedHashSet}s
 * @author sfink
 */
public class HashSetFactory {

  /**
   * If true, this factory returns Paranoid versions of collections
   */
  final public static boolean DEBUG = false;

  /**
   * @return A ParanoidHashSet if DEBUG = true, a java.util.HashSet otherwise
   */
  public static <T> HashSet<T> make(int size) {
    if (DEBUG) {
      return new ParanoidHashSet<T>(size);
    } else {
      return new LinkedHashSet<T>(size);
    }
  }

  /**
   * @return A ParanoidHashSet if DEBUG = true, a java.util.HashSet otherwise
   */
  public static <T> HashSet<T> make() {
    if (DEBUG) {
      return new ParanoidHashSet<T>();
    } else {
      return new LinkedHashSet<T>();
    }
  }

//
//  /**
//   * @return A ParanoidHashSet if DEBUG = true, a java.util.HashSet otherwise
//   */
//  public static <T> HashSet<T> make(Set<T> s) {
//    if (DEBUG) {
//      return new ParanoidHashSet<T>(s);
//    } else {
//      return new HashSet<T>(s);
//    }
//  }

  /**
   * @return A ParanoidHashSet if DEBUG = true, a java.util.HashSet otherwise
   */
  public static <T> HashSet<T> make(Collection<T> s) {
    if (DEBUG) {
      return new ParanoidHashSet<T>(s);
    } else {
      return new LinkedHashSet<T>(s);
    }
  }
}
