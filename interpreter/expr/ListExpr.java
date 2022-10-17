package interpreter.expr;

import interpreter.value.ListValue;
import interpreter.value.Value;

import java.util.ArrayList;
import java.util.List;

public class ListExpr extends Expr {

    private final List<ListItem> list;

    public ListExpr(int line) {
        super(line);
        this.list = new ArrayList<>();
    }

    public void addItem(ListItem item){
        this.list.add(item);
    }

    @Override
    public Value<?> expr() {
        List<Value<?>> l = new ArrayList<>();
        for (ListItem item : list) {
            l.addAll(item.items());
        }
        return new ListValue(l);
    }
}
