package interpreter.command;

import interpreter.expr.Expr;
import interpreter.util.Utils;
import interpreter.value.BoolValue;

public class AssertCommand extends Command {

  private final Expr expr;
  private final Expr msg;

  public AssertCommand(int line, Expr expr, Expr msg) {
    super(line);
    this.expr = expr;
    this.msg = msg;
  }

  @Override
  public void execute() {
    if (expr != null) {
      var v = expr.expr();
      if (!(v instanceof BoolValue bv)) {
        Utils.abort(super.getLine());
        return;
      }

      boolean b = bv.value();

      if (!b) {
        if (msg != null) {
          var m = msg.expr();
          System.out.println("assert: " + m);
        } else {
          System.out.println("assert: not true");
        }
      }
    }
  }

}
