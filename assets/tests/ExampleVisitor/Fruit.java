/**
 * Chrissy Soulakian
 * VisitorPattern Project
 * Created on 4/25/2016
 *
 * Fruit is one of the two Concrete Elements.
 */
public class Fruit implements Item {

    private String name;
    private double pricePerlb;
    private double weight;

    public Fruit(String name, double pricePerlb, double weight) {
        this.name = name;
        this.pricePerlb = pricePerlb;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getPricePerlb() {
        return pricePerlb;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public double accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
