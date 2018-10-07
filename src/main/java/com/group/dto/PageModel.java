package com.group.dto;

import java.io.Serializable;
import java.util.List;
/**
 * 分页模型
 * @author dzqc Xu
 * @since 2015-12-09
 */

public class PageModel<E> implements Serializable {
	
	private static final long serialVersionUID = 3265524976080127173L;

	private Integer totalCount; //总记录数
	
	private Integer pageSize = 10; //每页显示的数量
	
	private Integer totalPage; //总页数
	
	private Integer currentPage = 1; //申请页
	
	private List<E> list; //分页集合列表
	
	private String url; //分页跳转的URL
	
	public PageModel() {
	}
	
	public PageModel(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}
