package com.sw.ncs.server.db.expressions;

import java.util.Arrays;
import java.util.List;

public class Group extends ExpressionPart{
	private String hqlString;
	private List<Logic> expressions;
	
	public Group(List<Logic> expressions){
		this.expressions = expressions;
		compileHqlString();
	}
	
	public Group(Logic... expressions){
		this.expressions = Arrays.asList(expressions);
		compileHqlString();
	}
	
	private void compileHqlString(){
		StringBuilder stringBuilder = new StringBuilder("( ");
		boolean first = true;
		String expressionString;
		for(Logic expression : expressions){
			expressionString = expression.getHqlString();
			if(first){
				stringBuilder.append(expressionString.substring(expressionString.indexOf(" ")));
				first = false;
			}else{
				stringBuilder.append(" " + expressionString);
			}
			
			
		}
		stringBuilder.append(" )");
		hqlString = stringBuilder.toString();
	}

	public String getHqlString() {
		return hqlString;
	}
	
	public List<Logic> getExpressions(){
		return expressions;
	}

}
