package com.sw.ncs.server.utils.encryption;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.sw.ncs.server.customer.Customer;
import com.sw.ncs.server.customer.CustomerControl;
import com.sw.ncs.server.db.AbstractDatabaseEventHandler;
import com.sw.ncs.server.db.IEntity;
import com.sw.ncs.server.synchronization.AbstractSynchronizationHandler;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encryption{
	
	
	

	private static final BASE64Encoder encoder = new BASE64Encoder();
	private static final BASE64Decoder decoder = new BASE64Decoder();
	private String serialNumber;
	private static Map<Long,Encryption> instances = new HashMap<Long,Encryption>();
	
	
	public static Encryption getInstance(long customerNo){
		
		if(!instances.containsKey(customerNo)){
			Customer customer = CustomerControl.getInstance().get(customerNo, null);
			if(customer!=null){
				instances.put(customerNo, new Encryption(customer));
			}
			
		}
		
		return instances.get(customerNo);
	}

	static void addInstance(Customer customer){
		instances.put(customer.getId(), new Encryption(customer));
	}
	


	Encryption(Customer customer){
		this.serialNumber = customer.getSerialNo();
	}
	
	public String Encrypt(String value){
		
		SecretKeySpec keySpec = new SecretKeySpec(serialNumber.getBytes(), "AES");
		Cipher cipher = null;
		String encryptedValue = null;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		        byte[] encVal = cipher.doFinal(value.getBytes());
		        serialNumber = encoder.encode(serialNumber.getBytes());
		       
		        encryptedValue = serialNumber.substring(0,22) + encoder.encode(encVal);
		      
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
       
        return encryptedValue;
				
		
	}
	
public String Decrypt(String value) throws Exception{
	String decryptedValue = null;
	try {
	serialNumber = new String(decoder.decodeBuffer(serialNumber));
	
	SecretKeySpec keySpec = new SecretKeySpec(serialNumber.getBytes(), "AES");
     Cipher cipher = null;
    
	
		cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		
		value = value.substring(22);
		
	     byte[] decordedValue = decoder.decodeBuffer(value);
	    
	     byte[] decValue = cipher.doFinal(decordedValue);
	     decryptedValue = new String(decValue);
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		throw new Exception("Unable to decrypt data ");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
		// TODO Auto-generated catch block
		throw new Exception("Unable to decrypt data ");
	} catch (BadPaddingException e) {
		// TODO Auto-generated catch block
		throw new Exception("Unable to decrypt data ");
	}
     
     return decryptedValue;
	
	}

	

	}


