package interpreter.expr;

import interpreter.util.Utils;
import interpreter.value.Value;

public class SafeVariable extends Variable {

    private boolean initialized;
    private Value<?> value;

    public SafeVariable(int line, String name, boolean constant) {
        super(line, name, constant);
        this.initialized = false;
        this.value = null;
    }

    public Value<?> expr() {
        if (!initialized) {
            Utils.abort(super.getLine());
            return null;
        }

        if (value == null) {
            Utils.abort(super.getLine());
            return null;
        }

        return value;
    }

    public void setValue(Value<?> value) {
        if (initialized && super.isConstant())
            Utils.abort(super.getLine());

        if (value == null) {
            Utils.abort(super.getLine());
            return;
        }

        this.value = value;
        this.initialized = true;
    }
}
