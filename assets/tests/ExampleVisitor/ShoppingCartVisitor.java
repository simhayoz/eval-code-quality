/**
 * Chrissy Soulakian
 * VisitorPattern Project
 * Created on 4/25/2016
 *
 * Visitor Interface
 */
public interface ShoppingCartVisitor {
    double visit(Book book);
    double visit(Fruit fruit);
}
