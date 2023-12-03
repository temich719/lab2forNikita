import exception.DAOException;
import model.Menu;

public class Starter {

    public static void main(String[] args) {
        try {
            Menu menu = new Menu();
            menu.choice();
        } catch (DAOException e) {
            System.out.println("Something went wrong!");
        }
    }

}
