/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sree.service;

import com.sree.Products;
import com.sree.database.DatabaseConnection;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;


/**
 *
 * @author Sreejith
 */
@Stateless
@Path("products")
public class ProductsFacadeREST extends AbstractFacade<Products> {
    @PersistenceContext(unitName = "Assignment4PU")
    private EntityManager em;

    public ProductsFacadeREST() {
        super(Products.class);
    }

    @POST
    @Consumes("application/json")
    public String doPost(Products entity) {
        super.create(entity);
        int id = getId("select max(product_id) from products");
        String url = "http://localhost:8080/Assignment4/webresources/products/" + id;
        return url;
        
    }
    
    private int getId(String query) {
        int id = 0;
        //String jsonArray = null;      
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                id = rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.out.println("Exception in getting database connection: " + ex.getMessage());
        }
        return id;
    }
    
    
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public void edit(@PathParam("id") String id, Products entity) {
        //super.edit(entity);
        int change = doUpdate("UPDATE PRODUCTS SET name = ?, description = ?, quantity = ? WHERE PRODUCT_ID = ?", entity.getName(), entity.getDescription(), Integer.toString(entity.getQuantity()), id);
    }
    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error in Updating...: " + ex.getMessage());;
        }
        return numChanges;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Products find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Products> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/json"})
    public List<Products> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
