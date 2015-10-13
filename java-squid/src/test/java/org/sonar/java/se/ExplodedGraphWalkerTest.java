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

import org.junit.Test;
import org.sonar.plugins.java.api.tree.Tree;

import static org.fest.assertions.Fail.fail;

public class ExplodedGraphWalkerTest {

  @Test
  public void test() throws Exception {
    JavaCheckVerifier.verify("src/test/files/se/SeEngineTest.java", new SymbolicExecutionVisitor());
  }

  @Test
  public void test_null_pointer_check_unit_test() throws Exception {
    JavaCheckVerifier.verify("src/test/files/se/NullPointerCheck.java", new SymbolicExecutionVisitor());
  }

  @Test
  public void test_useless_condition() throws Exception {
    JavaCheckVerifier.verify("src/test/files/se/UselessConditionCheck.java", new SymbolicExecutionVisitor());
  }
  @Test
  public void reproducer() throws Exception {
    JavaCheckVerifier.verify("src/test/files/se/Reproducer.java", new SymbolicExecutionVisitor());
  }

  @Test
  public void test2() throws Exception {
    JavaCheckVerifier.verifyNoIssue("src/test/files/se/SeEngineTestCase.java", new SymbolicExecutionVisitor() {
      @Override
      public void visitNode(Tree tree) {
        try {
          tree.accept(new ExplodedGraphWalker(context));
        }catch (ExplodedGraphWalker.MaximumStepsReachedException exception) {
          fail("loop execution should be limited");
        }

      }
    });
  }

}
