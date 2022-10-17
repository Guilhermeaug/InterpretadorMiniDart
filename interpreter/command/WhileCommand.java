package interpreter.command;

import interpreter.expr.Expr;
import interpreter.util.Utils;
import interpreter.value.BoolValue;
import interpreter.value.Value;

public class WhileCommand extends Command {

    private final Expr expr;
    private final Command cmds;

    public WhileCommand(int line, Expr expr, Command cmds) {
        super(line);
        this.expr = expr;
        this.cmds = cmds;
    }

    // ::= while '(' <expr> ')' <body>
    @Override
    public void execute() {
        while (true) {
            var v = expr.expr();
            if (!(v instanceof BoolValue bv)) {
                Utils.abort(super.getLine());
                return;
            }
            
            boolean b = bv.value();

            if (!b)
                break;

            cmds.execute();
        }
    }
    
}
