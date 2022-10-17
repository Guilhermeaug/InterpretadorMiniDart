package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.SetExpr;
import interpreter.expr.Variable;
import interpreter.value.Value;

public class AssignCommand extends Command {

    private final Expr rhs;
    private final SetExpr lhs;

    public AssignCommand(int line, Expr rhs, SetExpr lhs) {
        super(line);
        this.rhs = rhs;
        this.lhs = lhs;
    }

    @Override
    public void execute() {
        Value<?> v = rhs.expr();
        if (lhs != null)
            lhs.setValue(v);
    }

}
