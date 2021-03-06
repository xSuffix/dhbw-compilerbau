package syntax_tree;

import java.util.HashSet;
import java.util.Set;

public abstract class SyntaxNode {
    public final Set<Integer> firstpos = new HashSet<>();
    public final Set<Integer> lastpos = new HashSet<>();
    public Boolean nullable;
}
