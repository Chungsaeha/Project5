package com.searchplace.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.searchplace.entity.SearchInfo;
import com.searchplace.repository.SearchInfoRepository;

@Service("SearchInfoService")
public class SearchInfoServiceImpl implements SearchInfoService {

	@Autowired
	private SearchInfoRepository repository;
	
	
	@Override
	@Transactional
	public void saveSearchInfo(SearchInfo searchInfo) {
		repository.save(searchInfo);
	}

	@Override
	@Transactional
	public List<SearchInfo> showSearchInfo(String userId) {
		return repository.findByUserId(userId);
	}

	@Override
	@Transactional
	public SearchInfo findSearchInfo(String searchWord) {
		return repository.findBySearchWord(searchWord);
	}

	@Override
	@Transactional
	public void deleteSearchInfo(String searchWord) {
		repository.deleteBySearchWord(searchWord);
	}

	@Override
	public List<SearchInfo> showFamousInfo() {
		return repository.findTop10ByOrderByCountDesc();
	}

}
