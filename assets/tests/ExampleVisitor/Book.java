/**
 * Chrissy Soulakian
 * VisitorPattern Project
 * Created on 4/25/2016
 *
 * Book is one of the two Concrete Elements.
 */
public class Book implements Item {

    private String title;
    private double price;

    public Book(String title, double price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public double accept(ShoppingCartVisitor visitor) {
        return visitor.visit(this);
    }
}