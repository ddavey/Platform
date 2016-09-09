package com.sw.ncs.server.db.expressions;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;

public abstract class Operator extends ExpressionPart{
	protected enum OperatorType {EQUALS,NOT_EQUALS};
	private enum RightSideType {COLUMN,VALUE};
	private RightSideType rightSideType;
	private Object rightSide;
	private String hqlString;
	
	private final Map<OperatorType,String> criteriaHqlMap = new HashMap<OperatorType,String>(){{
		put(OperatorType.EQUALS, "="); put(OperatorType.NOT_EQUALS,"!=");
	}};
	private Column leftSide;
	
	public Operator(Column leftSide,Object value){
		if(value.getClass() == Column.class){
			rightSideType = RightSideType.COLUMN; 
		}else{
			rightSideType = RightSideType.VALUE;
		}
		this.leftSide = leftSide;
		this.rightSide = value; 
		compileHqlString();
	}
	
	
	public Object getRightSide() {
		return rightSide;
	}


	public void setRightSide(Object rightSide) {
		this.rightSide = rightSide;
	}


	public Column getLeftSide() {
		return leftSide;
	}


	public void setLeftSide(Column leftSide) {
		this.leftSide = leftSide;
	}


	abstract OperatorType getOperatorType();
	
	protected String getOperatorHqlString(){
		return criteriaHqlMap.get(getOperatorType());
	}
	
	private void compileHqlString(){
		StringBuilder stringBuilder = new StringBuilder(leftSide.getName());
		stringBuilder.append(" " + getOperatorHqlString() + " ");
		
		if(rightSideType == RightSideType.VALUE){
			stringBuilder.append(":");
		}

		stringBuilder.append(((Column)leftSide).getName());
		hqlString = stringBuilder.toString();
	}
	
	public String getHqlString(){
		return this.hqlString;
	}
	
	void setQueryValue(Query query){
		if(rightSideType == RightSideType.VALUE){
			if(rightSide.getClass() == String.class){
				query.setString(leftSide.getName(), (String)rightSide);
			}
		}
	}
}
