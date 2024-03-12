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
            return eval((List<?>) expression);
        }
    }

    // Si ninguna condición se evalúa como verdadera, devuelve null.
    return null;
}


public Object quote(List<?> args) throws Exception {
    // La función quote en Lisp simplemente devuelve su argumento sin evaluarlo.
    // En otras palabras, quote es una forma de especificar datos literales en Lisp.

    if (args.size() != 1) {
        throw new Exception("Expected exactly one argument for quote");
    }

    // Devuelve el argumento tal cual, sin evaluarlo.
    return args.get(0);
}