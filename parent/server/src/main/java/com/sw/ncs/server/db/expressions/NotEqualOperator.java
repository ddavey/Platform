package com.sw.ncs.server.db.expressions;

public class NotEqualOperator extends Operator {

	

	public NotEqualOperator(Column leftSide, Object value) {
		super(leftSide, value);
		// TODO Auto-generated constructor stub
	}

	@Override
	OperatorType getOperatorType() {
		// TODO Auto-generated method stub
		return OperatorType.NOT_EQUALS;
	}

}
