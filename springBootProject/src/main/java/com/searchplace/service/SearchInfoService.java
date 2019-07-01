package com.searchplace.service;

import java.util.List;
import com.searchplace.entity.SearchInfo;

public interface SearchInfoService {
	public void saveSearchInfo(SearchInfo searchInfo);
	public List<SearchInfo> showSearchInfo(String userId);
	public SearchInfo findSearchInfo(String searchWord);
	public void deleteSearchInfo(String searchWord);
	public List<SearchInfo> showFamousInfo();
}
