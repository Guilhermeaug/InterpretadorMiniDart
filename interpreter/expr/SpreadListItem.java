package interpreter.expr;

import java.util.List;

import interpreter.util.Utils;
import interpreter.value.ListValue;
import interpreter.value.Value;

public class SpreadListItem extends ListItem {

    private final Expr expr;

    public SpreadListItem(int line, Expr expr) {
        super(line);
        this.expr = expr;
    }

    @Override
    public List<Value<?>> items() {
        var v = expr.expr();
        if (!(v instanceof ListValue lv)) {
            Utils.abort(super.getLine());
            return null;
        }

        return lv.value();
    }

}
