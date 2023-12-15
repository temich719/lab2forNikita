package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
@Setter
@Getter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;

    @OneToMany(mappedBy = "client")
    private List<Order> orders;
    private Double money;

    public Double payBill(Bill bill) {
        return money - bill.getTotal() >= 0 ? money - bill.getTotal() : -1.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (!Objects.equals(id, client.id)) return false;
        if (!Objects.equals(name, client.name)) return false;
        if (!Objects.equals(surname, client.surname)) return false;
        return Objects.equals(money, client.money);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (money != null ? money.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", money=" + money +
                '}';
    }
}
