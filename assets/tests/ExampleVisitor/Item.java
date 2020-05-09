/**
 * Chrissy Soulakian
 * VisitorPattern Project
 * Created on 4/25/2016
 *
 * Element Interface
 */
public interface Item {
    public double accept(ShoppingCartVisitor visitor);
}
