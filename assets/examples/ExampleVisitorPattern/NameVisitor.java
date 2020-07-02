public class NameVisitor implements Visitor {
    public void visit(Book book) {
        System.out.println("Book: " + book.getTitle());
    }

    public void visit(Fruit fruit) {
        System.out.println("Fruit: " + fruit.getName());
    }
}