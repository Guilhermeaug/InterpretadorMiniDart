package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.util.Utils;
import interpreter.value.MapValue;
import interpreter.value.Value;

public class MapExpr extends Expr {
    
    private final List<MapItem> map;
    
    public MapExpr(int line) {
        super(line);
        map = new ArrayList<>();
    }

    public void addItem(MapItem item) {
        map.add(item);
    }

    @Override
    public Value<?> expr() {
        Map<Value<?>, Value<?>> m = new HashMap<>();
            
        for (MapItem item : map) {
            Value<?> key = item.key.expr();
            if (key == null)
                Utils.abort(super.getLine());
                
            Value<?> value = item.value.expr();

            m.put(key, value);
        }

        return new MapValue(m);
    }

}
