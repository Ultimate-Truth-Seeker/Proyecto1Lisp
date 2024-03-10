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
        globalEnv.put("+", null);
        globalEnv.put("-", null);
        globalEnv.put("*", null);
        globalEnv.put("/", null);
        globalEnv.put("<", null);
        globalEnv.put(">", null);
        globalEnv.put("=", null);
        globalEnv.put("QUOTE", null);
        globalEnv.put("ATOM", null);
        globalEnv.put("LIST", null);
        globalEnv.put("EQUAL", null);
        globalEnv.put("COND", null);
        globalEnv.put("SETQ", null);
        globalEnv.put("DEFUN", null);
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
     * Para las variables y funciones del intérprete se les guarda como un String
     * Las palabras con comillas se identifican inmediatamente como String y no pasan por este método.
     * 
     * 
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
        int numberOfParams = list.size() - 1;
        switch (first.toUpperCase()) { // Se evalúa si es una función predeterminada, sino se llama al ambiente para retornar el valor de una función nueva
            case "+":
                return sum(list.subList(1, list.size()));
            case "-":
                return substract(list.subList(1, list.size()));
            case "*":
                return product(list.subList(1, list.size()));
            case "/":
                return divide(list.subList(1, list.size()));
            case ">":
                return moreThan(list.subList(1, list.size()));
            case "<":
                return lessThan(list.subList(1, list.size()));
            case "=":
                return equal(list.subList(1, list.size()));
            case "EQUAL":
                if (numberOfParams != 2) {
                    throw new Exception();
                }
                return equal(list.subList(1, list.size()));
            case "ATOM":
                if (numberOfParams != 1) {
                    throw new Exception();
                }
                return atom(list.subList(1, list.size()));
            case "LIST":
                return list(list.subList(1, list.size()));
            case "COND":
                return cond(list.subList(1, list.size()));
            case "QUOTE":
                if (numberOfParams != 1) {
                    throw new Exception();
                }
                return quote(list.subList(1, list.size()));
            case "SETQ":
                if (numberOfParams != 2) {
                    throw new Exception();
                }
                setq(list.subList(1, list.size()));
                return null;
            case "DEFUN":
                if (numberOfParams != 3) {
                    throw new Exception();
                }
                defun(list.subList(1, list.size()));
                return null;

            default:
                Object item = globalEnv.get(first);
                if (item.getClass() == LispFunction.class) {
                    if (numberOfParams == ((LispFunction) item).getParams().size()) {
                        return ((LispFunction) item).apply(list.subList(1, list.size()));
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

    //TODO: implement methods

    public Object substract(List<?> args) throws Exception {
        if (args.size() < 2) {
            throw new Exception("No hay suficientes argumentos para la resta");
        }
        Object result = parseObject((String) args.get(0));
        for (int i = 1; i < args.size(); i++) {
            Object tempResult = parseObject((String) args.get(i));
            if (result instanceof Double || tempResult instanceof Double) {
                result = ((Number) result).doubleValue() - ((Number) tempResult).doubleValue();
            } else {
                result = ((Number) result).intValue() - ((Number) tempResult).intValue();
            }
        }
        return result;
    }

    public Object product(List<?> args) throws Exception {
        Object result = 1;
        for (Object arg : args) {
            Object tempResult = parseObject((String) arg);
            if (result instanceof Double || tempResult instanceof Double) {
                result = ((Number) result).doubleValue() * ((Number) tempResult).doubleValue();
            } else {
                result = ((Number) result).intValue() * ((Number) tempResult).intValue();
            }
        }
        return result;
    }

    public Object divide(List<?> args) throws Exception {
        if (args.size() != 2) {
        throw new Exception("La división necesita exactamente 2 valores");
    }
    Object dividend = parseObject((String) args.get(0));
    Object divisor = parseObject((String) args.get(1));
    if (((Number) divisor).doubleValue() == 0) {
        throw new Exception("No se puede dividir entre cero");
    }
    return ((Number) dividend).doubleValue() / ((Number) divisor).doubleValue();
}
    public boolean moreThan(List<?> args) throws Exception {
        return false;
    }

    public boolean lessThan(List<?> args) throws Exception {
        return false;
    }

    public boolean equal(List<?> args) throws Exception {
        return false;
    }

    public boolean atom(List<?> args) throws Exception {
        return false;
    }

    public boolean list(List<?> args) throws Exception {
        return false;
    }

    public Object cond(List<?> args) throws Exception {
        return false;
    }

    public Object quote(List<?> args) throws Exception {
        return null;
    }

    public void setq(List<?> args) throws Exception {
        return;
    }

    public void defun(List<?> args) throws Exception {
        return;
    }
    
}
