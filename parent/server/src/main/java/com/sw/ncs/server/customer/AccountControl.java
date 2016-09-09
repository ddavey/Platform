package com.sw.ncs.server.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.sw.ncs.server.db.AbstractDatabaseEventHandler;
import com.sw.ncs.server.db.AbstractEntityControl;
import com.sw.ncs.server.db.Database;
import com.sw.ncs.server.db.DbSession;
import com.sw.ncs.server.db.EntityValidationException;
import com.sw.ncs.server.db.GridBean;
import com.sw.ncs.server.db.IEntity;
import com.sw.ncs.server.db.QueryParams;
import com.sw.ncs.server.db.expressions.And;
import com.sw.ncs.server.db.expressions.Column;
import com.sw.ncs.server.db.expressions.EqualsOperator;
import com.sw.ncs.server.db.expressions.Logic;
import com.sw.ncs.server.utils.encryption.Encryption;

public class AccountControl extends AbstractEntityControl{

	@Override
	protected void beforeCreate(IEntity entity) {

		Account account = (Account) entity;
		if(account.getId()==0){
			account.setPassword(Encryption.getInstance(customerNo).Encrypt(account.getPassword()));
		}
	}


	private static Map<Long,AccountControl> instances = new HashMap<Long,AccountControl>();
	private SessionControl sessionControl;
	
	public static AccountControl getInstance(long customerNo){
		if(instances.isEmpty()){
		}
		
		if(instances.get(customerNo) == null){
			instances.put(customerNo, new AccountControl(customerNo) );
		}
		return instances.get(customerNo);
	}

	private AccountControl(long customerNo){
		this.customerNo = customerNo;
		sessionControl = SessionControl.getInstance(customerNo);
		
	}
	

	

	@Override
	public boolean validate(IEntity entity) throws EntityValidationException {

		Account account = (Account) entity;
		Map<String,String> errors = new HashMap<String,String>();
		List<String> valid = new ArrayList<String>();
		
		if(account.getFirstName().length() < 3){
			errors.put("accountFirstName", "Must be minimum 3 Characters.");
		}else{
			valid.add("accountFirstName");
		}
		
		if(account.getLastName().length() < 3){
			errors.put("accountLastName", "Must be minimum 3 Characters.");
		}else{
			valid.add("accountLastName");
		}
		
		if(account.getEmail().length() < 3 || !validateEmail(account.getEmail())){
			errors.put("accountEmail", "Invalid email address.");
		}else{
			valid.add("accountEmail");
		}
		
		if(account.getUsername().length() < 3){
			errors.put("accountUsername", "Must be minimum 3 Characters.");
		}else{
			valid.add("accountUsername");
		}
		
		if(!validatePassword(account.getPassword())){
			errors.put("accountPassword", "Must have atleast 1 uppercase,1 lowercase and 1 number character with a length of atleast 8.");
		}else{
			valid.add("accountPassword");
		}
		
		
		if(!errors.isEmpty()){
			throw new EntityValidationException(errors,valid);
		}
		
		return true;
	}


	public boolean login(String username,String password,HttpSession httpSession,DbSession dbSession) throws AuthenticationException{
		
		boolean dbSessionNull = dbSession == null;
		
		if(dbSessionNull){
			dbSession = Database.getInstance().getSession(customerNo);
		}
		
		password = Encryption.getInstance(customerNo).Encrypt(password);
		Logic usernameLogic = new And(new EqualsOperator(new Column("username"), username));
		Logic pwLogic = new And(new EqualsOperator(new Column("password"),password));
		List<Logic> search = new ArrayList<Logic>();
		search.add(usernameLogic);
		search.add(pwLogic);
		QueryParams params = new QueryParams(1,-1,null,search);
		
		GridBean gridBean = list(params, null);
		
		if(gridBean.getData().size()!=1){
			throw new AuthenticationException(customerNo, username);
		}else {
			dbSession.beginTransaction();
			boolean sessionAdded = sessionControl.addSession(httpSession.getId(),dbSession);
			
			if(dbSessionNull){
				if(sessionAdded){
					dbSession.commitTransaction();
				}else{
					dbSession.getTransaction().rollback();
				}
			}
			
			
			return sessionAdded;
			}
	}

	@Override
	public GridBean list(QueryParams params, DbSession session) {

		boolean sessionNull = session == null;
		
		if(sessionNull){
			session = Database.getInstance().getSession(customerNo);
		}
		
		if(!sessionNull){
			return list(params,"Account",session);
		}else{
			GridBean gridBean = list(params,"Account",session);
			session.close();
			return gridBean;
		}
	}

	@Override
	public IEntity get(long id, DbSession session) {
		// TODO Auto-generated method stub
		return null;
	}


}






