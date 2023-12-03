package model;

import java.util.List;
import java.util.Objects;

public class Order {

    private Integer orderNumber;
    private List<Product> products;
    private Boolean isApproved;
    private Client client;

    public Order() {
        this.isApproved = false;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!Objects.equals(orderNumber, order.orderNumber)) return false;
        if (!Objects.equals(products, order.products)) return false;
        if (!Objects.equals(isApproved, order.isApproved)) return false;
        return Objects.equals(client, order.client);
    }

    @Override
    public int hashCode() {
        int result = orderNumber != null ? orderNumber.hashCode() : 0;
        result = 31 * result + (products != null ? products.hashCode() : 0);
        result = 31 * result + (isApproved != null ? isApproved.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber=" + orderNumber +
                ", products=" + products +
                ", isApproved=" + isApproved +
                ", client=" + client +
                '}';
    }
}
