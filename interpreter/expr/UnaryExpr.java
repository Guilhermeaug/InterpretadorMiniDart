package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.BoolValue;
import interpreter.value.NumberValue;
import interpreter.value.Value;

public class UnaryExpr extends Expr {

    private final Expr expr;
    private final UnaryOp op;

    public UnaryExpr(int line, Expr expr, UnaryOp op) {
        super(line);
        this.expr = expr;
        this.op = op;
    }

    @Override
    public Value<?> expr() {
        switch (op) {
            case NEG:
                return negOp();
            case NOT:
                return notOp();
            case PRE_INC:
                return preIncOp();
            case POS_INC:
                return posIncOp();
            case PRE_DEC:
                return preDecOp();
            case POS_DEC:
                return posDecOp();
            default:
                Utils.abort(super.getLine());
                return null;
        }
    }

    private Value<?> negOp() {
        Value<?> v = expr.expr();
        if (v instanceof NumberValue nv) {
            int n = nv.value();
            int res = -n;
            return new NumberValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> notOp() {
        Value<?> v = expr.expr();
        if (v instanceof BoolValue bv) {
            boolean b = bv.value();
            boolean res = !b;
            return new BoolValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> preIncOp() {
        var v = expr.expr();
        if (v instanceof NumberValue nv) {
            int n = nv.value();
            int res = n + 1;

            if (expr instanceof Variable v1) {
                v1.setValue(new NumberValue(res));
            } else if (expr instanceof AccessExpr a) {
                a.setValue(new NumberValue(res));
            }

            return new NumberValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> posIncOp() {
        var v = expr.expr();
        if (v instanceof NumberValue nv) {
            int n = nv.value();
            int res = n + 1;

            if (expr instanceof Variable var) {
                var.setValue(new NumberValue(res));
            } else if (expr instanceof AccessExpr ae) {
                ae.setValue(new NumberValue(res));
            }

            return nv;
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> preDecOp() {
        var v = expr.expr();
        if (v instanceof NumberValue nv) {
            int n = nv.value();
            int res = n - 1;

            if (expr instanceof Variable v1) {
                v1.setValue(new NumberValue(res));
            } else if (expr instanceof AccessExpr a) {
                a.setValue(new NumberValue(res));
            }

            return new NumberValue(res);
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

    private Value<?> posDecOp() {
        var v = expr.expr();
        if (v instanceof NumberValue nv) {
            int n = nv.value();
            int res = n - 1;

            if (expr instanceof Variable var) {
                var.setValue(new NumberValue(res));
            } else if (expr instanceof AccessExpr ae) {
                ae.setValue(new NumberValue(res));
            }

            return nv;
        } else {
            Utils.abort(super.getLine());
            return null;
        }
    }

}
