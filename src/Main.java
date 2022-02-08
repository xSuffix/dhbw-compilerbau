import c4_1_syntax_tree.*;
import visitor.*;

public class Main {
    public static void main(String[] args) {
        var input = "ab*c|(a+)?";
        var parser = new Parser(input);
        System.out.println(input);
        System.out.println(parser.start());
    }
}
