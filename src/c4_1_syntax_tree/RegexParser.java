package c4_1_syntax_tree;

public class RegexParser implements Parser {
    int index;
    String input;

    public RegexParser() {
    }
    public RegexParser(String input) {
        initialize(input);
    }

    public void initialize(String input) {
        index = 0;
        this.input = input;
    }

    void throwErrorMessage(String message) {
        System.out.println("Error!");

        System.out.print(message);
        System.out.print(" at index ");
        System.out.println(index);

        System.out.println(input);

        for (int i = 0; i < index; i++) {
            System.out.print(" ");
        }
        System.out.println("^ here");

        throw new RuntimeException("SyntaxError!");
    }

    boolean endOfInput() {
        return index >= input.length();
    }

    boolean nextIs(char peek) {
        if (endOfInput()) {
            return false;
        }

        return input[index] == peek;
    }

    boolean nextIsAlphaNumeric() {
        for (int i = 0; i < 26; i++) {
            if (nextIs((char)('A' + i))) {
                return true;
            }
            if (nextIs((char)('a' + i))) {
                return true;
            }
        }

        for (int i = 0; i < 10; i++) {
            if (nextIs((char)('0' + i))) {
                return true;
            }
        }

        return false;
    }

    char next() {
        if (endOfInput()) {
            throwErrorMessage("Unexpected end of input");
        }

        return input[index++];
    }

    void match(char expect) {
        if (!nextIs(expect)) {
            throwErrorMessage("Expected '" + expect + "'");
        }
        next();
    }

    void matchEndOfInput() {
        if (!endOfInput()) {
            throwErrorMessage("Expected end of input");
        }
    }

    //
    // Parse Methods:
    //

    public SyntaxNode start() {
        var result = regex();
        matchEndOfInput();

        return result;
    }

    SyntaxNode regex() {
        var left = regexLeft();
        var right = regexRight();

        if (left != null && right == null) {
            return left;
        }

        if (left == null && right != null) {
            return new OperandNode(""); // empty word
        }

        return new BinOpNode("°", left, right);
    }

    SyntaxNode regexLeft() {
        if (nextIs('(') || nextIsAlphaNumeric()) {
            var item = elementAndOperator();
            var next = regexLeft();

            if (next != null) {
                return new BinOpNode("°", item, next);
            }

            return item;
        }
    }

    SyntaxNode elementAndOperator() {
        var el = element();
        var op = operator();

        if (op == 0) {
            return el;
        }

        new UnaryOpNode(""+op, el);
    }

    SyntaxNode regexRight() {
        if (nextIs('|')) {
            next();
            return regex();
        }

        return null;
    }

    char operator() {
        if (nextIs('*') || nextIs('+') || nextIs('?')) {
            return next();
        }

        return 0;
    }

    SyntaxNode element() {
        if (nextIs('(')) {
            match('(');
            var re = regex();
            match(')');

            return re;
        }

        return new OperandNode(alphaNumeric());
    }

    char alphaNumeric() {
        if (nextIsAlphaNumeric()) {
            return next();
        }

        throwErrorMessage("Expected alpha-numeric character");
    }
}