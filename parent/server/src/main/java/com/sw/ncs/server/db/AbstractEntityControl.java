package com.sw.ncs.server.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.Query;

import com.sw.ncs.server.db.expressions.Column;
import com.sw.ncs.server.db.expressions.ExpressionPart;
import com.sw.ncs.server.db.expressions.Group;
import com.sw.ncs.server.db.expressions.Logic;
import com.sw.ncs.server.db.expressions.Operator;
import com.sw.ncs.server.utils.encryption.Encryption;

public abstract class AbstractEntityControl implements IEntityControl{
	protected long customerNo;
	private static List<IEntityEventHandler> beforeCreateEventHandlers = new ArrayList<IEntityEventHandler>();
	
	public IEntity save(IEntity entity,DbSession session)throws EntityValidationException{
		try {
			validate(entity);
			
			
		
			SuperEntity superEntity = (SuperEntity) entity;
			
			
				beforeCreate(superEntity);
			
			session.save(superEntity);
		} catch (EntityValidationException eve) {
			throw eve;
		}
		return entity;
	}
	
	private void executeBeforeCreateEventHandlers(IEntity entity) {
		for(IEntityEventHandler handler : beforeCreateEventHandlers){
			handler.beforeCreate(entity);
		}
	}

	public boolean validateEmail(String email){
		String patternText = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		
		return Pattern.compile(patternText).matcher(email).matches();
	}
	
	public boolean validatePassword(String password){
		String patternText = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
		
		boolean match = Pattern.compile(patternText).matcher(password).matches();
		
		return match;
	}
	
	public GridBean list(QueryParams params,String entity,DbSession session){
		Query query = generateQuery(params, entity, session);
		
		Query rowCountQuery = session.createQuery("select count(*) from "+entity+" "+params.getHqlSearchQuery());
		applyQueryParamValues(params,rowCountQuery);
		
		long totalRows = (Long)rowCountQuery.uniqueResult();
		
		
		if(params.getPerPage()>0){
			query.setFetchSize(params.getPerPage());
			
			if(params.getPageNo() > 0){
				query.setFirstResult((params.getPageNo() * params.getPerPage())-params.getPerPage());
			}
		}
		
		List<IEntity> data = query.list();
		
		return new GridBean(params.getPerPage(), params.getPageNo(), params.getSearch(), data,totalRows);
	}
	
	Query generateQuery(QueryParams params,String entity,DbSession session){
		if(session == null){
			session = Database.getInstance().getSession(customerNo);
		}
		String hqlQuery = "from "+entity+" "+params.getHqlSearchQuery();
		Query query = session.createQuery(hqlQuery);
		
		applyQueryParamValues(params,query);
		
		return query;
	}
	
	private void applyQueryParamValues(QueryParams params,Query query){
		List<Logic> search = params.getSearch();
		if(search!=null && search.size()>0){
			exploreLogic(params.getSearch(),query,null);
		}
		
	}
	
	private void exploreLogic(List<Logic> logicList,Query query,List<String> paramsSet){
		if(paramsSet == null){
			paramsSet = new ArrayList<String>();
		}
		
		ExpressionPart expression;
		Operator operator;
		for(Logic logic : logicList){
			expression = logic.getExpression();
			if(expression.getClass() == Group.class){
				exploreLogic(((Group)expression).getExpressions(),query,paramsSet);
			}else{
				operator = (Operator) expression;
				
				if(operator.getRightSide().getClass() != Column.class && !paramsSet.contains(operator.getLeftSide().getName())){
					
					if(operator.getRightSide().getClass() == String.class){
						query.setString(operator.getLeftSide().getName(), (String)operator.getRightSide());
					}
				}
			}
		}
	}
	
	protected void beforeCreate(IEntity entity){
		
	}
}
