package interpreter.command;

import interpreter.expr.Expr;
import interpreter.util.Utils;
import interpreter.value.BoolValue;

public class IfCommand extends Command {

  private final Expr expr;
  private final Command thenCmds;
  private final Command elseCmds;

  public IfCommand(int line, Expr expr, Command thenCmds, Command elseCmds) {
    super(line);
    this.expr = expr;
    this.thenCmds = thenCmds;
    this.elseCmds = elseCmds;
  }

  @Override
  public void execute() {
    if (expr == null) {
      Utils.abort(super.getLine());
      return;
    }

    var v = expr.expr();
    if (!(v instanceof BoolValue bv)) {
      Utils.abort(super.getLine());
      return;
    }

    var b = bv.value();

    if (b) {
      thenCmds.execute();
    } else {
      if (elseCmds != null) {
        elseCmds.execute();
      }
    }

  }

}
