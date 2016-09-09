package com.sw.ncs.server.db.expressions;


public class And extends Logic{


	public And(ExpressionPart expression) {
		super(expression);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LogicType getType() {
		// TODO Auto-generated method stub
		return LogicType.AND;
	}


	

}
