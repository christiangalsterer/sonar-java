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
package org.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.tag.Tag;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Rule(
  key = "S3340",
  name = "Annotation arguments should appear in the order in which they were declared",
  priority = Priority.MINOR,
  tags = {Tag.CONVENTION})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.READABILITY)
@SqaleConstantRemediation("1min")
public class AnnotationArgumentOrderCheck extends SubscriptionBaseVisitor {
  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.ANNOTATION);
  }

  @Override
  public void visitNode(Tree tree) {
    AnnotationTree annotationTree = (AnnotationTree) tree;
    Collection<Symbol> symbols = annotationTree.symbolType().symbol().memberSymbols();
    List<String> declarationNames = new ArrayList<>();
    for (Symbol symbol : symbols) {
      declarationNames.add(symbol.name());
    }
    List<String> annotationArguments = new ArrayList<>();
    for (ExpressionTree argument : annotationTree.arguments()) {
      if (argument.is(Tree.Kind.ASSIGNMENT)) {
        AssignmentExpressionTree assigmentTree = (AssignmentExpressionTree) argument;
        IdentifierTree nameTree = (IdentifierTree) assigmentTree.variable();
        annotationArguments.add(nameTree.name());
      }
    }
    declarationNames.retainAll(annotationArguments);
    if (!declarationNames.equals(annotationArguments)) {
      addIssue(tree, "Reorder annotation arguments to match the order of declaration.");
    }
  }

}
