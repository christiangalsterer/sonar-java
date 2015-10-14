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
package org.sonar.java.se.checkers;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.se.CheckerContext;
import org.sonar.java.se.ConstraintManager;
import org.sonar.java.se.ProgramState;
import org.sonar.java.se.SymbolicValue;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

@Rule(
  key = "S2259",
  name = "Null pointers should not be dereferenced",
  priority = Priority.BLOCKER)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.LOGIC_RELIABILITY)
@SqaleConstantRemediation("10min")
public class NullDereferenceChecker extends SEChecker {

<<<<<<< HEAD
=======
  private static final String RULE_KEY = "squid:S2259";


>>>>>>> SONARJAVA-1311 Better handling of checker dispatch
  @Override
  public ProgramState checkPreStatement(CheckerContext context, Tree syntaxNode) {
    SymbolicValue currentVal = context.getState().peekValue();
    if(currentVal == null) {
      //stack is empty, nothing to do.
      return context.getState();
    }
    if(syntaxNode.is(Tree.Kind.MEMBER_SELECT)) {
      if(context.isNull(currentVal)) {
        context.addIssue(syntaxNode, RULE_KEY, "NullPointerException might be thrown as '" + getName(syntaxNode) + "' is nullable here");
        return null;
      }
      //we dereferenced the symbolic value so we can assume it is not null
      return context.setConstraint(currentVal, ConstraintManager.NullConstraint.NOT_NULL);
    }
<<<<<<< HEAD
    if (val != null) {
      if (context.isNull(val)) {
        context.addIssue(syntaxNode, this, "NullPointerException might be thrown as '" + name + "' is nullable here");
        context.createSink();
        return;
      } else {
        //we dereferenced the symbolic value so we can assume it is not null
        programState = context.setConstraint(val, ConstraintManager.NullConstraint.NOT_NULL);
      }
=======
    return context.getState();
  }

  @Override
  public void checkPostStatement(CheckerContext context, Tree syntaxNode) {
    if (context.isNull(context.getState().peekValue()) && syntaxNode.is(Tree.Kind.SWITCH_STATEMENT)) {
      context.addIssue(syntaxNode, RULE_KEY, "NullPointerException might be thrown as '" + getName(syntaxNode) + "' is nullable here");
      context.createSink();
      return;
>>>>>>> SONARJAVA-1311 Better handling of checker dispatch
    }
    context.addTransition(setNullConstraint(context, syntaxNode));
  }

  private ProgramState setNullConstraint(CheckerContext context, Tree syntaxNode) {
    SymbolicValue val = context.getState().peekValue();
    switch (syntaxNode.kind()) {
      case NULL_LITERAL:
        assert val.equals(SymbolicValue.NULL_LITERAL);
        return context.getState();
      case METHOD_INVOCATION:
        ProgramState ps = context.getState();
        if (((MethodInvocationTree) syntaxNode).symbol().metadata().isAnnotatedWith("javax.annotation.CheckForNull")) {
          ps = context.setConstraint(val, ConstraintManager.NullConstraint.NULL);
        }
        return ps;
    }
    return context.getState();
  }

  private static String getName(Tree syntaxNode) {
    String name = "";
    ExpressionTree expressionTree = null;
    if (syntaxNode.is(Tree.Kind.MEMBER_SELECT)) {
      expressionTree = ((MemberSelectExpressionTree) syntaxNode).expression();
    } else if (syntaxNode.is(Tree.Kind.SWITCH_STATEMENT)) {
      expressionTree = ((SwitchStatementTree) syntaxNode).expression();
    }
    if(expressionTree != null) {
      if(expressionTree.is(Tree.Kind.IDENTIFIER)) {
        name = ((IdentifierTree) expressionTree).name();
      } else if(expressionTree.is(Tree.Kind.METHOD_INVOCATION)) {
        name = ((MethodInvocationTree) expressionTree).symbol().name();
      }
    }
    return name;
  }
}
