import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Clase que instancia funciones creadas en Lisp
 * @author AREA
 * @since Febrero 2024
 */
public class LispFunction {
    protected String name;
    protected List<?> params;
    protected List<?> body;
    
    
    /**
     * Ejecuta la función creada.
     * Asigna momentaneamente las variables para ejecutar el proceso descrito.
     * Obtiene la instancia singleton del intérprete para ejecutar sus métodos necesarios
     * 
     * @param argList los valores asignado a los parámetros de la función
     * @return el resultado de la función
     * @throws Exception si ocurre un error al evaluar un proceso de la función
     */
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


    /**
     * constructor de la clase
     * @param name
     * @param params
     * @param body
     */
    public LispFunction(String name, List<?> params, List<?> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    /**
     * 
     * @return la lista de parámetros de la función
     */
    public List<?> getParams() {
        return params;
    }



    
}
