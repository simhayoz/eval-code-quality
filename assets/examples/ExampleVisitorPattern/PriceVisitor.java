public class PriceVisitor implements Visitor {
    @Override
    public void visit(Book book) {
        System.out.println("Price: " + book.getPrice());
    }

    @Override
    public void visit(Fruit fruit) {
        System.out.println("Price: " + fruit.getPricePerKg() * fruit.getWeight());
    }
}
