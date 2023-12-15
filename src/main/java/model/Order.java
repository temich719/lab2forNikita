package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderNumber;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "orders_products",
            joinColumns = @JoinColumn(name = "orderId"),
            inverseJoinColumns = @JoinColumn(name = "productId")
    )
    private List<Product> products;
    private Boolean isApproved;

    @ManyToMany(mappedBy = "orders")
    private List<Bill> bills = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "clientId")
    private Client client;

    public Order() {
        this.isApproved = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!Objects.equals(orderNumber, order.orderNumber)) return false;
        return Objects.equals(isApproved, order.isApproved);
    }

    @Override
    public int hashCode() {
        int result = orderNumber != null ? orderNumber.hashCode() : 0;
        result = 31 * result + (isApproved != null ? isApproved.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber=" + orderNumber +
                ", isApproved=" + isApproved +
                '}';
    }
}
