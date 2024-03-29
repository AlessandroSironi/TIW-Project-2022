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

		try (PreparedStatement pstatement1 = connection.prepareStatement(query1);) {
			pstatement1.setString(1, usrn);
			try (ResultSet result1 = pstatement1.executeQuery();) {
				if (!result1.isBeforeFirst()) {
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
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
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
		
		 try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			 pstatement.setInt(1, iduser);
             try (ResultSet resultSet = pstatement.executeQuery()) {
                 while (resultSet.next()) {
                     User retrievedUser = new User();
                     retrievedUser.setID(resultSet.getInt("ID"));
                     retrievedUser.setUsername(resultSet.getString("user"));
                     retrievedUser.setMail(resultSet.getString("mail"));
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
	
	public boolean checkUserIDExists(int ID) throws SQLException {
		String query = "SELECT * FROM User WHERE ID = ?";
		 try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			 pstatement.setInt(1, ID);
			 try (ResultSet resultSet = pstatement.executeQuery()) {
				 if (resultSet.next()) return true; //User already exists.
			 } catch (SQLException e) {
				 e.printStackTrace();
			 }
		 }
		return false;
	}
	
	/*public String getUsernameByID(int ID) throws SQLException {
		String query = "SELECT user FROM User WHERE ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			 pstatement.setInt(1, ID);
			 try (ResultSet resultSet = pstatement.executeQuery()) {
				 if (resultSet.next()) {
					 String username = resultSet.getString("username");
					 return username;
				 }
			 } catch (SQLException e) {
				 e.printStackTrace();
			 }
		 }
		return null;
	} */
}
