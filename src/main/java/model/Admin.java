package model;

import dao.BillDAO;
import dao.DAOFactory;
import exception.DAOException;

import java.util.Objects;

public class Admin {

    private String name;
    private final BillDAO billDAO = DAOFactory.getINSTANCE().getBillDAO();

    public void approveOrder(Order order) {
        order.setIsApproved(true);
    }

    public Bill getBill(Order order) throws DAOException {
        return billDAO.makeBill(order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Admin admin = (Admin) o;

        if (!Objects.equals(name, admin.name)) return false;
        return Objects.equals(billDAO, admin.billDAO);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (billDAO != null ? billDAO.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "name='" + name + '\'' +
                ", billDAO=" + billDAO +
                '}';
    }
}
