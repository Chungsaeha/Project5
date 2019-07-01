package com.searchplace.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.searchplace.SHA256Util;
import com.searchplace.entity.SearchInfo;
import com.searchplace.entity.User;
import com.searchplace.service.SearchInfoService;
import com.searchplace.service.UserService;

@RestController
public class SearchPlaceController {
	
	@Autowired
	private UserService userService;
	
	@Autowired 
	private SearchInfoService searchInfoService;
		
	
	/*
	 * 	로그인 페이지.
	 */
	@RequestMapping("/login")
	public ModelAndView Login() throws Exception {
		
		return new ModelAndView();
	}
	
	/*
	 * 	로그인 요청.
	 */
	@RequestMapping(value = "login.do")
	@ResponseBody
	public Map<String, Object> LoginSubmit(@RequestBody Map<String, Object> reqMap, HttpServletRequest request) throws Exception {
		Map<String, Object> resMap	= new HashMap<String, Object>(); 								// 응답데이터
		
		String inputId 			= (String) reqMap.get("userId"); 												// 사용자ID
		String inputPwd 			= (String) reqMap.get("userPwd");											// 사용자패스워드
		String randomSalt 		= SHA256Util.generateSalt();														// 솔트
		String inputPwdEnc 	= SHA256Util.getEncrypt(inputPwd, randomSalt); 					// 사용자패스워드(암호화)
		String resMsg 				= "LoginFail";																				// 응답메시지
		
		User user						= userService.findOne(inputId);												// 회원인지 확인.
		// 회원일 경우,
		if(user != null) { 
			inputPwdEnc = SHA256Util.getEncrypt(inputPwd, user.getSalt());
			if(inputPwdEnc.equals(user.getUserPwd())) {	// 비밀번호검증
				resMsg = "LoginSuccess";
			}
		// 비회원일 경우,
		}else {
			user = new User(inputId, inputPwdEnc, randomSalt);
			userService.signupUser(user); //회원가입처리
			resMsg = "SignUpSuccess";
		}
		
		// 세션 생성.
		if(user != null) {
	        HttpSession httpSession = request.getSession(true);
	        httpSession.setAttribute("USER", user);
		}
		
		resMap.put("msg", resMsg);	//응답데이터 셋팅.
					
		
		return resMap;
	}
	
	/*
	 * 	로그아웃 요청.
	 */
	@RequestMapping(value = "logout.do")
	@ResponseBody
	public Map<String, Object> LogoutSubmit(@RequestBody Map<String, Object> reqMap, HttpServletRequest request) throws Exception {
		Map<String, Object> resMap = new HashMap<String, Object>();		// 응답데이터
		
		//세션 제거.
		HttpSession httpSession = request.getSession(true);
		if(httpSession.getAttribute("USER") != null) {
	        httpSession.removeAttribute("USER");
		}
                
        resMap.put("logout","success");	//응답데이터 셋팅.
        
        
		return resMap;
	}
	
	/*
	 * 	홈 페이지.
	 */
	@RequestMapping("/home")
	public ModelAndView Home(HttpServletRequest request) throws Exception {
		// 세션 얻기.
		HttpSession httpSession = request.getSession(true);
		User user = (User) httpSession.getAttribute("USER");
		ModelAndView mv = new ModelAndView();
		
		// 로그인이 안된 경우,
		if(user == null) {
			mv.setViewName("redirect:/login");	//로그인 페이지로 이동.
			throw new ModelAndViewDefiningException(mv);
		}
		mv.addObject("userId", user.getUserId());
		
		
		return mv;
	}
	
	/*
	 * 	검색어 저장 요청.
	 */
	@RequestMapping(value = "search.do")
	@ResponseBody
	public Map<String, Object> searchSubmit(@RequestBody Map<String, Object> reqMap, HttpServletRequest request) throws Exception {		
		Map<String, Object> resMap	 		= new HashMap<String, Object>();

		String searchWord 		= (String) reqMap.get("searchWord");			// 검색어
		String searchDate 		= (String) reqMap.get("searchDate");			// 검색날짜
		String searchTime 		= (String) reqMap.get("searchTime");			// 검색시간
		Long timeStamp 			= (Long) reqMap.get("timeStamp");				// 검색타임스탬프
		int count 						= 1;																	// 검색횟수 
		String resMsg 				= "saveFail";													// 응답메시지
		
		// 세션 얻기.
		HttpSession httpSession = request.getSession(true);
		User user = (User) httpSession.getAttribute("USER");
		
		// 로그인이 된 경우,
		if(user != null) {
			SearchInfo searchInfo = searchInfoService.findSearchInfo(searchWord);	//검색어 내역 확인.
			
			// 기존 검색어가 있을 경우,
			if(searchInfo != null) {
				count = searchInfo.getCount() + 1;
				searchInfoService.deleteSearchInfo(searchWord);		// 기존 검색어 삭제.
			}
			
			// 검색어 저장.
			SearchInfo newSearchInfo = new SearchInfo(user.getUserId(), searchWord, searchDate, searchTime, timeStamp, count);
			searchInfoService.saveSearchInfo(newSearchInfo);
			resMsg = "saveSuccess";
		}
		
		resMap.put("resMsg", resMsg);
		
		return reqMap;
	}
	
	/*
	 * 	나의검색 히스토리 목록 요청.
	 */
	@RequestMapping(value = "showList.do")
	@ResponseBody
	public Map<String, Object> showList(@RequestBody Map<String, Object> reqMap, HttpServletRequest request) throws Exception {
		Map<String, Object> resMap	 		= new HashMap<String, Object>();
		
		List<SearchInfo> searchInfoList 	= new ArrayList<SearchInfo>();
		String resMsg 	= "getListFail";		// 응답메시지

		// 세션 얻기.
		HttpSession httpSession = request.getSession(true);
		User user = (User) httpSession.getAttribute("USER");
		
		// 로그인이 된 경우,
		if(user != null) {
			searchInfoList = searchInfoService.showSearchInfo(user.getUserId());	
			if(searchInfoList.size() > 0) {
				resMsg = "getListSuccess";
			}
		}
		
		resMap.put("mySearchList", searchInfoList);
		resMap.put("resMsg", resMsg);
		
		return resMap;
	}
	
	/*
	 * 	인기 키워드 목록 요청.
	 */
	@RequestMapping(value = "famousList.do")
	@ResponseBody
	public Map<String, Object> famousList(@RequestBody Map<String, Object> reqMap, HttpServletRequest request) throws Exception {
		Map<String, Object> resMap 			= new HashMap<String, Object>();
		List<SearchInfo> famousSearchList = new ArrayList<SearchInfo>();
		String resMsg 	= "getListFail";		// 응답메시지
		
		// 세션 얻기.
		HttpSession httpSession = request.getSession(true);
		User user = (User) httpSession.getAttribute("USER");
		
		// 로그인이 된 경우,
		if(user != null) {
			famousSearchList = searchInfoService.showFamousInfo();
			if(famousSearchList.size() > 0) {
				resMsg = "getListSuccess";
			}
		}
		
		resMap.put("famousSearchList", famousSearchList);
		resMap.put("resMsg", resMsg);
			
		
		return resMap;
	}
}
