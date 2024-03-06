import java.util.List;
import java.util.Map;

public class LispFunction {
    protected String name;
    protected List<?> params;
    protected List<?> body;
    protected Map<String, Object> env;
    
    

    public Object apply(List<?> argList) {
        return null;
    }



    public LispFunction(String name, List<?> params, List<?> body, Map<String, Object> env) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.env = env;
    }

    public List<?> getParams() {
        return params;
    }



    
}
