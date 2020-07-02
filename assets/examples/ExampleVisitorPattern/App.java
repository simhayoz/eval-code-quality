import java.util.List;

public class App {
    public static void main(String[] args) {
        List<Item> shoppingList = List.of(new Book("To Kill a Mockinbird", 14.95),
                new Book("Of Mice and Men", 7.89),
                new Fruit("peach", 3.95, 2),
                new Book("The Old Man and The Sea", 9.99),
                new Fruit("banana", 3.30, 0.5));
        NameVisitor nameVisitor = new NameVisitor();
        PriceVisitor priceVisitor = new PriceVisitor();
        for(Item item : shoppingList) {
            item.accept(nameVisitor);
            item.accept(priceVisitor);
        }
    }
}