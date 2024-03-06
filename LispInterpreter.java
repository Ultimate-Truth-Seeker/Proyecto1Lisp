import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LispInterpreter {
    private static LispInterpreter interpreter;
    private Map<String, Object> globalEnv = new HashMap<>();
    

    private LispInterpreter() {
        globalEnv.put("T", true);
        globalEnv.put("NIl", false);
    }
    /**
     * constructor Singleton para el intérprete
     * @return la instancia de LispInterpreter
     */
    public static LispInterpreter getInterpreter() {
        if (interpreter == null) {
            interpreter = new LispInterpreter();
        }
        return interpreter;
    }

    /**
     * Esta función construye el listado de argumentos en base a la entrada de texto ingresada. 
     * Manda a llamar al método de parsObject cada vez que carga un argumento para ingresar en su clase correspondiente.
     * Se pueden ingresar varias listas a la vez y el Main las ejecuta una por una.
     * Puede identificar comentarios.
     * 
     * 
     * @param expression la entrada del usuario 
     * @return un List que contiene los argumentos de la entrada
     * @throws Exception cuando la entrada tiene errores de sintáxis, como paréntesis abiertos, o comandos fuera de lista
     */
    public List<?> StringToLisp(String expression) throws Exception {
        boolean inList = false;
        boolean inArg = false;
        boolean inString = false;
        boolean inComentary = false;
        int nlists = 0;
        String arg = "";
        List<Object> list = new ArrayList<>();
        

        for (String c : expression.split("")) {
            if (c.isBlank()) {
                if (inArg && !inList && !inString) {
                    list.add(parseObject(arg));
                    inArg = false;
                    arg = "";

                } else if (inComentary && c.equals("\n")) {
                    inComentary = false;
                } else if (inArg) {
                    arg = arg.concat(c);
                } else {
                    continue;
                }
            } else {
                if (c.equals(";")) {
                    inComentary = true;
                    if (inArg) {
                        list.add(parseObject(arg));
                        arg = "";
                        inArg = false;
                    }
                    continue;
                }
                if (inComentary) {
                    continue;
                }
                if (c.equals("(") && !inString) {
                    if (inList) {
                        nlists += 1;
                        arg = arg.concat(c);
                    } else if (inArg) {
                        inArg = false; list.add(parseObject(arg)); arg = "";
                    }
                    inList = true;
                    inArg = true;
                    continue;
                }
                inArg = true;
                

                if (c.equals("\"") && !inList) {
                    inString = !inString;
                    arg = arg.concat(c);
                    if (!inString) {
                        list.add(arg); arg = ""; inArg = false;
                    }
                } else if (c.equals(")") && !inString) {
                    if (!inList) {
                        throw new Exception();
                    }
                    if (nlists > 0) {
                        nlists -= 1;
                        arg = arg.concat(c); continue;
                    }
                    inList = false; inArg = false;
                    list.add(StringToLisp(arg));
                    arg = "";
                    
                } else {
                    arg = arg.concat(c);
                }
            }


        }
        if (inList || inString){
            throw new Exception();
        }
        if (inArg) {
            list.add(parseObject(arg));
        }
        return list;
    }

    /**
     * Método que retorna un el tipo de dato que le corresponde al texto ingresado 
     * Para las palabras comando del intérprete se les guarda como un String
     * 
     * @param element la cadenta de texto del argumento
     * @return una instancia tipo Object que también es de la clase correspondiente al argumento 
     */
    public Object parseObject(String element) {
        Object toType = null;
        try {
            toType = Integer.parseInt(element);
        } catch (Exception e) {
            try {
                toType = Double.parseDouble(element);
            } catch (Exception e2) {
                if (element.equals("T")) {
                    toType = true;
                } else if (element.equals("NIL")){ 
                    toType = false;
                } else {
                    toType = (String) element;
                }
            }
        }
        return toType;
    }

    /**
     * Método principal del intérprete que evalúa y ejecuta las operaciones en una lista de Lisp y retorna el valor de salida de la función.
     * 
     * @param list una implementación de List que representa una lista de Lisp. 
     * @return el resultado de la función representada por la lista en el lenguaje de Lisp
     * @throws Exception cuando hay un error de ejecución
     */
    public Object eval(List<?> list) throws Exception {
        String first = (String) list.get(0); // Se obtiene el comando inicial
        switch (first) { // Se evalúa si es una función predeterminada, sino se llama al ambiente para retornar el valor de una función nueva
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
                    throw new Exception();
                }
        }

    }

    public Object sum(List<?> args) throws Exception {
        Object result = 0; boolean stillInt = true;
        for (Object o : args) {
            Object tempResult;
            if (List.class.isAssignableFrom(o.getClass())) {
                tempResult = eval((List<?>) o );
            } else if (o.getClass() == String.class && globalEnv.containsKey(o)) {
                tempResult = globalEnv.get(o);
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
