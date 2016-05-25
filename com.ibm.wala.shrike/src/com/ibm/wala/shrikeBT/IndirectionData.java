/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.shrikeBT;

import java.io.Serializable;

public interface IndirectionData extends Serializable{

  int[] indirectlyReadLocals(int instructionIndex);
  
  int[] indirectlyWrittenLocals(int instructionIndex);
  
}
