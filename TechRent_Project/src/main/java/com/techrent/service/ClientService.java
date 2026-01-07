package com.techrent.service;

import com.techrent.dao.ClientDAO;
import com.techrent.model.Client;
import java.util.List;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAO();

    public List<Client> findAll() {
        return clientDAO.findAll();
    }

    public void save(Client client) {
        clientDAO.save(client);
    }

    public void update(Client client) {
        clientDAO.update(client);
    }

    public void delete(Client client) {
        clientDAO.delete(client);
    }
}