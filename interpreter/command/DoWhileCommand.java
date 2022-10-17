package interpreter.command;

import interpreter.expr.Expr;
import interpreter.util.Utils;
import interpreter.value.BoolValue;

public class DoWhileCommand extends Command {

    private final Command cmds;
    private final Expr expr;

    public DoWhileCommand(int line, Command cmds, Expr expr) {
        super(line);
        this.cmds = cmds;
        this.expr = expr;
    }

    // <dowhile> ::= do <body> while '(' <expr> ')' ';'
    @Override
    public void execute() {
        cmds.execute();

        while (true) {
            var v = expr.expr();
            if (!(v instanceof BoolValue bv)) {
                Utils.abort(super.getLine());
                return;
            }

            if (!bv.value())
                break;

            cmds.execute();
        }

    }
}
