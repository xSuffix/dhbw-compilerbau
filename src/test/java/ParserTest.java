// Author: Tobias Kohnle

import syntax_tree.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
    Parser parser;

    private static boolean equals(Visitable v1, Visitable v2) {
        if (v1 == v2)
            return true;
        if (v1 == null)
            return false;
        if (v2 == null)
            return false;
        if (v1.getClass() != v2.getClass())
            return false;
        if (v1.getClass() == OperandNode.class) {
            OperandNode op1 = (OperandNode) v1;
            OperandNode op2 = (OperandNode) v2;
            return op1.position == op2.position && op1.symbol.equals(op2.symbol);
        }
        if (v1.getClass() == UnaryOpNode.class) {
            UnaryOpNode op1 = (UnaryOpNode) v1;
            UnaryOpNode op2 = (UnaryOpNode) v2;
            return op1.operator.equals(op2.operator) && equals(op1.subNode,
                    op2.subNode);
        }
        if (v1.getClass() == BinOpNode.class) {
            BinOpNode op1 = (BinOpNode) v1;
            BinOpNode op2 = (BinOpNode) v2;
            return op1.operator.equals(op2.operator) &&
                    equals(op1.left, op2.left) &&
                    equals(op1.right, op2.right);
        }
        throw new IllegalStateException("Ungueltiger Knotentyp");
    }

    @BeforeEach
    public void init() {
        parser = new Parser();
    }

    @Test
    public void testEmpty() {
        parser.initialize("()#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new OperandNode(""), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void testEmpty2() {
        parser.initialize("#");
        Visitable actual = parser.start();
        Visitable expected = new OperandNode("#");

        equals(expected, actual);
    }

    @Test
    public void test1() {
        parser.initialize("(a)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new OperandNode("a"), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void test2() {
        parser.initialize("(ab)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new BinOpNode("°", new OperandNode("a"), new OperandNode("b")), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void test3() {
        parser.initialize("(a|b)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new BinOpNode("|", new OperandNode("a"), new OperandNode("b")), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void test4() {
        parser.initialize("((a)?)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new UnaryOpNode("?", new OperandNode("a")), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void test5() {
        parser.initialize("(a*)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new UnaryOpNode("*", new OperandNode("a")), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void test7() {
        parser.initialize("(a|)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new BinOpNode("|", new OperandNode("a"), new OperandNode("")), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void test8() {
        parser.initialize("(|b)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new BinOpNode("|", new OperandNode(""), new OperandNode("b")), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void test9() {
        parser.initialize("(|)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new BinOpNode("|", new OperandNode(""), new OperandNode("")), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void testMixed() {
        parser.initialize("(a?b+|c*)#");
        Visitable actual = parser.start();
        Visitable expected = new BinOpNode("°", new BinOpNode("|", new BinOpNode("°", new UnaryOpNode("?", new OperandNode("a")), new UnaryOpNode("+", new OperandNode("b"))), new UnaryOpNode("*", new OperandNode("c"))), new OperandNode("#"));

        equals(expected, actual);
    }

    @Test
    public void testFail1() {
        parser.initialize("a#");
        assertThrows(RuntimeException.class, () -> parser.start());
    }

    @Test
    public void testFail2() {
        parser.initialize("(a)");
        assertThrows(RuntimeException.class, () -> parser.start());
    }

    @Test
    public void testFail3() {
        parser.initialize("(*)#");
        assertThrows(RuntimeException.class, () -> parser.start());
    }

    @Test
    public void testFail4() {
        parser.initialize("(a)#b");
        assertThrows(RuntimeException.class, () -> parser.start());
    }

    @Test
    public void testFail5() {
        parser.initialize("");
        assertThrows(RuntimeException.class, () -> parser.start());
    }

    @Test
    public void testFail6() {
        parser.initialize("(as)k)#");
        assertThrows(RuntimeException.class, () -> parser.start());
    }

    @Test
    public void testFail7() {
        parser.initialize("((a )k)#");
        assertThrows(RuntimeException.class, () -> parser.start());
    }
}
