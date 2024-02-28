import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LispInterpreter {
    private static LispInterpreter interpreter;
    private Map<String, Object> globalEnv = new HashMap<>();
    
    private LispInterpreter() {
        globalEnv.put("+", null);
    }
    public static LispInterpreter getInterpreter() {
        if (interpreter == null) {
            interpreter = new LispInterpreter();
        }
        return interpreter;
    }


    public Object eval(List<?> list) throws Exception {
        String first = (String) list.get(0);
        switch (first) {
            case "+":
                return sum(list.subList(1, list.size()));

            default:
                Object item = globalEnv.get(first);
                if (item.getClass() == LispFunction.class) {
                    if (list.size() - 1 == ((LispFunction) item).getParams().size()) {
                        return ((LispFunction) item).apply();
                    } else {
                        throw new Exception();
                    }
                } else {
                    return item;
                }
        }

    }

    public Object sum(List<?> args) throws Exception {
        Object result = 0; boolean stillInt = true;
        for (Object o : args) {
            Object tempResult;
            if (List.class.isAssignableFrom(o.getClass())) {
                tempResult = eval((List<?>) o );
            } else {
                tempResult = o;
            }
            if (tempResult.getClass() == Integer.class) {
                if (stillInt) {
                    result = (Integer) result + (Integer) tempResult;
                } else {
                    result = (Double) result + (Integer) tempResult;
                }
                
            } else {
                if (stillInt) {
                    result = (Integer) result + (Double) tempResult;
                    stillInt = false;
                } else {
                    result = (Double) result + (Double) tempResult;
                }
            }
            

        }
        return result;
    }

    

    
}
