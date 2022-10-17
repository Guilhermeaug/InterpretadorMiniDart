package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.ListExpr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.ListValue;

public class ForCommand extends Command {

    private final Variable var;
    private final Expr expr;
    private final Command cmds;

    public ForCommand(int line, Variable var, Expr expr, Command cmds) {
        super(line);
        this.var = var;
        this.expr = expr;
        this.cmds = cmds;
    }

    // for '(' <name> in <expr> ')' <body>
    @Override
    public void execute() {
        var v = expr.expr();

        if(!(v instanceof ListValue lv)){
            System.out.println("Error: " + " expression is not a list");
            Utils.abort(super.getLine());
            return;
        }

        var list = lv.value();
        for (var item : list) {
            var.setValue(item);
            cmds.execute();
        }

    }
}
