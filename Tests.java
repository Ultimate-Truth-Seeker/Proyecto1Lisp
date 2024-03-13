import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class Tests {

    @Test
    void testStringToLisp() throws Exception {
        List<Object> result = (List<Object>) LispInterpreter.getInterpreter().StringToLisp("(+ 1 2)");
        assertEquals(List.of("+", 1, 2), result.get(0));
    }

    @Test
    void testGetArg() throws Exception {
        LispInterpreter interpreter = LispInterpreter.getInterpreter();
        Field globalEnvField = LispInterpreter.class.getDeclaredField("globalEnv");
        globalEnvField.setAccessible(true);
        Map<String, Object> globalEnv = (Map<String, Object>) globalEnvField.get(interpreter);
        globalEnv.put("x", 77);
        Object result = interpreter.getArg("x");
        assertEquals(77, result);
    }

    @Test
    void testEval() throws Exception {
        LispInterpreter interpreter = LispInterpreter.getInterpreter();
        Field globalEnvField = LispInterpreter.class.getDeclaredField("globalEnv");
        globalEnvField.setAccessible(true);
        Map<String, Object> globalEnv = (Map<String, Object>) globalEnvField.get(interpreter);
        globalEnv.put("x", 3);
        globalEnv.put("y", 14);
        Object result = interpreter.eval(List.of("+", "x", "y"));
        assertEquals(17, result);
    }

    @Test
    void testApply() throws Exception {
        LispInterpreter interpreter = LispInterpreter.getInterpreter();
        LispFunction function = new LispFunction("add", List.of("x", "y"), List.of("+", "x", "y"));
        Field globalEnvField = LispInterpreter.class.getDeclaredField("globalEnv");
        globalEnvField.setAccessible(true);
        Map<String, Object> globalEnv = (Map<String, Object>) globalEnvField.get(interpreter);
        globalEnv.put("x", 8);
        globalEnv.put("y", 7);
        Object result = function.apply(List.of("x", "y"));
        assertEquals(15, result);
    }
}
