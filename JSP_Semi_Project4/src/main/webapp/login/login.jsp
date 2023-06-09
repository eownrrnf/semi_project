<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="http://code.jquery.com/jquery.js"></script>

<!-- 카카오 -->
<script src="https://developers.kakao.com/sdk/js/kakao.min.js"></script>
<script type="text/javascript">
	Kakao.init('813cd237c40399ffe801d1e722e6e738');
	Kakao.isInitialized();
</script>

<!-- 구글 -->
<script src="https://accounts.google.com/gsi/client" async defer></script>
<script src="https://apis.google.com/js/platform.js" async defer></script>
<script	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<title>로그인</title>
<style type="text/css">
.main_con {
	text-align: center;
	align-content: center;
	align-items: center;
	width: 500px;
	margin: 320px auto;
}

#top{
	top: 0px;
}

table{
	margin: auto !important;
}

#buttonDiv{
	margin-left: 18%; 
}

.in {
	width: 300px;
	padding: 10px;
	border: none;
	border-bottom: 1px solid #ccc;
}

#btn {
  width: 320px;
  height: 50px;
  background-color: #424242;
  color: white;
  font-size: 23px;
  font-weight: bolder;
  border: none;
}

#btn:hover {
  color: #FA0F97;
}

#button_1, #button_2 {
  background-color: transparent;
  border: none;
  font-size: 12px;
  color: #a0a0a0;
  cursor: pointer;
}

#button_1:hover, #button_2:hover {
	color: #FA0F97;
}

#button_2 {
  color: black;
  margin-left: 5%;
}

#kakao {
  width: 320px;
}

</style>
</head>
<body>

	<jsp:include page="../test_main_top.jsp" />


	<div class="main_con">
		<h3>로그인 화면</h3>
		
		type : ${param.loginType }
		
		<div>
			<form action="login_check.do" method="post">
				<input type="hidden" name="loginType" value="${param.loginType }">
				<table>
					<tr>
						<th>ID</th>
						<td><input name="id"></td>
					</tr>
					<tr>
						<th>PWD</th>
						<td><input type="password" name="pwd"></td>
					</tr>
					<tr>
						<td colspan="2"><input type="submit" value="로그인"> 
						<input
							type="button" value="회원가입"
							onclick="location.href='<%=request.getContextPath()%>/register.do'">
						</td>
					</tr>

				</table>
			</form>
			<input type="button" value="아이디 찾기"
				onclick="location.href='<%=request.getContextPath()%>/forget_id.do'">
			<input type="button" value="비밀번호 찾기"
				onclick="location.href='<%=request.getContextPath()%>/forget_pwd.do'">
		</div>

		<!-- 카카오 로그인 버튼 -->
		<div>
			<a onclick="loginKakao();"> <img
				src="img/kakao_login_medium_wide.png?ver=1" alt="카카오 로그인"></img>
			</a>
		</div>

		<!-- 구글 로그인 버튼 -->
		<div id="buttonDiv"></div>


	</div>
</body>

<script type='text/javascript'>
	
	
	// 카카오 로그인 api
	function loginKakao() {
		//1. 로그인 시도
		Kakao.Auth.login({
			success : function(authObj) {
								/* var accessToken = Kakao.Auth.getAccessToken();	// 액세스 토큰 할당
								Kakao.Auth.setAccessToken(accessToken);	// 토큰 등록 
								alert(accessToken);*/

				//2. 로그인 성공시, API 호출
				Kakao.API.request({
					url : '/v2/user/me',
					success : function(res) {						
								
								const kakao_id = res.id;
								/* const kakao_email = res.kakao_account.email;
								const kakao_nickname = res.properties.nickname;
								
								
								alert(id);
								alert(email); */
								
								<%-- location.href = "<%=request.getContextPath()%>/login_check.do?id="+id+"&pwd="+id; --%>
								// post 방식으로 값 전달..
								const form = document.createElement('form');        // form 태그 생성 
								let objs = document.createElement('input');             // 값을 넣을 input 생성 
								objs.setAttribute('type', 'hidden');                                  // 값의 type
								objs.setAttribute('name', "id");                  // 값을 담을 변수 이름 : 인증 성공 시 서버에서 받아서 셋팅 
								objs.setAttribute('value', kakao_id);          // 값 : 인증 성공시 서버에서 받아서 셋팅 
								form.appendChild(objs);
								let objs2 = document.createElement('input');             // 값을 넣을 input 생성 
								objs2.setAttribute('type', 'hidden');                                  // 값의 type
								objs2.setAttribute('name', "pwd");                  // 값을 담을 변수 이름 : 인증 성공 시 서버에서 받아서 셋팅 
								objs2.setAttribute('value', kakao_id);          // 값 : 인증 성공시 서버에서 받아서 셋팅 
								form.appendChild(objs2);
								let objs3 = document.createElement('input');             // 값을 넣을 input 생성 
								objs3.setAttribute('type', 'hidden');                                  // 값의 type
								objs3.setAttribute('name', 'loginType');                  // 값을 담을 변수 이름 : 인증 성공 시 서버에서 받아서 셋팅 
								objs3.setAttribute('value', "${param.loginType }");          // 값 : 인증 성공시 서버에서 받아서 셋팅 
								form.appendChild(objs3);
								form.setAttribute('method', 'post');                            //get,post 가능
								form.setAttribute('action', "login_check.do");         // 호출할 url : 인증 성공시 서버에서 받아서 셋팅 
								document.body.appendChild(form);
								form.submit();
					}
				})
			},
			fail : function(err) {
				alert(JSON.stringify(err));
			}
		});
	};
	
	// 구글 로그인 api
	// 로그인 버튼 표시
	function handleCredentialResponse(response) {
    	const responsePayload = parseJwt(response.credential);
    	console.log("ID: " + responsePayload.sub);
        console.log('Full Name: ' + responsePayload.name);
        console.log('Given Name: ' + responsePayload.given_name);
        console.log('Family Name: ' + responsePayload.family_name);
        console.log("Image URL: " + responsePayload.picture);
        console.log("Email: " + responsePayload.email);
        
        const google_id = responsePayload.sub;
        
        const form = document.createElement('form');        // form 태그 생성 
		let objs = document.createElement('input');             // 값을 넣을 input 생성 
		objs.setAttribute('type', 'hidden');                                  // 값의 type
		objs.setAttribute('name', "id");                  // 값을 담을 변수 이름 : 인증 성공 시 서버에서 받아서 셋팅 
		objs.setAttribute('value', google_id);          // 값 : 인증 성공시 서버에서 받아서 셋팅 
		form.appendChild(objs);
		let objs2 = document.createElement('input');             // 값을 넣을 input 생성 
		objs2.setAttribute('type', 'hidden');                                  // 값의 type
		objs2.setAttribute('name', "pwd");                  // 값을 담을 변수 이름 : 인증 성공 시 서버에서 받아서 셋팅 
		objs2.setAttribute('value', google_id);          // 값 : 인증 성공시 서버에서 받아서 셋팅 
		form.appendChild(objs2);
		let objs3 = document.createElement('input');             // 값을 넣을 input 생성 
		objs3.setAttribute('type', 'hidden');                                  // 값의 type
		objs3.setAttribute('name', 'loginType');                  // 값을 담을 변수 이름 : 인증 성공 시 서버에서 받아서 셋팅 
		objs3.setAttribute('value', "${param.loginType }");          // 값 : 인증 성공시 서버에서 받아서 셋팅 
		form.appendChild(objs3);
		form.setAttribute('method', 'post');                            //get,post 가능
		form.setAttribute('action', "login_check.do");      	   // 호출할 url : 인증 성공시 서버에서 받아서 셋팅 
		document.body.appendChild(form);
		form.submit();
        
        
    }
	
	// 구글 로그인 정보가져오기
    function parseJwt (token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    };
    
    
    window.onload = function () {
      google.accounts.id.initialize({
        client_id: "1041087198718-inbdu2ft7ri1j36l72g3sojpumk19tea.apps.googleusercontent.com",
        callback: handleCredentialResponse
      });
      google.accounts.id.renderButton(
        document.getElementById("buttonDiv"),
        { theme: "outline", size: "large", width: "300"}  // customization attributes
      );
      google.accounts.id.prompt(); // also display the One Tap dialog
    }
    
</script>
</html>