package com.sw.ncs.server.db.expressions;

public class EqualsOperator extends Operator{

	public EqualsOperator(Column leftSide, Object value) {
		super(leftSide, value);
	}

	@Override
	OperatorType getOperatorType() {
		// TODO Auto-generated method stub
		return OperatorType.EQUALS;
	}

}
