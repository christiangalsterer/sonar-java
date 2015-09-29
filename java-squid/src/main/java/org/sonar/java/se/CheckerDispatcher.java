/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.se;

import org.sonar.java.JavaCheckMessage;
import org.sonar.java.se.checkers.SEChecker;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;

public class CheckerDispatcher implements CheckerContext {
  private final ExplodedGraphWalker explodedGraphWalker;
  private final JavaFileScannerContext context;
  private final List<SEChecker> checkers;
  private Tree syntaxNode;
  private int currentCheckerIndex = 0;
  private boolean transition = false;

  public CheckerDispatcher(ExplodedGraphWalker explodedGraphWalker, JavaFileScannerContext context, List<SEChecker> checkers) {
    this.explodedGraphWalker = explodedGraphWalker;
    this.context = context;
    this.checkers = checkers;
  }

  public void executeCheckPreStatement(Tree syntaxNode) {
    this.syntaxNode = syntaxNode;
    execute();
  }

  private void execute() {
    this.transition = false;
    if (currentCheckerIndex < checkers.size()) {
      checkers.get(currentCheckerIndex).checkPreStatement(this, syntaxNode);
    } else {
      explodedGraphWalker.enqueue(
          new ExplodedGraph.ProgramPoint(explodedGraphWalker.programPosition.block, explodedGraphWalker.programPosition.i + 1),
          explodedGraphWalker.programState
      );
      return;
    }
    if (!transition) {
      addTransition(explodedGraphWalker.programState);
    }
  }

  public ProgramState getState() {
    return explodedGraphWalker.programState;
  }

  @Override
  public ProgramState setConstraint(SymbolicValue val, ConstraintManager.NullConstraint nl) {
    return ConstraintManager.setConstraint(getState(), val, nl);
  }

  @Override
  public boolean isNull(SymbolicValue val) {
    return explodedGraphWalker.constraintManager.isNull(getState(), val);
  }

  @Override
  public void addIssue(Tree tree, String ruleKey, String message) {
    context.addIssue(tree, new JavaCheckMessage(ruleKey, message));
  }


  @Override
  public void addTransition(ProgramState state) {
    ProgramState oldState = explodedGraphWalker.programState;
    explodedGraphWalker.programState = state;
    currentCheckerIndex++;
    execute();
    currentCheckerIndex--;
    explodedGraphWalker.programState = oldState;
    this.transition = true;
  }

  @Override
  public Object createSink() {
    transition = true;
    return new Object();
  }


  @Override
  public SymbolicValue getVal(Tree expression) {
    return explodedGraphWalker.getVal(expression);
  }

  public void executeCheckEndOfExecution(MethodTree tree) {
    for (SEChecker checker : checkers) {
      checker.checkEndOfExecution(this);
    }
  }

  public void init() {
    for (SEChecker checker : checkers) {
      checker.init();
    }
  }
}
