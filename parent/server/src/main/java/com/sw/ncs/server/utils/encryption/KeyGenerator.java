package com.sw.ncs.server.utils.encryption;

import java.util.UUID;

public class KeyGenerator {
	public static synchronized String generateKey(){

			return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);

	}
	

}
