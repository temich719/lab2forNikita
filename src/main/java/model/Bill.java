package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "bills")
@Getter
@Setter
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "bills_orders",
            joinColumns = @JoinColumn(name = "billId"),
            inverseJoinColumns = @JoinColumn(name = "orderId")
    )
    private List<Order> orders;
    @Transient
    private Double total;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bill bill = (Bill) o;

        if (!Objects.equals(id, bill.id)) return false;
        if (!Objects.equals(orders, bill.orders)) return false;
        return Objects.equals(total, bill.total);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (total != null ? total.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", total=" + total +
                '}';
    }
}
