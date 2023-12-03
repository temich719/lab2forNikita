package model;

import java.util.List;
import java.util.Objects;

public class Bill {

    private List<Order> orders;
    private Double total;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bill bill = (Bill) o;

        if (!Objects.equals(orders, bill.orders)) return false;
        return Objects.equals(total, bill.total);
    }

    @Override
    public int hashCode() {
        int result = orders != null ? orders.hashCode() : 0;
        result = 31 * result + (total != null ? total.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "orders=" + orders +
                ", total=" + total +
                '}';
    }
}
