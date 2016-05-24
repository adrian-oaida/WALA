package com.ibm.wala.cast.ir.ssa;

import java.util.Map;

import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.loader.AstMethod.LexicalInformation;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.cfg.AbstractCFG;
import com.ibm.wala.cfg.IBasicBlock;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAGetCaughtExceptionInstruction;
import com.ibm.wala.ssa.SSAIndirectionData;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAOptions;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.ssa.IR.SSA2LocalMap;
import com.ibm.wala.ssa.SSACFG.ExceptionHandlerBasicBlock;
import com.ibm.wala.ssa.SSAIndirectionData.Name;
import com.ibm.wala.types.TypeReference;

public class AstIR extends IR {
  private final LexicalInformation lexicalInfo;
  
  private final SSA2LocalMap localMap;

  public LexicalInformation lexicalInfo() {
    return lexicalInfo;
  }
  
  private void setCatchInstructions(SSACFG ssacfg, AbstractCFG oldcfg) {
    for (int i = 0; i < oldcfg.getNumberOfNodes(); i++)
      if (oldcfg.isCatchBlock(i)) {
        ExceptionHandlerBasicBlock B = (ExceptionHandlerBasicBlock) ssacfg.getNode(i);
        B.setCatchInstruction((SSAGetCaughtExceptionInstruction) getInstructions()[B.getFirstInstructionIndex()]);
        getInstructions()[B.getFirstInstructionIndex()] = null;
      }
  }

  private void setupCatchTypes(SSACFG cfg, Map<IBasicBlock, TypeReference[]> map) {
    for(Map.Entry<IBasicBlock,TypeReference[]> e : map.entrySet()) {
      if (e.getKey().getNumber() != -1) {
        ExceptionHandlerBasicBlock bb = (ExceptionHandlerBasicBlock) cfg.getNode(e.getKey().getNumber());
        for (int j = 0; j < e.getValue().length; j++) {
          bb.addCaughtExceptionType(e.getValue()[j]);
        }
      }
    }
  }

  @Override
  protected SSA2LocalMap getLocalMap() {
    return localMap;
  }

  @Override
  protected String instructionPosition(int instructionIndex) {
    Position pos = getMethod().getSourcePosition(instructionIndex);
    if (pos == null) {
      return "";
    } else {
      return pos.toString();
    }
  }

  @Override
  public AstMethod getMethod() {
    return (AstMethod)super.getMethod();
  }

  public AstIR(AstMethod method, SSAInstruction[] instructions, SymbolTable symbolTable, SSACFG cfg, SSAOptions options) {
    super(method, instructions, symbolTable, cfg, options);

    lexicalInfo = method.cloneLexicalInfo();
    
    localMap = SSAConversion.convert(method, this, options);

    setCatchInstructions(getControlFlowGraph(), method.cfg());

    setupCatchTypes(getControlFlowGraph(), method.catchTypes());

    setupLocationMap();
  }

  @Override
  protected SSAIndirectionData<Name> getIndirectionData() {
    // TODO Auto-generated method stub
    return null;
  }
}