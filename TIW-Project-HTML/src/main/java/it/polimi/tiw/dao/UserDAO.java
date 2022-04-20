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
	
	public User checkCredentials (String usr, String pwd) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
		String query = "SELECT ID, mail, user, name, surname FROM User WHERE user = ? AND psw_hash = ?";
		String query_salt = "SELECT salt FROM User WHERE user = ?";
		
		byte[] salt = null;
		String saltString = null;
		String pwd_hash = null;
		
		try (PreparedStatement pstatement1 = connection.prepareStatement(query_salt);) {
			pstatement1.setString(1, usr);
			try (ResultSet result1 = pstatement1.executeQuery();) {
				if (!result1.isBeforeFirst()) return null; //No results, user does not exists
				else {
					result1.next();
					saltString = result1.getString("salt");
					salt = saltString.getBytes();
					
					try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						pstatement.setString(1, usr);
						
						KeySpec spec = new PBEKeySpec(pwd.toCharArray(), salt, 65536, 128);
						//The third parameter (65536) is effectively the strength parameter.
						//It indicates how many iterations that this algorithm run for, increasing the time it takes to produce the hash.
						//SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
						SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
						//Password-based-Key-Derivative-Function
						//512 bits, 128 bytes.
						
						byte[] hash = factory.generateSecret(spec).getEncoded();
						pwd_hash = hash.toString();
						
						pstatement.setString(1, usr);
						pstatement.setString(2, pwd_hash);
						
						try (ResultSet result = pstatement.executeQuery();) {
							if (!result.isBeforeFirst()) return null; //No results, wrong login data
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
