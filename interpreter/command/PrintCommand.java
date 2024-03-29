package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class PrintCommand extends Command {

    private final Expr expr;

    public PrintCommand(int line, Expr expr) {
        super(line);
        this.expr = expr;
    }

    @Override
    public void execute() {
        if (expr != null) {
            var v = expr.expr();
            System.out.print(v);
        }
        System.out.println();
    }
}
