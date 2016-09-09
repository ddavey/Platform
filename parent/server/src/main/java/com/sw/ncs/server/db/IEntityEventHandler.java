package com.sw.ncs.server.db;

public interface IEntityEventHandler {
	public void beforeCreate(IEntity entity);
	
	public void afterCreate(IEntity entity);
}
