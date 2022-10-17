package interpreter.expr;

import java.util.List;

import interpreter.value.ListValue;
import interpreter.value.Value;

public class SingleListItem extends ListItem {

    private final Expr expr;

    public SingleListItem(int line, Expr expr) {
        super(line);
        this.expr = expr;
    }

    @Override
    public List<Value<?>> items() {
        var v = expr.expr();
        return List.of(v);
    }

}
