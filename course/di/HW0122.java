package course.di;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class HW0122 {
    public interface Repo {
    }

    public static class Memory implements Repo {
    }

    public static class Service {
        private final Repo repo;

        public Service(Repo repo) {
            this.repo = repo;
        }
    }

    public static class Controller {
        private final Service service;

        public Controller(Service service) {
            this.service = service;
        }
    }

    public static class Super {
        private final String name;

        public Super(String name) {
            this.name = name;
        }

        public Super() {
            this.name = "BluBla";
        }

    }

    public static class Mega {
        private final String name;


        public Mega(String name) {
            this.name = name;
        }

        @MiniContainer.MiniInject
        public Mega() {
            this.name = "BluBla";
        }

    }
    public static class FileRepo implements Repo {
        private final Repo mem;
        public FileRepo(Repo mem) {
            this.mem = mem;
        }
    }

    static void main() {
        MiniContainer container = new MiniContainer();
        MiniContainer container1 = new MiniContainer();
        MiniContainer container2 = new MiniContainer();
        MiniContainer container3 = new MiniContainer();
        MiniContainer container4 = new MiniContainer();
        container.register("controller", Controller.class, MiniContainer.Scope.PROTOTYPE);
        container.register("service", Service.class, MiniContainer.Scope.PROTOTYPE);
        container.register("memory", Memory.class, MiniContainer.Scope.PROTOTYPE);
        container.start();
        System.out.println(container.getBean(Controller.class).getClass().getSimpleName());
        try {
            container1.register("", Service.class, MiniContainer.Scope.PROTOTYPE);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        try {
            container2.register("memory", Memory.class, MiniContainer.Scope.PROTOTYPE);
            container2.register("memory", Memory.class, MiniContainer.Scope.PROTOTYPE);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        container1.start();
        container2.start();

        try {
            container3.register("repo", Repo.class, MiniContainer.Scope.PROTOTYPE);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        container4.register("super", Super.class, MiniContainer.Scope.PROTOTYPE);
        container4.start();

        try {
            container4.getBean(Super.class);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        MiniContainer container5 = new MiniContainer();
        container5.register("mega", Mega.class, MiniContainer.Scope.PROTOTYPE);
        container5.start();

        try {
            System.out.println(container5.getBean(Mega.class));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println(container5.getBean(Controller.class));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        MiniContainer container6 = new MiniContainer();
        container6.register("memory", Memory.class, MiniContainer.Scope.PROTOTYPE);
        container6.register("fileRepo", FileRepo.class, MiniContainer.Scope.PROTOTYPE);
        container6.start();

        try {
            System.out.println(container6.getBean(Repo.class));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        MiniContainer containerLast = new MiniContainer();
        Memory newObject = new Memory();

        containerLast.registerInstance("last", newObject);
        containerLast.start();
        System.out.println(containerLast.getBean(Memory.class) == newObject);


//        System.out.println(container.getBean(Service.class).getClass().getSimpleName());
//        System.out.println(container.getBean(Memory.class).getClass().getSimpleName());

    }

}
