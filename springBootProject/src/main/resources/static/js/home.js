$(function() {
	var param = {};

	// 인기키워드 목록 요청.
	$.ajax({
		type : "POST",
		url : "famousList.do",
		contentType : "application/json;charset=UTF-8",
		data : JSON.stringify(param),
		dataType : 'json',
		async : false,
		success : function(response) {
			if(response.resMsg === "getListFail"){
				$("#famousSearchList").append("<tr><th scope='row' colspan='3'>검색결과가 없습니다.</th><td></td><td></td></tr>");
			}else {
				for (var i = 0; i < response.famousSearchList.length; i++) {
					var value = response.famousSearchList[i];
					var grade = i + 1;
					$("#famousSearchList").append(
							"<tr>" + "<th scope='row'>" + grade + "</th>" + "<td>"
									+ value.searchWord + "</td>" + "<td>"
									+ value.count + "</td>" + "</tr>");
				}
			}
		}
	});

	// 내 검색 히스토리 요청
	$.ajax({
		type : "POST",
		url : "showList.do",
		contentType : "application/json;charset=UTF-8",
		data : JSON.stringify(param),
		dataType : 'json',
		async : false,
		success : function(response) {
			if(response.resMsg === "getListFail"){
				$("#mySearchList").append("<tr><th scope='row' colspan='3'>검색결과가 없습니다.</th><td></td><td></td></tr>");
			}else {
				for (var i = response.mySearchList.length - 1; i >= 0; i--) {
					var value = response.mySearchList[i];
					var grade = response.mySearchList.length - i;
					$("#mySearchList").append(
							"<tr>" + "<th scope='row'>" + grade + "</th>" + "<td>"
									+ value.searchWord + "</td>" + "<td>"
									+ value.searchDate + " " + value.searchTime
									+ "</td>" + "</tr>");
				}
			}
		}
	});

	// 로그아웃 요청.
	$("#logoutSubmit").click(function() {
		$.ajax({
			type : "POST",
			url : "logout.do",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(param),
			dataType : 'json',
			async : false,
			success : function(response) {
				if(response.logout !== "success"){
					alert("통신 실패. 다시 시도해주세요.")
				}else {
					alert("로그아웃 되었습니다.");
					window.location.href = "/login";
				}
			}
		});
	});	
});

//커스텀오버레이를 담을 배열.
var customOverlays = [];

var mapContainer = document.getElementById('map'), // 지도를 표시할 div
mapOption = {
	center : new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
	level : 3
// 지도의 확대 레벨
};

// 지도를 생성합니다
var map = new kakao.maps.Map(mapContainer, mapOption);

// 장소 검색 객체를 생성합니다
var ps = new kakao.maps.services.Places();

// 키워드로 장소를 검색합니다
searchPlaces();

// 키워드 검색 함수.
function searchPlaces() {

	var keyword = document.getElementById('keyword').value;
	var date = new Date();
	var currentDate = date.getFullYear() + "." + (date.getMonth() + 1) + "."
			+ date.getDate();
	var currentTime = date.getHours() + ":" + date.getMinutes() + ":"
			+ date.getSeconds();
	var timestamp = +new Date();

	if (keyword.replace(/^\s+|\s+$/g, '')) {
		param = {
			"searchWord" : keyword,
			"searchDate" : currentDate,
			"searchTime" : currentTime,
			"timeStamp" : timestamp
		};

		// 검색한 히스토리 저장 요청.
		$.ajax({
			type : "POST",
			url : "search.do",
			contentType : "application/json;charset=UTF-8",
			dataType : 'json',
			data : JSON.stringify(param),
			async : false,
			success : function(response) {
				if(response.resMsg === "saveFail"){
					alert("통신 실패. 다시 시도해주세요.");
				}else {
					alert("검색어를 저장했습니다.");
					$("body").load("/home");
					// 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
					ps.keywordSearch(keyword, placesSearchCB);
				}
			}
		});
	}
}

// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
function placesSearchCB(data, status, pagination) {
	if (status === kakao.maps.services.Status.OK) {

		// 정상적으로 검색이 완료됐으면
		// 검색 목록과 커스텀오버레이를 표출합니다
		displayPlaces(data);

		// 페이지 번호를 표출합니다
		displayPagination(pagination);

	} else if (status === kakao.maps.services.Status.ZERO_RESULT) {

		alert('검색 결과가 존재하지 않습니다.');
		return;

	} else if (status === kakao.maps.services.Status.ERROR) {

		alert('검색 결과 중 오류가 발생했습니다.');
		return;

	}
}

//검색 결과 목록과 커스텀오버레이를 표출하는 함수입니다
function displayPlaces(places) {

	var listEl = document.getElementById('placesList'), menuEl = document
			.getElementById('menu_wrap'), fragment = document
			.createDocumentFragment(), bounds = new kakao.maps.LatLngBounds(), listStr = '';

	// 검색 결과 목록에 추가된 항목들을 제거합니다
	removeAllChildNods(listEl);

	// 지도에 표시되고 있는 커스텀오버레이를 제거합니다
	removeCustomOverlay();

	for (var i = 0; i < places.length; i++) {

		var placePosition = new kakao.maps.LatLng(places[i].y, places[i].x), customOverlay = addCustomOverlay(
				placePosition, places[i]), itemEl = getListItem(i, places[i]); // 검색 결과
	
		bounds.extend(placePosition);

		fragment.appendChild(itemEl);
	}
	
	// 검색결과 항목들을 검색결과 목록 Elemnet에 추가합니다
	listEl.appendChild(fragment);
	menuEl.scrollTop = 0;

	// 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
	map.setBounds(bounds);
}

// 검색결과 항목을 Element로 반환하는 함수입니다
function getListItem(index, places) {
	
	var el = document.createElement('li'), itemStr = '<span class="markerbg marker_'
			+ (index + 1)
			+ '"></span>'
			+ '<div class="info">'
			+ '   <h5>'
			+ places.place_name + '</h5>';

	if (places.road_address_name) {
		itemStr += '    <span>' + places.road_address_name + '</span>'
				+ '   <span class="jibun gray">' + places.address_name
				+ '</span>';
	} else {
		itemStr += '<span>' + places.address_name + '</span>';
	}

	itemStr += ' <span class="tel">' + places.phone + '</span>';

	itemStr += '<a class="gomap" href=https://map.kakao.com/link/map/' + places.id + ' target="_blank">지도바로가기</a></div>';

	el.innerHTML = itemStr;
	el.className = 'item';
	
	return el;
}

//커스텀오버레이를 생성하고 지도 위에 커스텀오버레이를 표시하는 함수입니다
function addCustomOverlay(position, place) {
	
	
	var content = '<div class="overlay_wrap">' + 
    '    <div class="overlay_info">' + 
    '        <div class="title">' + 
    '            ' + place.place_name + 
    '        </div>' + 
    '        <div class="body">' + 
    '            <div class="overlay_desc">' + 
    '                <div class="ellipsis">' + place.address_name + '</div>' + 
    '            </div>' + 
    '        </div>' + 
    '    </div>' +    
    '</div>';
	
	var customOverlay = new kakao.maps.CustomOverlay({
	    position: position,
	    content: content
	});

	customOverlay.setMap(map);
	customOverlays.push(customOverlay); // 배열에 생성된 커스텀오버레이를 추가합니다

	return customOverlay;
}

//지도 위에 표시되고 있는 커스텀오버레이를 모두 제거합니다
function removeCustomOverlay() {
	for (var i = 0; i < customOverlays.length; i++) {
		customOverlays[i].setMap(null);
	}
	customOverlays = [];
}

//검색결과 목록 하단에 페이지번호를 표시는 함수입니다
function displayPagination(pagination) {
	var paginationEl = document.getElementById('pagination'), fragment = document
			.createDocumentFragment(), i;

	// 기존에 추가된 페이지번호를 삭제합니다
	while (paginationEl.hasChildNodes()) {
		paginationEl.removeChild(paginationEl.lastChild);
	}

	for (i = 1; i <= pagination.last; i++) {
		var el = document.createElement('a');
		el.href = "#";
		el.innerHTML = i;

		if (i === pagination.current) {
			el.className = 'on';
		} else {
			el.onclick = (function(i) {
				return function() {
					pagination.gotoPage(i);
				}
			})(i);
		}

		fragment.appendChild(el);
	}
	paginationEl.appendChild(fragment);
}


//검색결과 목록의 자식 Element를 제거하는 함수입니다
function removeAllChildNods(el) {
	while (el.hasChildNodes()) {
		el.removeChild(el.lastChild);
	}
}