package com.searchplace.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.searchplace.entity.SearchInfo;

public interface SearchInfoRepository extends JpaRepository<SearchInfo, Long> {
	public List<SearchInfo> findByUserId(String userId);
	public SearchInfo findBySearchWord(String searchWord);
	public void deleteBySearchWord(String searchWord);	
	public List<SearchInfo> findTop10ByOrderByCountDesc();
}
