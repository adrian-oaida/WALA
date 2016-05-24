/******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *****************************************************************************/
package com.ibm.wala.cast.ir.ssa;

import java.util.Map;

import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.loader.AstMethod.LexicalInformation;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.cfg.AbstractCFG;
import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.cfg.IBasicBlock;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.Context;
import com.ibm.wala.ssa.DefaultIRFactory;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.IRFactory;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACFG.ExceptionHandlerBasicBlock;
import com.ibm.wala.ssa.SSAGetCaughtExceptionInstruction;
import com.ibm.wala.ssa.SSAIndirectionData;
import com.ibm.wala.ssa.SSAIndirectionData.Name;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAOptions;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.TypeReference;

public class AstIRFactory<T extends IMethod> implements IRFactory<T> {

  public ControlFlowGraph makeCFG(final IMethod method, final Context context) {
    return ((AstMethod) method).getControlFlowGraph();
  }

  public static class AstDefaultIRFactory extends DefaultIRFactory {
    private final AstIRFactory astFactory;

    public AstDefaultIRFactory() {
      this(new AstIRFactory());
    }
    
    public AstDefaultIRFactory(AstIRFactory astFactory) {
      this.astFactory = astFactory;
    }

    @Override
    public IR makeIR(IMethod method, Context context, SSAOptions options) {
      if (method instanceof AstMethod) {
        return astFactory.makeIR(method, context, options);
      } else {
        return super.makeIR(method, context, options);
      }
    }

    @Override
    public ControlFlowGraph makeCFG(IMethod method, Context context) {
      if (method instanceof AstMethod) {
        return astFactory.makeCFG(method, context);
      } else {
        return super.makeCFG(method, context);
      }
    }
  }

  

  @Override
  public IR makeIR(final IMethod method, final Context context, final SSAOptions options) {
    assert method instanceof AstMethod : method.toString();
  
    AbstractCFG oldCfg = ((AstMethod) method).cfg();
    SSAInstruction[] oldInstrs = (SSAInstruction[]) oldCfg.getInstructions();
    SSAInstruction[] instrs = new SSAInstruction[ oldInstrs.length ];
    System.arraycopy(oldInstrs, 0, instrs, 0, instrs.length);
    
    IR newIR = new AstIR((AstMethod) method, instrs, ((AstMethod) method).symbolTable().copy(), new SSACFG(method, oldCfg, instrs),
        options);

    return newIR;
  }

  public static IRFactory<IMethod> makeDefaultFactory() {
    return new AstDefaultIRFactory();
  }

  @Override
  public boolean contextIsIrrelevant(IMethod method) {
    return true;
  }
}
