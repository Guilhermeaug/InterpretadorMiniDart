package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.BoolValue;
import interpreter.value.Value;

import java.util.List;

public class IfListItem extends ListItem {

    private final Expr expr;
    private final ListItem thenItem;
    private final ListItem elseItem;

    public IfListItem(int line, Expr expr, ListItem thenItem, ListItem elseItem) {
        super(line);
        this.expr = expr;
        this.thenItem = thenItem;
        this.elseItem = elseItem;
    }

    @Override
    public List<Value<?>> items() {
        var v = expr.expr();
        if (!(v instanceof BoolValue bv)) {
            Utils.abort(super.getLine());
            return null;
        }

        if (bv.value()) {
            return thenItem.items();
        } else {
            if (elseItem != null)
                return elseItem.items();
        }

        return List.of();
    }
}
