package com.searchplace.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SearchInfo {

	@Id
	@GeneratedValue
	private Long id;

	private String userId;
	private String searchWord;
	private String searchDate;
	private String searchTime;
	private Long timeStamp;
	private int count;

	public SearchInfo() {
		
	}
	
	public SearchInfo(String userId, String searchWord, String searchDate, String searchTime, Long timeStamp, int count) {
		this.userId = userId;
		this.searchWord = searchWord;
		this.searchDate = searchDate;
		this.searchTime = searchTime;
		this.timeStamp = timeStamp;
		this.count = count;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}

	public String getSearchTime() {
		return searchTime;
	}

	public void setSearchTime(String searchTime) {
		this.searchTime = searchTime;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
