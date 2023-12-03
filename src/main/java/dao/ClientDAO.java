package dao;

import exception.DAOException;
import model.Client;

public interface ClientDAO {

    Integer createClient(Client client) throws DAOException;

    Client getClientById(Integer id) throws DAOException;

}
