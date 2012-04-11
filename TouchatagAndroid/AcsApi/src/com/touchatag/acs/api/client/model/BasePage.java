package com.touchatag.acs.api.client.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public abstract class BasePage {

	@Attribute
	public int page;

	@Attribute(name="pagesize")
	public int pageSize;

	@Attribute
	public int total;

	public abstract List<?> getItems();

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
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

}
