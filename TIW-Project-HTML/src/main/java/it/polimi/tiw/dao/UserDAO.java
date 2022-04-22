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
		String query = "SELECT ID, user, mail, name, surname FROM User WHERE user = ? AND psw_hash = ?";
		String query1 = "SELECT psw_salt FROM User WHERE user = ?";
		
		byte[] hash = null;
		
		//SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		//random.nextBytes(salt);
		
		System.out.println("Sono in checkCredentials e non ho eseguito query1.");
		try (PreparedStatement pstatement1 = connection.prepareStatement(query1);) {
			pstatement1.setString(1, usrn);
			try (ResultSet result1 = pstatement1.executeQuery();) {
				System.out.println("Sono in checkCredentials/executeQuery_pstatement1.");
				if (!result1.isBeforeFirst()) {
					System.out.println("User doesn't exists.");
					return null;
				}
				else {
					result1.next();
					salt = result1.getBytes("psw_salt");
				}
			}
		}
		
		try {
			KeySpec spec = new PBEKeySpec(pwd.toCharArray(), salt, 65536, 128);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			hash = factory.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setBytes(2, hash);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setID(result.getInt("ID"));
					user.setUsername(result.getString("user"));
					user.setMail(result.getString("mail"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					
					return user;
				}
			}
		}
	}
	
	public void registerUser (String mail, String user, String password, String name, String surname) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		
		System.out.println("Sono in registerUser.");
		
		//String psw_hashed = null;
		
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		
		byte[] hash = factory.generateSecret(spec).getEncoded();
				
		String query = "INSERT INTO User (mail, user, psw_hash, psw_salt, name, surname) VALUES (?, ?, ?, ?, ?, ?)";
		
		 try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			 pstatement.setString(1, mail);
			 pstatement.setString(2, user);
			 pstatement.setBytes(3, hash);
			 pstatement.setBytes(4, salt);
			 pstatement.setString(5, name);
			 pstatement.setString(6, surname);
             
			 pstatement.executeUpdate();
		 }
	} 
	
	public ArrayList<User> getOtherUsers (int iduser) throws SQLException {
		String query = "SELECT ID, mail, user, name, surname FROM User WHERE ID != ?";
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
		System.out.println("Sono in checkUserExists");
		String query = "SELECT * FROM User WHERE user = ?";
		 try (PreparedStatement pstatement = connection.prepareStatement(query);) {
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
