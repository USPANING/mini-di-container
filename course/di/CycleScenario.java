package course.di;

public class CycleScenario {
    public static class A {
        private final B b;
        public A(B b) { this.b = b;}
        public B smth() { return b;}
    }

    public static class B {
        private final A a;
        public B(A a) { this.a = a;}
        public A smth() { return a;}
    }

    static void main() {
        MiniContainer container = new MiniContainer();
        container.register("a", A.class, MiniContainer.Scope.SINGLETON);
        container.register("b", B.class, MiniContainer.Scope.SINGLETON);
        container.start();
        try {
            container.getBean(A.class);

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

    }
}
