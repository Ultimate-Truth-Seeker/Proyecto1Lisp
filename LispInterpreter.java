import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Clase Singleton que genera un intérprete para Common Lisp
 * 
 * @author Grupo de Proyecto1Lisp
 * @since Febrero 2024
 */
public class LispInterpreter {
    private static LispInterpreter interpreter;
    private Map<String, Object> globalEnv = new HashMap<>();
    private Stack<Map<String, Object>> localEnvs = new Stack<>();

    /**
     * Constructor privado de la clase. 
     * Al momento de instanciarse carga al entorno global los nombres de las funciones primitivas.
     * También añade otras funciones lógicas de apoyo usando las funciones básicas y arroja error si ocurre un error en el proceso.
     * 
     */
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

        localEnvs.push(globalEnv);

        String or = "defun or (h k) (cond ((= h t) t) ((= k t) t) (t nil))";
        String and = "defun and (h k) (cond ((= h nil) nil) ((= k nil) nil) (t t))";
        String not = "defun not (h) (cond (h nil) (t t))";
        try {
            eval(StringToLisp(or)); eval(StringToLisp(and)); eval(StringToLisp(not));
        } catch (Exception e) {
            System.err.println("Error de carga. No se puede usar puertas lógicas.");
        }

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
     * 
     * @return retorna el stack de ambientes locales de las funciones
     */
    public Stack<Map<String, Object>> getLocalEnvs() {
        return localEnvs;
    }

    /**
     * esta función puede actualizar el entorno en uso cada vez que se utiliza.
     * necesario cuando hay asignaciones temporales en las funciones
     */
    public void updateGlobalEnv() {
        globalEnv = localEnvs.peek();
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
        boolean inList = false; // banderas de lectura
        boolean inArg = false;
        boolean inString = false;
        boolean inComentary = false;
        int nlists = 0;
        String arg = "";
        List<Object> list = new ArrayList<>();
        

        for (String c : expression.split("")) {// lee cada letra y construye los objetos en base a las palabras
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
        if (inList || inString){// Arroja error si una cadena de texto o una lista no se cerró
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
                if (element.toUpperCase().equals("T")) {
                    toType = true;
                } else if (element.toUpperCase().equals("NIL")){ 
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
     * No evalúa elementos individuales como variables o objetos.
     * 
     * @param list una implementación de List que representa una lista de Lisp. 
     * @return el resultado de la función representada por la lista en el lenguaje de Lisp
     * @throws Exception cuando hay un error de ejecución
     */
    public Object eval(List<?> list) throws Exception {
        String first = (String) list.get(0); // Se obtiene el comando inicial
        int numberOfParams = list.size() - 1;
        globalEnv = localEnvs.peek();
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
                if (item == null) {
                    throw new Exception("Variable no definida: " + first);
                }
                if (item.getClass() == LispFunction.class) {
                    if (numberOfParams == ((LispFunction) item).getParams().size()) {
                        Object result = ((LispFunction) item).apply(list.subList(1, list.size()));
                        return result;
                    } else {
                        throw new Exception("Número incorrecto de parámetros para la función: " + first);
                    }
                } else {
                    throw new Exception("Nombre no reconocido: " + first);
                }
        }

    }

    /**
     * Método para evaluar un argumento. Necesario para poder realizar las operaciones entre tipos adecuados. 
     * Se evalúan las funciones y se retornan los datos de variables. 
     * Retorna el objeto mismo si no es función ni variable
     * 
     * 
     * @param arg el argumento a evaluar
     * @return el resultado de la función, la asignación de variable o el objeto mismo
     * @throws Exception si ocurre un error al evaluar
     */
    public Object getArg(Object arg) throws Exception {
        if (List.class.isAssignableFrom(arg.getClass())) {
            return eval((List<?>) arg);
        } else if (arg.getClass() == String.class && globalEnv.containsKey(arg)) {
            return globalEnv.get(arg);
        }
        return arg;
    }

    /**
     * Método para sumar números de una lista
     * @param args los argumentos de la lista
     * @return el resultado de la suma o 0 si está vacía
     * @throws Exception si se intenta sumar un dato no numérico
     */
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

    /**
     * método para restar elementos de una lista
     * @param args los números que se restan
     * @return el resultado de la resta en tipo doble o el primer número si era el único argumento
     * @throws Exception si la lista viene vacía o si se usan datos no numéricos
     */
    public Object substract(List<?> args) throws Exception {
        if (args.isEmpty()) {
            throw new Exception("No arguments for subtraction");
        }
    
        Object result = getArg(args.get(0));
        if (! (result instanceof Number)) {
            throw new Exception("No retorna un número: " + args.get(0));
        }

        for (int i = 1; i < args.size(); i++) {
            Object next = getArg(args.get(i));
            if (! (next instanceof Number)) {
                throw new Exception("No retorna un número: " + args.get(i));
            }
            result = ((Number) result).doubleValue() - ((Number) next).doubleValue();
        }
    
        return result;
    }
    
    /**
     * método para obtener el producto de los números de una lista
     * @param args los argumentos de la lista
     * @return el resultado del producto en tipo doble
     * @throws Exception si la lista está vacía o se intenta multiplicar datos no numéricos
     */
    public Object product(List<?> args) throws Exception {
        if (args.isEmpty()) {
            throw new Exception("No arguments for multiplication");
        }
    
        double result = 1.0;

        for (Object arg : args) {
            result *= ((Number) getArg(arg)).doubleValue();
        }
    
        return result;
    }
    
    /**
     * Método para dividir dos números
     * @param args los números de la división
     * @return el resultado en tipo doble
     * @throws Exception si no hay exactamente dos números, se intenta dividir entre 0 o con datos no numéricos
     */
    public Object divide(List<?> args) throws Exception {
        if (args.size() != 2) {
        throw new Exception("Expected exactly two arguments for division");
        }

        double numerator = ((Number) getArg(args.get(0))).doubleValue();
        double denominator = ((Number) getArg(args.get(1))).doubleValue();

        if (denominator == 0) {
        throw new Exception("Cannot divide by zero");
        }

        return numerator / denominator;
    }

    /**
     * La función 'moreThan' toma una lista de dos argumentos y devuelve verdadero si el primer argumento es mayor que el segundo.
     * @param args los dos argumentos
     * @return verdadero si el primero es mayor, falso si no
     * @throws Exception si no hay exactamente dos argumentos o se comparan datos no numéricos 
     */
    public boolean moreThan(List<?> args) throws Exception {
        if (args.size() != 2) {
            throw new Exception();
        }
        Object arg1 = getArg(args.get(0));
        Object arg2 = getArg(args.get(1));
        if (arg1 instanceof Number && arg2 instanceof Number) {
            return ((Number) arg1).doubleValue() > ((Number) arg2).doubleValue();
        } else {
            throw new Exception();
        }
    }

    /**
     * La función 'lessThan' toma una lista de dos argumentos y devuelve verdadero si el primer argumento es menor que el segundo.
     * @param args los dos argumentos
     * @return verdadero si el primero es menor, falso si no
     * @throws Exception si no hay solo dos argumentos o si son tipos no numéricos
     */
    public boolean lessThan(List<?> args) throws Exception {
        if (args.size() != 2) {
            throw new Exception();
        }
        Object arg1 = getArg(args.get(0));
        Object arg2 = getArg(args.get(1));
        if (arg1 instanceof Number && arg2 instanceof Number) {
            return ((Number) arg1).doubleValue() < ((Number) arg2).doubleValue();
        } else {
            throw new Exception();
        }
    }

    /**
     * La función 'equal' toma una lista de dos argumentos y devuelve verdadero si ambos argumentos son iguales.
     * @param args los argumentos
     * @return verdadero si ambos elementos representan el mismo objeto
     * @throws Exception si no hay solo dos argumentos
     */
    public boolean equal(List<?> args) throws Exception {
        if (args.size() != 2) {
            throw new Exception();
        }
        Object arg1 = getArg(args.get(0));
        Object arg2 = getArg(args.get(1));
        return arg1.equals(arg2);
    }

    /**
     * La función 'atom' toma una lista de argumentos y devuelve verdadero si el primer argumento no es una lista.
     * @param args el argumento
     * @return verdadero si se evalúa en un solo elemento y no es lista
     * @throws Exception si hau más de un argumento
     */
    public boolean atom(List<?> args) throws Exception {
        if (args.size() != 1) {
            throw new Exception();
        }
        Object arg = getArg(args.get(0));
        return !(arg instanceof List);
        
    }

    /**
     * La función 'list' toma una lista de argumentos y devuelve verdadero si el primer argumento es una lista.
     * @param args el elemento a evaluar
     * @return verdadero si es una instancia de lista 
     * @throws Exception si hay más de un argumento
     */
    public boolean list(List<?> args) throws Exception {
        if (args.size() != 1) {
            throw new Exception();
        }
        Object arg = args.get(0);
        return arg instanceof List;
    }

    /**
     * método que evlúa una serie de condiciones y retorna la operación correspondiente en cuanto se haga cumplir una de ellas
     * retorna nulo en caso que ninguna se cumpla
     * 
     * @param args el listado de pares condición-resultado a evaluar
     * @return el resultado de la primera condición cumplida, o nulo si ninguna se cumple
     * @throws Exception si los argumentos no son pares condición-resultado
     */
    public Object cond(List<?> args) throws Exception {
        for (Object arg : args) {
        if (!(arg instanceof List) || ((List<?>) arg).size() != 2) {
            throw new Exception("Each argument to cond must be a pair");
        }

        List<?> pair = (List<?>) arg;
        Object condition = pair.get(0);
        Object expression = pair.get(1);

        // Evalúa la condición. Si es verdadera, evalúa y devuelve la expresión.
        Object conditionResult;
        if (condition instanceof List) {
            conditionResult = eval((List<?>) condition);
        } else if (condition instanceof String && globalEnv.containsKey(condition)) {
            conditionResult = globalEnv.get(condition);
        } else {
            conditionResult = condition;
        }

        if (Boolean.TRUE.equals(conditionResult)) {
            if (expression instanceof List) {
                return eval((List<?>) expression);
            } else if (expression instanceof String && globalEnv.containsKey(expression)) {
                return globalEnv.get(expression);
            } else {
                return expression;
            }
        }
    }

    // Si ninguna condición se evalúa como verdadera, devuelve null.
    return null;    }

    /**
     * método que retorna el argumento sin evaluarlo
     * @param args el argumento
     * @return ese mismo argumento sin hacerle nada
     * @throws Exception si hay más de un argumento
     */
    public Object quote(List<?> args) throws Exception {
        // La función quote en Lisp simplemente devuelve su argumento sin evaluarlo.
        // En otras palabras, quote es una forma de especificar datos literales en Lisp.

        if (args.size() != 1) {
        throw new Exception("Expected exactly one argument for quote");
         }

        // Devuelve el argumento tal cual, sin evaluarlo.
        return args.get(0);
    }

    /**
     * método para asignar variables de manera dinámica
     * Solo puede asignar valores concretos o de otras variables. No puede asignarse a variables que apunten a funciones
     * 
     * @param args el nombre de la variable y su contenido
     * @throws Exception si el nombre es inválido o el contenido es inválido
     */
    public void setq(List<?> args) throws Exception {
        if (args.get(0).getClass() != String.class) {
            throw new Exception();
        }
        String name = (String) args.get(0);
        if (name.contains("\"")) {
            throw new Exception();
        }
        if (args.get(1).getClass() == String.class) {
            String asignment = (String) args.get(1);
            if (globalEnv.get(asignment) != null) {
                if (globalEnv.get(asignment).getClass() == LispFunction.class) {
                    throw new Exception(); 
                } else if (!asignment.contains("\"")) {
                    setq(List.of(name, globalEnv.get(asignment)));
                    return;
            } 
            } else if (!asignment.contains("\"")) {
                throw new Exception();
            }
        } 

        globalEnv.put(name, getArg(args.get(1)));

        
        return;
    }

    /**
     * Función que añade otras funciones personalizadas utilizando las básicas.
     * Permite la utilización de recursividad en su ejecución.
     * En el eval se arroja error si no hay exactamente 3 argumentos.
     * @param args el nombre, parámetros y cuerpo de la función
     * @throws Exception si los tipos de cada parte no son correctos
     */
    public void defun(List<?> args) throws Exception {
        String name = (String) args.get(0);
        if (name.contains("\"")) {
            throw new Exception();
        }
        List<?> params = (List<?>) args.get(1);
        List<?> body = (List<?>) args.get(2);
        globalEnv.put(name, new LispFunction(name, params, body));
        return;
    }
    
}
