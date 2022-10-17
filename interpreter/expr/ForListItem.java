package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.ListValue;
import interpreter.value.Value;

import java.util.ArrayList;
import java.util.List;

public class ForListItem extends ListItem {

    private final Variable var;
    private final Expr expr;
    private final ListItem item;

    public ForListItem(int line, Variable var, Expr expr, ListItem item) {
        super(line);
        this.var = var;
        this.expr = expr;
        this.item = item;
    }

    @Override
    public List<Value<?>> items() {
        var v = expr.expr();
        if (!(v instanceof ListValue lv)) {
            Utils.abort(super.getLine());
            return null;
        }

        var list = lv.value();
        List<Value<?>> l = new ArrayList<>();
        for (var i : list) {
            var.setValue(i);
            l.addAll(item.items());
        }

        return l;
    }
}
