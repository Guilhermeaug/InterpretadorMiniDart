package interpreter.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.w3c.dom.Text;

import interpreter.util.Utils;
import interpreter.value.BoolValue;
import interpreter.value.ListValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class FunctionExpr extends Expr {

    private final FunctionOp op;
    private final Expr expr;

    private static final Scanner input = new Scanner(System.in);

    public FunctionExpr(int line, FunctionOp op, Expr expr) {
        super(line);

        this.op = op;
        this.expr = expr;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = expr.expr();

        switch (op) {
            case READ:
                return readOp(v);
            case RANDOM:
                return randomOp(v);
            case LENGTH:
                return lengthOp(v);
            case KEYS:
                return keysOp(v);
            case VALUES:
                return valuesOp(v);
            case TOBOOL:
                return toBoolOp(v);
            case TOINT:
                return toIntOp(v);
            case TOSTR:
                return toStrOp(v);
            default:
                Utils.abort(super.getLine());
                return null;
        }
    }

    private TextValue readOp(Value<?> v) {
        System.out.print(v);

        String text = input.nextLine().trim();
        return text.isEmpty() ? null : new TextValue(text);
    }

    private NumberValue randomOp(Value<?> v) {
        if (v instanceof NumberValue n) {
            return new NumberValue((int) (Math.random() * n.value()));
        }

        throw new RuntimeException("Invalid operand type");
    }

    private NumberValue lengthOp(Value<?> v) {
        if (v instanceof ListValue l) {
            return new NumberValue(l.value().size());
        }

        throw new RuntimeException("Invalid operand type");
    }

    private ListValue keysOp(Value<?> v) {
        if (v instanceof MapValue m) {
            List<Value<?>> keys = new ArrayList<>(m.value().keySet());
            return new ListValue(keys);
        }

        throw new RuntimeException("Invalid operand type");
    }

    private ListValue valuesOp(Value<?> v) {
        if (v instanceof MapValue m) {
            List<Value<?>> values = new ArrayList<>(m.value().values());
            return new ListValue(values);
        }

        throw new RuntimeException("Invalid operand type");
    }

    private BoolValue toBoolOp(Value<?> v) {
        boolean b;
        if (v == null) {
            b = false;
        } else if (v instanceof BoolValue bv) {
            b = bv.value();
        } else if (v instanceof NumberValue nv) {
            b = nv.value() != 0;
        } else if (v instanceof TextValue tv) {
            b = !tv.value().isEmpty();
        } else if (v instanceof ListValue lv) {
            b = !lv.value().isEmpty();
        } else if (v instanceof MapValue mv) {
            b = !mv.value().isEmpty();
        } else {
            b = true;
        }

        return new BoolValue(b);
    }

    private NumberValue toIntOp(Value<?> v) {
        int n;
        if (v == null) {
            n = 0;
        } else if (v instanceof BoolValue bv) {
            boolean b = bv.value();

            n = b ? 1 : 0;
        } else if (v instanceof NumberValue nv) {
            n = nv.value();
        } else if (v instanceof TextValue sv) {
            String s = sv.value();

            try {
                n = Integer.parseInt(s);
            } catch (Exception e) {
                n = 0;
            }
        } else {
            n = 0;
        }

        return new NumberValue(n);
    }

    private TextValue toStrOp(Value<?> v) {
        return new TextValue(v.value().toString());
    }

}
