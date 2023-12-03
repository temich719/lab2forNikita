package model;

import dao.ClientDAO;
import dao.DAOFactory;
import dao.OrderDAO;
import dao.ProductDAO;
import exception.DAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Executor {

    private final Logger LOGGER = LogManager.getLogger(Executor.class);

    private final ProductDAO productDAO;

    public Executor() {
        this.productDAO = DAOFactory.getINSTANCE().getProductDAO();
    }

    public void execute() {
        try {
            LOGGER.info("Start");
            ClientDAO clientDAO = DAOFactory.getINSTANCE().getClientDAO();
            OrderDAO orderDAO = DAOFactory.getINSTANCE().getOrderDAO();
            Scanner scanner = new Scanner(System.in);
            Menu menu = new Menu();
            Admin admin = new Admin();
            Client client = menu.startMenu();
            Integer clientId = clientDAO.createClient(client);
            client.setId(clientId);
            menu.showMenu();
            System.out.println("Input numbers separated with comma: ");
            String input = scanner.nextLine();
            List<Integer> numbers = parseInput(input);
            List<Product> productToOrder = new ArrayList<>();
            for (Integer i : numbers) {
                Product product = productDAO.getProductById(i);
                product.setId(i);
                productToOrder.add(product);
            }
            Order order = new Order();
            order.setProducts(productToOrder);
            order.setClient(client);
            admin.approveOrder(order);
            Integer orderId = orderDAO.makeOrder(order);
            order.setOrderNumber(orderId);
            Bill bill = admin.getBill(order);
            Double remainMoney = client.payBill(bill);
            if (remainMoney == -1.0) {
                System.out.println("You haven't enough money for pay this bill!");
            } else {
                System.out.println("Bill was successfully closed. You have " + remainMoney + "$");
            }
            LOGGER.info("END");
        } catch (DAOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private List<Integer> parseInput(String input) {
        List<Integer> numbers = new ArrayList<>();

        String[] tokens = input.split(",");
        for (String token : tokens) {
            try {
                int number = Integer.parseInt(token.trim());
                numbers.add(number);
            } catch (NumberFormatException e) {
                System.out.println("Error during converting number: " + token);
            }
        }

        return numbers;
    }

}
