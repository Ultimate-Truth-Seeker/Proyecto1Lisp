import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class LispFunction {
    protected String name;
    protected List<?> params;
    protected List<?> body;
    protected Map<String, Object> env;
    
    

    public Object apply(List<?> argList) throws Exception {
        Stack<Map<String, Object>> localEnvs = LispInterpreter.getInterpreter().getLocalEnvs();
        localEnvs.push(new HashMap<>(localEnvs.peek()));


            for (int i = 0; i < params.size(); i++) {
                Object arg = argList.get(i);
                if (List.class.isAssignableFrom(arg.getClass())){
                    arg = LispInterpreter.getInterpreter().eval((List<?>)arg);
                } else if (arg.getClass() == String.class && localEnvs.peek().containsKey(arg)) {
                    arg = LispInterpreter.getInterpreter().getLocalEnvs().peek().get(arg);
                }


                localEnvs.peek().put((String) params.get(i), arg);
            }

            Object result = null;
            result = LispInterpreter.getInterpreter().eval(body);

            localEnvs.pop();
            LispInterpreter.getInterpreter().updateGlobalEnv();
            return result;
    }



    public LispFunction(String name, List<?> params, List<?> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public List<?> getParams() {
        return params;
    }



    
}
