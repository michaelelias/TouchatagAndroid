package com.touchatag.android.client.rest.model;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

	private int pageNumber;
	private int pageSize;
	private int total;
	private List<T> items = new ArrayList<T>();

	public Page(){}
	
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

}
