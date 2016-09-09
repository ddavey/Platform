package com.sw.ncs.server.db.expressions;

public class Or extends Logic {

	protected Or(ExpressionPart expression) {
		super(expression);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LogicType getType() {
		// TODO Auto-generated method stub
		return LogicType.OR;
	}

}
