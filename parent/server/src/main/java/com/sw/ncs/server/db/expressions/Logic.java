package com.sw.ncs.server.db.expressions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Logic implements Criteria{
	enum LogicType {AND,OR};
	private LogicType type;
	private String hqlString;
	private ExpressionPart expression;
	private Map<LogicType,String> logicTypeMap = new HashMap<LogicType,String>(){{
		put(LogicType.AND, "AND");put(LogicType.OR, "OR");
	}};
	
	protected Logic(ExpressionPart expression){
		this.expression = expression;
		compileHqlString();
	}
	
	
	private String getTypeHqlString(){
		return logicTypeMap.get(type);
	}
	
	public String getHqlString(){
		return hqlString;
	}
	
	public abstract LogicType getType();
	
	private void compileHqlString(){
		StringBuilder builder = new StringBuilder(logicTypeMap.get(getType()));
		builder.append(" " + expression.getHqlString());
		hqlString = builder.toString();
	}
	
	public ExpressionPart getExpression(){
		return expression;
	}
}
