package course.di;

public class Ex0122 {

    public static class Memory {
        public Memory() {}
    }

    static void main() {
        MiniContainer container = new MiniContainer();
        MiniContainer container2 = new MiniContainer();
        container2.register("memory", Memory.class, MiniContainer.Scope.SINGLETON);
        container.register("memory", Memory.class, MiniContainer.Scope.SINGLETON);
        container.start();
        container2.start();
        Memory first = container.getBean(Memory.class);
        Memory second = container.getBean(Memory.class);
        Memory third = container2.getBean(Memory.class);
        System.out.println("Same cont.: " + (first == second));
        System.out.println("two containers: " + (first == third));
    }
}
