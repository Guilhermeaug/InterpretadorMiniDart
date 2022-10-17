package interpreter.expr;

import java.util.Map;

import interpreter.util.Utils;
import interpreter.value.ListValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {

    private final SetExpr base;
    private final Expr index;

    public AccessExpr(int line, SetExpr base, Expr index) {
        super(line);
        this.base = base;
        this.index = index;
    }

    @Override
    public Value<?> expr() {
        Value<?> bvalue = base.expr();
        if (bvalue instanceof ListValue lv) {
            var list = lv.value();
            var i = index.expr();
            if (!(i instanceof NumberValue nv)) {
                Utils.abort(super.getLine());
                return null;
            }
            int index = nv.value();
            if (index >= list.size() || index < 0) {
                return null;
            }

            return list.get(index);

        } else if (bvalue instanceof MapValue mv) {
            Map<Value<?>, Value<?>> map = mv.value();

            Value<?> ivalue = index.expr();
            if (ivalue == null)
                Utils.abort(super.getLine());

            return map.get(ivalue);
        } else {
            Utils.abort(super.getLine());
        }

        return null;
    }

    @Override
    public void setValue(Value<?> value) {
        Value<?> bvalue = base.expr();
        if (bvalue instanceof ListValue lv) {
            var list = lv.value();
            var i = index.expr();
            if (!(i instanceof NumberValue nv)) {
                Utils.abort(super.getLine());
                return;
            }
            int index = nv.value();
            if (index >= list.size() || index < 0) {
                return;
            }
            list.set(index, value);

        } else if (bvalue instanceof MapValue mv) {
            Map<Value<?>, Value<?>> map = mv.value();

            Value<?> ivalue = index.expr();
            if (ivalue == null)
                Utils.abort(super.getLine());

            map.put(ivalue, value);
        } else {
            Utils.abort(super.getLine());
        }
    }

}
