package dao;

import exception.DAOException;
import model.Client;

public interface ClientDAO {

    Long createClient(Client client) throws DAOException;

    Client getClientById(Long id) throws DAOException;

}
