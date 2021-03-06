/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.crsh.cmdline.matcher;

import junit.framework.TestCase;
import org.crsh.cmdline.CommandCompletion;
import org.crsh.cmdline.Delimiter;
import org.crsh.cmdline.annotations.Argument;
import org.crsh.cmdline.ClassDescriptor;
import org.crsh.cmdline.annotations.Command;
import org.crsh.cmdline.CommandFactory;
import org.crsh.cmdline.annotations.Option;
import org.crsh.cmdline.spi.Completion;

import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CompleteTestCase extends TestCase {

  public void testCompleterResolution() throws Exception {

    class A {
      @Command
      void m(@Argument() String arg) {}
      @Command
      void n(@Argument(completer =  CompleterSupport.Foo.class) String arg) {}
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher();

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("m fo"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete("n fo"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("ab", false)), matcher.complete(new CompleterSupport.Echo(), "m ab"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete(new CompleterSupport.Echo(), "n fo"));
  }

  public void testExplicitCommandSingleArgument() throws Exception
  {

    class A {
      @Command
      void m(@Argument(completer =  CompleterSupport.Foo.class) String arg) {}
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher();

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete("m "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("m f"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete("m fo"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("m a "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("m a f"));
  }

  public void testImplicitCommandSingleArgument() throws Exception
  {

    class A {
      @Command
      void main(@Argument(completer =  CompleterSupport.Foo.class) String arg) {}
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher("main");

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete(""));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("f"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete("fo"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("a "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("a f"));
  }

  public void testSecondArgument() throws Exception {

    class A {
      @Command
      void main(
        @Argument String arg1,
        @Argument(completer =  CompleterSupport.Foo.class) String arg2) {}
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher("main");

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete("foo "));
    assertEquals(new CommandCompletion(Delimiter.DOUBLE_QUOTE, Completion.create("foo", true)), matcher.complete("foo \""));
  }

  public void testMultiArgument() throws Exception
  {

    class A {
      @Command
      void m(@Argument(completer =  CompleterSupport.Foo.class) List<String> arg) {}
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher();

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete("m "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("m f"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete("m fo"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete("m a "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("m a f"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete("m a fo"));
  }

  public void testOption() throws Exception
  {

    class A {
      @Option(names = {"a", "add", "addition"}) String add;
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher();

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("-", "a", true)), matcher.complete("-"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("", true)), matcher.complete("-a"));

    CommandCompletion a = new CommandCompletion(Delimiter.EMPTY, Completion.builder("--").add("add", true).add("addition", true).build());
    CommandCompletion b = new CommandCompletion(Delimiter.EMPTY, Completion.builder("--ad").add("d", true).add("dition", true).build());
    CommandCompletion c = new CommandCompletion(Delimiter.EMPTY, Completion.create("--addi", "tion", true));

    //
    assertEquals(a, matcher.complete("--"));
    assertEquals(b, matcher.complete("--ad"));
    assertEquals(c, matcher.complete("--addi"));
  }

  public void testDoubleDash() throws Exception {

    class A {
      @Command
      void main(@Option(names = "o") String o, @Argument(completer = CompleterSupport.Foo.class) String arg) { }
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher("main");

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("-- f"));
  }

  public void testOptionValue() throws Exception
  {

    class A {
      @Option(names = "a", completer = CompleterSupport.Foo.class) String a;
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher();
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete("-a "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("-a f"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete("-a fo"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("-b")), matcher.complete("-a -b"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("-a b "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("c")), matcher.complete("-a b c"));
  }

  public void testImplicitCommandOptionName() throws Exception
  {
    class A {
      @Command
      void main(@Option(names = {"o", "option"}) String o) { }
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher("main");

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("-", "o", true)), matcher.complete("-"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("--", "option", true)), matcher.complete("--"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("--o", "ption", true)), matcher.complete("--o"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("--op", "tion", true)), matcher.complete("--op"));
  }

  public void testOptionArgument() throws Exception
  {

    class A {
      @Command
      void main(@Option(names = "o") String o, @Argument(completer = CompleterSupport.Foo.class) String arg) { }
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher("main");

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete("-o bar "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("-o bar f"));
  }

  public void testCommand() throws Exception
  {

    class A {
      @Option(names = "a") String a;
      @Command
      void foo(@Option(names = "b") String b) { }
      @Command
      void faa() { }
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher("main");

    //
    CommandCompletion a = new CommandCompletion(Delimiter.EMPTY, Completion.builder("").add("foo", true).add("faa", true).build());
    CommandCompletion b = new CommandCompletion(Delimiter.EMPTY, Completion.builder("f").add("oo", true).add("aa", true).build());
    CommandCompletion c = new CommandCompletion(Delimiter.EMPTY, Completion.create("", true));
    CommandCompletion d = new CommandCompletion(Delimiter.EMPTY, Completion.create());

    //
    assertEquals(a, matcher.complete(""));
    assertEquals(b, matcher.complete("f"));
    assertEquals(c, matcher.complete("foo"));
    assertEquals(d, matcher.complete("foo "));

    //
    assertEquals(a, matcher.complete("-a a "));
    assertEquals(b, matcher.complete("-a a f"));
    assertEquals(c, matcher.complete("-a a foo"));
    assertEquals(d, matcher.complete("-a a foo "));
  }

  public void testArgumentValuedMain() throws Exception
  {

    class A {
      @Command
      void main(@Argument(completer = CompleterSupport.Echo.class) String s) { }
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher("main");

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("", false)), matcher.complete(""));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("m", false)), matcher.complete("m"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("ma", false)), matcher.complete("ma"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("mai", false)), matcher.complete("mai"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("main", false)), matcher.complete("main"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("main "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("main a"));
  }

  public void testEnum() throws Exception
  {
    class A {
      @Command
      void foo(@Option(names = "a") RetentionPolicy a) { }
      @Command
      void bar(@Argument RetentionPolicy a) { }
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher();

    //
    CommandCompletion a = new CommandCompletion(Delimiter.EMPTY, Completion.builder("").add("SOURCE", true).add("CLASS", true).add("RUNTIME", true).build());
    CommandCompletion b = new CommandCompletion(Delimiter.DOUBLE_QUOTE, Completion.builder("").add("SOURCE", true).add("CLASS", true).add("RUNTIME", true).build());
    CommandCompletion c = new CommandCompletion(Delimiter.SINGLE_QUOTE, Completion.builder("").add("SOURCE", true).add("CLASS", true).add("RUNTIME", true).build());
    CommandCompletion d = new CommandCompletion(Delimiter.EMPTY, Completion.create("SOU", "RCE", true));
    CommandCompletion e = new CommandCompletion(Delimiter.DOUBLE_QUOTE, Completion.create("SOU", "RCE", true));
    CommandCompletion f = new CommandCompletion(Delimiter.SINGLE_QUOTE, Completion.create("SOU", "RCE", true));
    CommandCompletion g = new CommandCompletion(Delimiter.EMPTY, Completion.create("SOURCE", "", true));
    CommandCompletion h = new CommandCompletion(Delimiter.EMPTY, Completion.create("SOURCE", "", true));

    //
    for (String m : Arrays.asList("foo -a", "bar")) {
      assertEquals("testing " + m, a, matcher.complete(m + " "));
      assertEquals("testing " + m, b, matcher.complete(m + " \""));
      assertEquals("testing " + m, c, matcher.complete(m + " '"));
      assertEquals("testing " + m, d, matcher.complete(m + " SOU"));
      assertEquals("testing " + m, e, matcher.complete(m + " \"SOU"));
      assertEquals("testing " + m, f, matcher.complete(m + " 'SOU"));
      assertEquals("testing " + m, g, matcher.complete(m + " SOURCE"));
      assertEquals("testing " + m, h, matcher.complete(m + " \"SOURCE\""));
    }
  }

  public void testCommandOption() throws Exception
  {
    class A {
      @Command
      void bar(@Option(names = "a", completer = CompleterSupport.Foo.class) String a) { }
    }

    //
    ClassDescriptor<A> desc = CommandFactory.DEFAULT.create(A.class);
    Matcher<A> matcher = desc.matcher();

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("bar", true)), matcher.complete(""));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("b", "ar", true)), matcher.complete("b"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("", true)), matcher.complete("bar"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("bar "));

    //
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("foo", true)), matcher.complete("bar -a "));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("oo", true)), matcher.complete("bar -a f"));
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create("o", true)), matcher.complete("bar -a fo"));
  }

  public void testFailure() throws Exception
  {

    //
    class A {
      @Command
      void foo(@Option(names = "a", completer = CompleterSupport.Exception.class) String a) { }
    }
    Matcher<A> matcherA = CommandFactory.DEFAULT.create(A.class).matcher();
    try {
      matcherA.complete("foo -a b");
      fail();
    }
    catch (CmdCompletionException e) {
    }

    //
    class B {
      @Command
      void foo(@Option(names = "a", completer = CompleterSupport.RuntimeException.class) String a) { }
    }
    Matcher<B> matcherB = CommandFactory.DEFAULT.create(B.class).matcher();
    try {
      matcherB.complete("foo -a b");
      fail();
    }
    catch (CmdCompletionException e) {
    }

    //
    class C {
      @Command
      void foo(@Option(names = "a", completer = CompleterSupport.Abstract.class) String a) { }
    }
    Matcher<C> matcherC = CommandFactory.DEFAULT.create(C.class).matcher();
    try {
      matcherC.complete("foo -a b");
      fail();
    }
    catch (CmdCompletionException e) {
    }
  }

  public void testArgumentProvidedValue() throws Exception {

    class A {
      Custom o;
      @Command
      public void foo(@Argument Custom o) { this.o = o; }
    }

    ClassDescriptor<A> desc = new CommandFactory(CompleteTestCase.class.getClassLoader()).create(A.class);

    //
    Matcher<A> matcher = desc.matcher();
    assertEquals(new CommandCompletion(Delimiter.EMPTY, Completion.create()), matcher.complete("foo "));
  }
}
