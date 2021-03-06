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

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class DateUtilsTruncateCheckTest {

  private static final String FILENAME = "src/test/files/checks/DateUtilsTruncateCheck.java";

  @Test
  public void java8() {
    JavaCheckVerifier.verify(FILENAME, new DateUtilsTruncateCheck(), 8);
  }

  @Test
  public void java7() {
    JavaCheckVerifier.verifyNoIssue(FILENAME, new DateUtilsTruncateCheck(), 7);
  }

  @Test
  public void unknown_version() {
    JavaCheckVerifier.verify("src/test/files/checks/DateUtilsTruncateCheck_no_version.java", new DateUtilsTruncateCheck(), (Integer) null);
  }
}
