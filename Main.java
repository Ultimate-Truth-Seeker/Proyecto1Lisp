import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        List<Object> l = List.of("+", -0.5, 12.132412412431234, 2, List.of("+", 2, 3), 3.0, 3);

        LispInterpreter lispInterpreter = LispInterpreter.getInterpreter();
        
        System.out.println(lispInterpreter.eval(l));
        
        
    }
}
