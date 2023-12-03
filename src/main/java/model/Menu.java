package model;

import dao.DAOFactory;
import dao.OrderDAO;
import dao.ProductDAO;
import exception.DAOException;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final List<Product> products;
    private final ProductDAO productDAO = DAOFactory.getINSTANCE().getProductDAO();
    private final Scanner scanner = new Scanner(System.in);

    public Menu() throws DAOException {
        this.products = productDAO.getProducts();
    }

    public void showMenu() {
        System.out.println("Menu:");
        for (int i = 0; i < products.size(); i++) {
            System.out.println((i+1) + ". " + products.get(i).getName());
        }
    }

    public Client startMenu() {
        System.out.println("Enter your name:");
        String name = scanner.nextLine();
        System.out.println("Enter your surname:");
        String surname = scanner.nextLine();
        System.out.println("Enter your money amount:");
        Double money = scanner.nextDouble();
        Client client = new Client();
        client.setName(name);
        client.setSurname(surname);
        client.setMoney(money);
        return client;
    }

    private void sortProduct() throws DAOException {
        List<Product> products = productDAO.getProducts();
        Comparator<Product> productComparator = Comparator.comparingDouble(Product::getPrice);
        products.sort(productComparator);
        System.out.println("Sorted array: " + products);
    }

    private void findProductsByKeyword() throws DAOException {
        System.out.println("Input keyword:");
        scanner.nextLine();
        String keyword = scanner.nextLine();
        System.out.println("Products which match: ");
        System.out.println(productDAO.findProductsByKeyword(keyword));
    }

    private void printOrdersByClientId() throws DAOException {
        System.out.println("Input client id: ");
        Integer id = scanner.nextInt();
        OrderDAO orderDAO = DAOFactory.getINSTANCE().getOrderDAO();
        System.out.println("Orders of client with id = " + id);
        System.out.println(orderDAO.getOrdersByClientId(id));
    }

    private void getAllProducts() throws DAOException {
        System.out.println("All products: ");
        System.out.println(productDAO.getProducts());
    }

    private void getApprovedOrders() throws DAOException {
        System.out.println("Approved orders: ");
        System.out.println(DAOFactory.getINSTANCE().getOrderDAO().getApprovedOrders());
    }

    public void choice() throws DAOException {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("Input number: ");
            System.out.println("1.Product sort by price");
            System.out.println("2.Find product");
            System.out.println("3.Get orders by client id");
            System.out.println("4.Get products");
            System.out.println("5.Get all approved orders");
            System.out.println("6.Business logic");
            System.out.println("7.Exit");
            Executor executor = new Executor();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    sortProduct();
                    break;
                case 2:
                    findProductsByKeyword();
                    break;
                case 3:
                    printOrdersByClientId();
                    break;
                case 4:
                    getAllProducts();
                    break;
                case 5:
                    getApprovedOrders();
                    break;
                case 6:
                    executor.execute();
                    break;
                case 7:
                    isRunning = false;
                    break;
                default:
                    System.out.println("Some issues with menu");
            }
            System.out.println();
        }
    }

}
