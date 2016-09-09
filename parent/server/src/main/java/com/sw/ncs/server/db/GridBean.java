package com.sw.ncs.server.db;

import java.util.List;

import com.sw.ncs.server.db.expressions.Logic;

public class GridBean {
	private List<IEntity> data;
	private List<Logic> searchParams;
	private int perPage;
	private int page;
	private long resultCount;
	
	public GridBean(int page,int perPage,List<Logic> searchParams,List<IEntity> data,long resultCount){
		this.page = page;
		this.perPage = perPage;
		this.searchParams = searchParams;
		this.data = data;
		this.resultCount = resultCount;
	}

	public List<IEntity> getData() {
		return data;
	}

	public List<Logic> getSearchParams() {
		return searchParams;
	}

	public int getPerPage() {
		return perPage;
	}

	public int getPage() {
		return page;
	}
	
	
	
}
