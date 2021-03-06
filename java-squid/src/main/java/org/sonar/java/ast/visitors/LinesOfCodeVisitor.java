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
package org.sonar.java.ast.visitors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.sonar.java.model.InternalSyntaxToken;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;
import java.util.Set;

public class LinesOfCodeVisitor extends SubscriptionVisitor{

  private Set<Integer> lines = Sets.newHashSet();

  public int linesOfCode(CompilationUnitTree tree) {
    lines.clear();
    scanTree(tree);
    return lines.size();
  }

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.TOKEN);
  }

  @Override
  public void visitToken(SyntaxToken syntaxToken) {
    if (!((InternalSyntaxToken) syntaxToken).isEOF()) {
      lines.add(syntaxToken.line());
    }
  }
}
