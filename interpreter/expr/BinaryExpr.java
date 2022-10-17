package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.BoolValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;
import org.w3c.dom.Text;

public class BinaryExpr extends Expr {

    private final Expr left;
    private final BinaryOp op;
    private final Expr right;

    public BinaryExpr(int line, Expr left, BinaryOp op, Expr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value<?> expr() {
        Value<?> v1 = left.expr();
        Value<?> v2 = right.expr();

        switch (op) {
            case IF_NULL:
                return ifNullOp(v1, v2);
            case AND:
                return andOp(v1, v2);
            case OR:
                return orOp(v1, v2);
            case EQUAL:
                return equalOp(v1, v2);
            case NOT_EQUAL:
                return notEqualOp(v1, v2);
            case LOWER_THAN:
                return lowerThanOp(v1, v2);
            case LOWER_EQUAL:
                return lowerEqualOp(v1, v2);
            case GREATER_THAN:
                return greaterThanOp(v1, v2);
            case GREATER_EQUAL:
                return greaterEqualOp(v1, v2);
            case ADD:
                return addOp(v1, v2);
            case SUB:
                return subOp(v1, v2);
            case MUL:
                return mulOp(v1, v2);
            case DIV:
                return divOp(v1, v2);
            case MOD:
                return modOp(v1, v2);
            default:
                Utils.abort(super.getLine());
                return null;
        }
    }

    private Value<?> ifNullOp(Value<?> v1, Value<?> v2) {
        if (v1 != null) return v1;
        else return v2;
    }

    private Value<?> andOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof BoolValue b1 && v2 instanceof BoolValue b2) {
            return new BoolValue(b1.value() && b2.value());
        }
        Utils.abort(super.getLine());
        return null;
    }

    private Value<?> orOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof BoolValue b1 && v2 instanceof BoolValue b2) {
            return new BoolValue(b1.value() || b2.value());
        }
        Utils.abort(super.getLine());
        return null;
    }

    private Value<?> equalOp(Value<?> v1, Value<?> v2) {
        if (v1 != null && v2 != null) {
            return new BoolValue(v1.value().equals(v2.value()));
        }

        if (v1 == null && v2 == null) {
            return new BoolValue(true);
        }

        return new BoolValue(false);
    }

    private Value<?> notEqualOp(Value<?> v1, Value<?> v2) {
        if (v1 != null && v2 != null) {
            return new BoolValue(!v1.value().equals(v2.value()));
        }

        if (v1 == null && v2 == null) {
            return new BoolValue(false);
        }

        return new BoolValue(true);
    }

    private Value<?> lowerThanOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            int n1 = nv1.value();
            int n2 = nv2.value();
            boolean res = n1 < n2;

            return new BoolValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> lowerEqualOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            int n1 = nv1.value();
            int n2 = nv2.value();
            boolean res = n1 <= n2;

            return new BoolValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> greaterThanOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            int n1 = nv1.value();
            int n2 = nv2.value();
            boolean res = n1 > n2;

            return new BoolValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> greaterEqualOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            int n1 = nv1.value();
            int n2 = nv2.value();
            boolean res = n1 >= n2;

            return new BoolValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> addOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            return new NumberValue(nv1.value() + nv2.value());
        }

        if (v1 instanceof TextValue tv1 && v2 instanceof TextValue tv2) {
            return new TextValue(tv1.value() + tv2.value());
        }

        Utils.abort(super.getLine());
        return null;
    }

    private Value<?> subOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            return new NumberValue(nv1.value() - nv2.value());
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> mulOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            return new NumberValue(nv1.value() * nv2.value());
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> divOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            int n1 = nv1.value();
            int n2 = nv2.value();
            if (n2 != 0) {
                int res = n1 / n2;
                return new NumberValue(res);
            }
        }

        Utils.abort(super.getLine());
        return null;
    }

    private Value<?> modOp(Value<?> v1, Value<?> v2) {
        if (v1 instanceof NumberValue nv1 && v2 instanceof NumberValue nv2) {
            int n1 = nv1.value();
            int n2 = nv2.value();
            if (n2 != 0) {
                int res = n1 % n2;
                return new NumberValue(res);
            }
        }

        Utils.abort(super.getLine());
        return null;
    }

}
