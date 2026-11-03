package course.di;

public class Ex0322 {
    public static class Ticket {}
    public static class Holder {
        private final Ticket ticket;
        public Holder(Ticket ticket) { this.ticket = ticket;}
        public Ticket getTicket() {return ticket;}
    }

    static void main() {
        MiniContainer container = new MiniContainer();
        container.register("ticket", Ticket.class, MiniContainer.Scope.PROTOTYPE);
        container.register("holder", Holder.class, MiniContainer.Scope.SINGLETON);
        container.start();

        Ticket first = container.getBean(Ticket.class);
        Ticket second = container.getBean(Ticket.class);
        System.out.println("two tickets: " + (first == second));

        Holder third = container.getBean(Holder.class);
        Holder fourth = container.getBean(Holder.class);
        System.out.println("same holder: " + (third == fourth));

        System.out.println("ticket inside holder: " + (third.getTicket() == fourth.getTicket()));
    }
}
