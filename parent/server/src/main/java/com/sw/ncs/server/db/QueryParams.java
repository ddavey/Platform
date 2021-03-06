package com.sw.ncs.server.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.sw.ncs.server.db.expressions.And;
import com.sw.ncs.server.db.expressions.Group;
import com.sw.ncs.server.db.expressions.Logic;

public class QueryParams {
	public static enum SortOptions {ASC,DESC};
	private int pageNo;
	private int perPage;
	private List<Logic> search;
	private List<Entry<String,SortOptions>> sorting = new ArrayList<Entry<String,SortOptions>>();
	
	public QueryParams(int pageNo,int perPage,List<Entry<String,SortOptions>> sorting,List<Logic> search){
		this.pageNo = pageNo;
		this.perPage = perPage;
		this.sorting = sorting;
		this.search = search;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPerPage() {
		return perPage;
	}

	public List<Entry<String, SortOptions>> getSorting() {
		return sorting;
	}

	public List<Logic> getSearch() {
		return search;
	}
	
	String getHqlSearchQuery(){
		StringBuilder builder = new StringBuilder();
		
		if(search != null && !search.isEmpty()){
			builder.append(" where ");
			List<Logic> groupedLogic = new ArrayList<Logic>();
			groupedLogic.add(new And(new Group(search)));
			boolean first = true;
			String strCondition;
			for(Logic logic : groupedLogic){
				strCondition = " "+logic.getHqlString();
				if(first){
					strCondition = strCondition.replaceFirst("AND","").replaceFirst("OR", "");
					first = false;
				}
				builder.append(strCondition);
			}
		}
		
		return builder.toString();
	}
}
