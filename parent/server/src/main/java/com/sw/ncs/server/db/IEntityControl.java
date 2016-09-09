package com.sw.ncs.server.db;

public interface IEntityControl {
	
	public IEntity get(long id,DbSession session);
	
	public GridBean list(QueryParams params,DbSession session);
	
	public boolean validate(IEntity entity)throws EntityValidationException;
	
	public IEntity save(IEntity entity,DbSession session)throws EntityValidationException;
	
	
	
}
