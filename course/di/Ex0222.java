package course.di;

public class Ex0222 {
    public interface Repo {
    }

    public static class Memory implements Repo {
    }

    public static class FileRepo implements Repo {
    }

    static void main() {
        MiniContainer container = new MiniContainer();
        container.register("memory", Memory.class, MiniContainer.Scope.SINGLETON);
        container.register("file", FileRepo.class, MiniContainer.Scope.SINGLETON);
        container.start();

        try {
            container.getBean(Repo.class);

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        Repo bean = container.getBean(Repo.class, "memory");
        System.out.println(bean.getClass().getSimpleName());

        try {
            container.getBean(Repo.class, "Kuza");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
