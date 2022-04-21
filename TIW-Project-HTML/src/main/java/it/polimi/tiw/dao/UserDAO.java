package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import it.polimi.tiw.beans.User;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class UserDAO {
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  ID, user FROM User  WHERE user = ? AND psw =?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setID(result.getInt("ID"));
					user.setUsername(result.getString("user"));
					return user;
				}
			}
		}
	}
	
	public void registerUser (String mail, String user, String password, String name, String surname) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		String saltString = null;
		random.nextBytes(salt);
		saltString = salt.toString();
		
		String psw_hashed = null;
		
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		
		byte[] hash = factory.generateSecret(spec).getEncoded();
		psw_hashed = hash.toString();
				
		String query = "INSERT INTO User (mail, user, psw_hash, psw_salt, name, surname) VALUES (?, ?, ?, ?, ?, ?)";
		
		 try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			 pstatement.setString(1, mail);
			 pstatement.setString(2, user);
			 pstatement.setString(3, psw_hashed);
			 pstatement.setString(4, saltString);
			 pstatement.setString(5, name);
			 pstatement.setString(6, surname);
             
			 pstatement.executeUpdate();
		 }
	} 
	
	public ArrayList<User> getOtherUsers (int iduser) throws SQLException {
		String query = "SELECT ID, mail, user, name, surname FROM User WHERE ID != iduser";
		ArrayList<User> users = new ArrayList<>();
		
		 try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			 pstatement.setInt(1, iduser);
             try (ResultSet resultSet = pstatement.executeQuery()) {
                 while (resultSet.next()) {
                     User retrievedUser = new User();
                     retrievedUser.setID(resultSet.getInt("iduser"));
                     retrievedUser.setMail(resultSet.getString("email"));
                     retrievedUser.setName(resultSet.getString("name"));
                     retrievedUser.setSurname(resultSet.getString("surname"));
                     users.add(retrievedUser);
                 }
             }
             return users;
         }
     }
	
	public boolean checkUserExists(String username) throws SQLException {
		String query = "SELECT * FROM User WHERE user = ?";
		 try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			 pstatement.setString(1, username);
			 try (ResultSet resultSet = pstatement.executeQuery()) {
				 if (resultSet.next()) return true; //User already exists.
			 } catch (SQLException e) {
				 e.printStackTrace();
			 }
		 }
		return false;
	}
}
