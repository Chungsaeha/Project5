$(function(){			
	$('#loginSubmit').click(function(){		
		if($("#userId").val() === "") {
			alert("사용자 아이디를 입력해주세요.");
		}else if($("#userPwd").val() === "") {
			alert("사용자 비밀번호를 입력해주세요.");
		}else {
			var param = {
				"userId" : $("#userId").val(),
				"userPwd" : $("#userPwd").val()
			}
			$.ajax({
				type : "POST",
				url : "login.do",
				contentType:"application/json;charset=UTF-8",
				dataType: 'json',
		        	data: JSON.stringify(param),
				async: false,
				success : function(response) {
					if(response.msg === "LoginFail"){
						alert("비밀번호를 확인해주세요.");
					}else if(response.msg === "LoginSuccess"){
						alert("로그인 성공했습니다.");
						$("#form1").attr("action", "/home");
						$("#form1")[0].submit();
					}else if(response.msg === "SignUpSuccess"){
						alert("회원가입 성공했습니다.");
						$("#form1").attr("action", "/home");
						$("#form1")[0].submit();
					}
				}
			});
		}
	});
});