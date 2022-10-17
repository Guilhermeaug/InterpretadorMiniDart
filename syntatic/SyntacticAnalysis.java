package syntatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.command.*;
import interpreter.expr.*;
import interpreter.util.Utils;
import interpreter.value.BoolValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntacticAnalysis {

    private final LexicalAnalysis lex;
    private Lexeme current;
    private final Map<String, Variable> memory;

    public SyntacticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        memory = new HashMap<>();
    }

    public Command start() {
        Command cmd = procCode();
        eat(TokenType.END_OF_FILE);
        return cmd;
    }

    private void advance() {
        // System.out.println("Advanced (\"" + current.token + "\", " +
        // current.type + ")");
        current = lex.nextToken();
    }

    private void eat(TokenType type) {
        // System.out.println("Expected (..., " + type + "), found (\"" +
        // current.token + "\", " + current.type + ")");
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN -> System.out.printf("Lexema inválido [%s]\n", current.token);
            case UNEXPECTED_EOF, END_OF_FILE -> System.out.print("Fim de arquivo inesperado\n");
            default -> System.out.printf("Lexema não esperado [%s]\n", current.token);
        }

        System.exit(1);
    }

    // <code> ::= { <cmd> }
    private BlocksCommand procCode() {
        int line = lex.getLine();
        List<Command> cmds = new ArrayList<>();
        while (current.type == TokenType.FINAL || current.type == TokenType.VAR || current.type == TokenType.PRINT
                || current.type == TokenType.ASSERT || current.type == TokenType.IF || current.type == TokenType.WHILE
                || current.type == TokenType.DO || current.type == TokenType.FOR || current.type == TokenType.NOT
                || current.type == TokenType.SUB || current.type == TokenType.INC || current.type == TokenType.DEC
                || current.type == TokenType.OPEN_PAR || current.type == TokenType.NULL
                || current.type == TokenType.FALSE || current.type == TokenType.TRUE || current.type == TokenType.NUMBER
                || current.type == TokenType.TEXT || current.type == TokenType.READ || current.type == TokenType.RANDOM
                || current.type == TokenType.LENGTH || current.type == TokenType.KEYS
                || current.type == TokenType.VALUES || current.type == TokenType.TOBOOL
                || current.type == TokenType.TOINT || current.type == TokenType.TOSTR || current.type == TokenType.NAME
                || current.type == TokenType.OPEN_BRA || current.type == TokenType.OPEN_CUR) {
            Command c = procCmd();
            cmds.add(c);
        }

        return new BlocksCommand(line, cmds);
    }

    // <cmd> ::= <decl> | <print> | <assert> | <if> | <while> | <dowhile> | <for> |
    // <assign>
    private Command procCmd() {
        Command cmd = null;
        switch (current.type) {
            case FINAL:
            case VAR:
                cmd = procDecl();
                break;
            case PRINT:
                cmd = procPrint();
                break;
            case ASSERT:
                cmd = procAssert();
                break;
            case IF:
                cmd = procIf();
                break;
            case WHILE:
                cmd = procWhile();
                break;
            case DO:
                cmd = procDoWhile();
                break;
            case FOR:
                cmd = procFor();
                break;
            case NOT:
            case SUB:
            case INC:
            case DEC:
            case OPEN_PAR:
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
            case READ:
            case RANDOM:
            case LENGTH:
            case KEYS:
            case VALUES:
            case TOBOOL:
            case TOINT:
            case TOSTR:
            case NAME:
            case OPEN_BRA:
            case OPEN_CUR:
                cmd = procAssign();
                break;
            default:
                showError();
                break;
        }

        return cmd;
    }

    // <decl> ::= [ final ] var [ '?' ] <name> [ '=' <expr> ] { ',' <name> [ '='
    // <expr> ] } ';'
    private BlocksCommand procDecl() {
        int line = lex.getLine();
        List<Command> cmds = new ArrayList<>();

        boolean constant = false;
        if (current.type == TokenType.FINAL) {
            advance();
            constant = true;
        }

        eat(TokenType.VAR);

        boolean nullable = false;
        if (current.type == TokenType.NULLABLE) {
            advance();
            nullable = true;
        }

        Variable var = procDeclarationName(constant, nullable);

        if (current.type == TokenType.ASSIGN) {
            line = lex.getLine();
            advance();

            Expr rhs = procExpr();

            AssignCommand acmd = new AssignCommand(line, rhs, var);
            cmds.add(acmd);
        }

        while (current.type == TokenType.COMMA) {
            advance();

            var = procDeclarationName(constant, nullable);

            if (current.type == TokenType.ASSIGN) {
                advance();

                Expr rhs = procExpr();

                AssignCommand acmd = new AssignCommand(line, rhs, var);
                cmds.add(acmd);
            }
        }

        eat(TokenType.SEMICOLON);

        return new BlocksCommand(line, cmds);
    }

    // <print> ::= print '(' [ <expr> ] ')' ';'
    private PrintCommand procPrint() {
        eat(TokenType.PRINT);
        int line = lex.getLine();

        eat(TokenType.OPEN_PAR);

        Expr expr = null;
        if (current.type == TokenType.NOT || current.type == TokenType.SUB || current.type == TokenType.INC
                || current.type == TokenType.DEC || current.type == TokenType.OPEN_PAR || current.type == TokenType.NULL
                || current.type == TokenType.FALSE || current.type == TokenType.TRUE || current.type == TokenType.NUMBER
                || current.type == TokenType.TEXT || current.type == TokenType.READ || current.type == TokenType.RANDOM
                || current.type == TokenType.LENGTH || current.type == TokenType.KEYS
                || current.type == TokenType.VALUES || current.type == TokenType.TOBOOL
                || current.type == TokenType.TOINT || current.type == TokenType.TOSTR || current.type == TokenType.NAME
                || current.type == TokenType.OPEN_BRA || current.type == TokenType.OPEN_CUR) {
            expr = procExpr();
        }
        eat(TokenType.CLOSE_PAR);
        eat(TokenType.SEMICOLON);

        return new PrintCommand(line, expr);
    }

    // <assert> ::= assert '(' <expr> [ ',' <expr> ] ')' ';'
    private AssertCommand procAssert() {
        eat(TokenType.ASSERT);
        int line = lex.getLine();

        eat(TokenType.OPEN_PAR);

        Expr expr = procExpr();
        Expr msg = null;
        if (current.type == TokenType.COMMA) {
            advance();
            msg = procExpr();
        }
        eat(TokenType.CLOSE_PAR);
        eat(TokenType.SEMICOLON);

        return new AssertCommand(line, expr, msg);
    }

    // <if> ::= if '(' <expr> ')' <body> [ else <body> ]
    private IfCommand procIf() {
        eat(TokenType.IF);
        int line = lex.getLine();

        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);
        Command thenCmds = procBody();
        Command elseCmds = null;
        if (current.type == TokenType.ELSE) {
            advance();
            elseCmds = procBody();
        }

        return new IfCommand(line, expr, thenCmds, elseCmds);
    }

    // <while> ::= while '(' <expr> ')' <body>
    private WhileCommand procWhile() {
        eat(TokenType.WHILE);
        int line = lex.getLine();

        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);
        Command cmds = procBody();

        return new WhileCommand(line, expr, cmds);
    }

    // <dowhile> ::= do <body> while '(' <expr> ')' ';'
    private DoWhileCommand procDoWhile() {
        eat(TokenType.DO);
        int line = lex.getLine();

        Command command = procBody();
        eat(TokenType.WHILE);
        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();

        eat(TokenType.CLOSE_PAR);
        eat(TokenType.SEMICOLON);

        return new DoWhileCommand(line, command, expr);
    }

    // <for> ::= for '(' <name> in <expr> ')' <body>
    private ForCommand procFor() {
        eat(TokenType.FOR);
        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);

        Variable v = procName();
        eat(TokenType.IN);

        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);

        Command cmd = procBody();

        return new ForCommand(line, v, expr, cmd);
    }

    // <body> ::= <cmd> | '{' <code> '}'
    private Command procBody() {
        Command cmds;
        if (current.type == TokenType.OPEN_CUR) {
            advance();
            cmds = procCode();
            eat(TokenType.CLOSE_CUR);
        } else {
            cmds = procCmd();
        }

        return cmds;
    }

    // <assign> ::= [ <expr> '=' ] <expr> ';'
    private AssignCommand procAssign() {
        Expr rhs = procExpr();
        SetExpr lhs = null;

        int line = lex.getLine();
        if (current.type == TokenType.ASSIGN) {
            advance();

            if (!(rhs instanceof SetExpr)) {
                Utils.abort(line);
                return null;
            }

            lhs = (SetExpr) rhs;
            rhs = procExpr();
        }

        eat(TokenType.SEMICOLON);

        return new AssignCommand(line, rhs, lhs);
    }

    // <expr> ::= <cond> [ '??' <cond> ]
    private Expr procExpr() {
        Expr expr1 = procCond();
        Expr expr2 = null;
        if (current.type == TokenType.IF_NULL) {
            advance();
            expr2 = procCond();
        }

        if (expr2 != null) {
            return new BinaryExpr(expr1.getLine(), expr1, BinaryOp.IF_NULL, expr2);
        } else {
            return expr1;
        }
    }

    // <cond> ::= <rel> { ( '&&' | '||' ) <rel> }
    private Expr procCond() {
        Expr expr = procRel();
        while (current.type == TokenType.AND || current.type == TokenType.OR) {
            if (current.type == TokenType.AND) {
                advance();
            } else {
                advance();
            }

            procRel();
        }

        return expr;
    }

    // <rel> ::= <arith> [ ( '<' | '>' | '<=' | '>=' | '==' | '!=' ) <arith> ]
    private Expr procRel() {
        Expr left = procArith();

        if (current.type == TokenType.LOWER_THAN || current.type == TokenType.GREATER_THAN
                || current.type == TokenType.LOWER_EQUAL || current.type == TokenType.GREATER_EQUAL
                || current.type == TokenType.EQUAL || current.type == TokenType.NOT_EQUAL) {
            BinaryOp op = null;
            switch (current.type) {
                case LOWER_THAN:
                    op = BinaryOp.LOWER_THAN;
                    advance();
                    break;
                case GREATER_THAN:
                    op = BinaryOp.GREATER_THAN;
                    advance();
                    break;
                case LOWER_EQUAL:
                    op = BinaryOp.LOWER_EQUAL;
                    advance();
                    break;
                case GREATER_EQUAL:
                    op = BinaryOp.GREATER_EQUAL;
                    advance();
                    break;
                case EQUAL:
                    op = BinaryOp.EQUAL;
                    advance();
                    break;
                default:
                    op = BinaryOp.NOT_EQUAL;
                    advance();
                    break;
            }

            int line = lex.getLine();
            Expr right = procArith();

            left = new BinaryExpr(line, left, op, right);
        }

        return left;
    }

    // <arith> ::= <term> { ( '+' | '-' ) <term> }
    private Expr procArith() {
        Expr left = procTerm();

        while (current.type == TokenType.ADD || current.type == TokenType.SUB) {
            BinaryOp op;
            if (current.type == TokenType.ADD) {
                op = BinaryOp.ADD;
                advance();
            } else {
                op = BinaryOp.SUB;
                advance();
            }
            int line = lex.getLine();

            Expr right = procTerm();

            left = new BinaryExpr(line, left, op, right);
        }

        return left;
    }

    // <term> ::= <prefix> { ( '*' | '/' | '%' ) <prefix> }
    private Expr procTerm() {
        Expr left = procPrefix();
        while (current.type == TokenType.MUL || current.type == TokenType.DIV || current.type == TokenType.MOD) {
            BinaryOp op;
            if (current.type == TokenType.MUL) {
                op = BinaryOp.MUL;
                advance();
            } else if (current.type == TokenType.DIV) {
                op = BinaryOp.DIV;
                advance();
            } else {
                op = BinaryOp.MOD;
                advance();
            }

            int line = lex.getLine();
            Expr right = procPrefix();

            left = new BinaryExpr(line, left, op, right);
        }

        return left;
    }

    // <prefix> ::= [ '!' | '-' | '++' | '--' ] <factor>
    private Expr procPrefix() {
        UnaryOp op = null;
        if (current.type == TokenType.NOT || current.type == TokenType.SUB || current.type == TokenType.INC
                || current.type == TokenType.DEC) {
            switch (current.type) {
                case NOT -> op = UnaryOp.NOT;
                case SUB -> op = UnaryOp.NEG;
                case INC -> op = UnaryOp.PRE_INC;
                default -> op = UnaryOp.POS_INC;
            }
            advance();
        }

        int line = lex.getLine();
        Expr expr = procFactor();

        if (op != null) {
            return new UnaryExpr(line, expr, op);
        }

        return expr;
    }

    // <factor> ::= ( '(' <expr> ')' | <rvalue> ) [ '++' | '--' ]
    private Expr procFactor() {
        Expr expr;
        if (current.type == TokenType.OPEN_PAR) {
            advance();
            expr = procExpr();
            eat(TokenType.CLOSE_PAR);
        } else {
            expr = procRValue();
        }

        int line = lex.getLine();
        if (current.type == TokenType.INC || current.type == TokenType.DEC) {
            UnaryExpr ue;
            if (current.type == TokenType.INC) {
                advance();
                ue = new UnaryExpr(line, expr, UnaryOp.POS_INC);
            } else {
                advance();
                ue = new UnaryExpr(line, expr, UnaryOp.POS_DEC);
            }
            return ue;
        }

        return expr;
    }

    // <rvalue> ::= <const> | <function> | <lvalue> | <list> | <map>
    private Expr procRValue() {
        Expr expr = null;
        switch (current.type) {
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
                expr = procConst();
                break;
            case READ:
            case RANDOM:
            case LENGTH:
            case KEYS:
            case VALUES:
            case TOBOOL:
            case TOINT:
            case TOSTR:
                expr = procFunction();
                break;
            case NAME:
                expr = procLValue();
                break;
            case OPEN_BRA:
                expr = procList();
                break;
            case OPEN_CUR:
                expr = procMap();
                break;
            default:
                showError();
                break;
        }

        return expr;
    }

    // <const> ::= null | false | true | <number> | <text>
    private ConstExpr procConst() {
        Value<?> v = null;
        switch (current.type) {
            case NULL:
                advance();
                break;
            case FALSE:
                advance();
                v = new BoolValue(false);
                break;
            case TRUE:
                advance();
                v = new BoolValue(true);
                break;
            case NUMBER:
                v = procNumber();
                break;
            case TEXT:
                v = procText();
                break;
            default:
                showError();
                break;
        }

        int line = lex.getLine();
        return new ConstExpr(line, v);
    }

    // <function> ::= ( read | random | length | keys | values | tobool | toint |
    // tostr ) '(' <expr> ')'
    private FunctionExpr procFunction() {
        FunctionOp op = null;
        switch (current.type) {
            case READ:
                advance();
                op = FunctionOp.READ;
                break;
            case RANDOM:
                advance();
                op = FunctionOp.RANDOM;
                break;
            case LENGTH:
                advance();
                op = FunctionOp.LENGTH;
                break;
            case KEYS:
                advance();
                op = FunctionOp.KEYS;
                break;
            case VALUES:
                advance();
                op = FunctionOp.VALUES;
                break;
            case TOBOOL:
                advance();
                op = FunctionOp.TOBOOL;
                break;
            case TOINT:
                advance();
                op = FunctionOp.TOINT;
                break;
            case TOSTR:
                advance();
                op = FunctionOp.TOSTR;
                break;
            default:
                showError();
                break;
        }
        int line = lex.getLine();

        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);

        return new FunctionExpr(line, op, expr);
    }

    // <lvalue> ::= <name> { '[' <expr> ']' }
    private SetExpr procLValue() {
        SetExpr base = procName();
        while (current.type == TokenType.OPEN_BRA) {
            advance();
            int line = lex.getLine();

            Expr index = procExpr();

            base = new AccessExpr(line, base, index);

            eat(TokenType.CLOSE_BRA);
        }

        return base;
    }

    // <list> ::= '[' [ <l-elem> { ',' <l-elem> } ] ']'
    private ListExpr procList() {
        eat(TokenType.OPEN_BRA);
        int line = lex.getLine();

        ListExpr expr = new ListExpr(line);
        if (current.type != TokenType.CLOSE_BRA) {
            ListItem item = procLElem();
            expr.addItem(item);

            while (current.type == TokenType.COMMA) {
                advance();
                item = procLElem();
                expr.addItem(item);
            }
        }

        eat(TokenType.CLOSE_BRA);
        return expr;
    }

    // <l-elem> ::= <l-single> | <l-spread> | <l-if> | <l-for>
    private ListItem procLElem() {
        return switch (current.type) {
            case SPREAD -> procLSpread();
            case IF -> procLIf();
            case FOR -> procLFor();
            default -> procLSingle();
        };
    }

    // <l-single> ::= <expr>
    private SingleListItem procLSingle() {
        int line = lex.getLine();
        Expr expr = procExpr();
        return new SingleListItem(line, expr);
    }

    // <l-spread> ::= '...' <expr>
    private SpreadListItem procLSpread() {
        eat(TokenType.SPREAD);
        int line = lex.getLine();
        Expr expr = procExpr();
        return new SpreadListItem(line, expr);
    }

    // <l-if> ::= if '(' <expr> ')' <l-elem> [ else <l-elem> ]
    private IfListItem procLIf() {
        eat(TokenType.IF);
        eat(TokenType.OPEN_PAR);

        int line = lex.getLine();
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);

        ListItem thenItem = procLElem();
        ListItem elseItem = null;
        if (current.type == TokenType.ELSE) {
            advance();
            elseItem = procLElem();
        }

        return new IfListItem(line, expr, thenItem, elseItem);
    }

    // <l-for> ::= for '(' <name> in <expr> ')' <l-elem>
    private ForListItem procLFor() {
        eat(TokenType.FOR);
        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);

        Variable var = procName();
        eat(TokenType.IN);

        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);

        ListItem item = procLElem();

        return new ForListItem(line, var, expr, item);
    }

    // <map> ::= '{' [ <m-elem> { ',' <m-elem> } ] '}'
    private MapExpr procMap() {
        eat(TokenType.OPEN_CUR);
        int line = lex.getLine();

        MapExpr mexpr = new MapExpr(line);

        if (current.type == TokenType.NOT || current.type == TokenType.SUB || current.type == TokenType.INC
                || current.type == TokenType.DEC || current.type == TokenType.OPEN_PAR || current.type == TokenType.NULL
                || current.type == TokenType.FALSE || current.type == TokenType.TRUE || current.type == TokenType.NUMBER
                || current.type == TokenType.TEXT || current.type == TokenType.READ || current.type == TokenType.RANDOM
                || current.type == TokenType.LENGTH || current.type == TokenType.KEYS
                || current.type == TokenType.VALUES || current.type == TokenType.TOBOOL
                || current.type == TokenType.TOINT || current.type == TokenType.TOSTR || current.type == TokenType.NAME
                || current.type == TokenType.OPEN_BRA || current.type == TokenType.OPEN_CUR) {
            MapItem item = procMElem();
            mexpr.addItem(item);

            while (current.type == TokenType.COMMA) {
                advance();
                item = procMElem();
                mexpr.addItem(item);
            }
        }
        eat(TokenType.CLOSE_CUR);

        return mexpr;
    }

    // <m-elem> ::= <expr> ':' <expr>
    private MapItem procMElem() {
        Expr key = procExpr();
        eat(TokenType.COLON);
        Expr value = procExpr();

        return new MapItem(key, value);
    }

    private Variable procDeclarationName(boolean constant, boolean nullable) {
        String name = current.token;
        eat(TokenType.NAME);
        int line = lex.getLine();

        if (memory.containsKey(name))
            Utils.abort(line);

        Variable var;
        if (nullable) {
            var = new UnsafeVariable(line, name, constant);
        } else {
            var = new SafeVariable(line, name, constant);
        }

        memory.put(name, var);

        return var;
    }

    private Variable procName() {
        String name = current.token;
        eat(TokenType.NAME);
        int line = lex.getLine();

        if (!memory.containsKey(name))
            Utils.abort(line);

        return memory.get(name);
    }

    private NumberValue procNumber() {
        String txt = current.token;
        eat(TokenType.NUMBER);

        int n;
        try {
            n = Integer.parseInt(txt);
        } catch (Exception e) {
            n = 0;
        }

        return new NumberValue(n);
    }

    private TextValue procText() {
        String txt = current.token;
        eat(TokenType.TEXT);

        return new TextValue(txt);
    }
}
