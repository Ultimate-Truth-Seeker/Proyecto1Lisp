public Object substract(List<?> args) throws Exception {
    if (args.isEmpty()) {
        throw new Exception("No arguments for subtraction");
    }

    double result = (Double) args.get(0);

    for (int i = 1; i < args.size(); i++) {
        result -= (Double) args.get(i);
    }

    return result;
}

public Object product(List<?> args) throws Exception {
    if (args.isEmpty()) {
        throw new Exception("No arguments for multiplication");
    }

    double result = 1.0;

    for (Object arg : args) {
        result *= (Double) arg;
    }

    return result;
}

public Object divide(List<?> args) throws Exception {
    if (args.size() != 2) {
        throw new Exception("Expected exactly two arguments for division");
    }

    double numerator = (Double) args.get(0);
    double denominator = (Double) args.get(1);

    if (denominator == 0) {
        throw new Exception("Cannot divide by zero");
    }

    return numerator / denominator;
}

public boolean moreThan(List<?> args) throws Exception {
    if (args.size() != 2) {
        throw new Exception("Expected exactly two arguments for comparison");
    }

    double first = (Double) args.get(0);
    double second = (Double) args.get(1);

    return first > second;
}

public boolean lessThan(List<?> args) throws Exception {
    if (args.size() != 2) {
        throw new Exception("Expected exactly two arguments for comparison");
    }

    double first = (Double) args.get(0);
    double second = (Double) args.get(1);

    return first < second;
}

public boolean equal(List<?> args) throws Exception {
    if (args.size() != 2) {
        throw new Exception("Expected exactly two arguments for comparison");
    }

    double first = (Double) args.get(0);
    double second = (Double) args.get(1);

    return first == second;
}