/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.shell;

public class ParameterTestCase extends AbstractCommandTestCase {

  /** . */
  private final String option_command = "class option_command extends org.crsh.command.CRaSHCommand {\n" +
      "@Command\n" +
      "public String main(@Option(names=['o','option']) String opt) {\n" +
      "return opt;" +
      "}\n" +
      "}";

  public void testShortOption() throws Exception {

    lifeCycle.bind("option_command", option_command);

    assertEquals("bar", assertOk("option_command -o bar"));

    //
    String foo = "class foo extends org.crsh.command.CRaSHCommand {\n" +
        "@Command\n" +
        "public String main() {\n" +
        "option_command o:'bar'\n" +
        "}\n" +
        "}";
    lifeCycle.bind("foo", foo);
    lifeCycle.bind("option_command", option_command);

    //
    assertEquals("bar", assertOk("foo"));
  }

  public void testShortOptionInScript() throws Exception {
    String foo = "option_command o:'bar'\n";
    lifeCycle.bind("foo", foo);
    lifeCycle.bind("option_command", option_command);

    //
    assertEquals("bar", assertOk("foo"));
  }

  public void testLongOption() throws Exception {
    String foo = "class foo extends org.crsh.command.CRaSHCommand {\n" +
        "@Command\n" +
        "public String main() {\n" +
        "option_command option:'bar'\n" +
        "}\n" +
        "}";
    lifeCycle.bind("foo", foo);
    lifeCycle.bind("option_command", option_command);

    //
    assertEquals("bar", assertOk("foo"));
  }

  public void testLongOptionInScript() throws Exception {
    String foo = "option_command option:'bar'\n";
    lifeCycle.bind("foo", foo);
    lifeCycle.bind("option_command", option_command);

    //
    assertEquals("bar", assertOk("foo"));
  }
}
