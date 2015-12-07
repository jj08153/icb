package com.icb123.web.bean;

import java.util.List;

public class PageBean {

	private List resultList;
	private int count;
	private int totalPage;
	private String currentPage;
	private int size;
	
	public PageBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getTotalPage(int totalRows,int pageSize){
		int totalPage=0;		
		if(totalRows/pageSize<1){
			totalPage=1;
		}else if(totalRows%pageSize==0){
			totalPage=totalRows/pageSize;
		}else if(totalRows%pageSize!=0){
			totalPage=totalRows/pageSize++;
		}
		return totalPage;
	}
}
