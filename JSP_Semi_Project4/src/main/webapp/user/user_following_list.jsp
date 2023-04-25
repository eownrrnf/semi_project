<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>


	<jsp:include page="../test_main_top.jsp" />

	<div class="main_con"></div>
	
	<c:set var="vo" value="${userVO }"></c:set>
	<h3>${vo.getUser_nickname() } is Following</h3>
	
	<table border="1">
		<tr>
			<th>팔로잉 id</th>
			<th>프로필 사진</th>
			<th>닉네임</th>
			<th>팔로워</th>
		</tr>
		
		<c:set var="followingList" value="${followingList }"></c:set>
		<c:if test="${!empty followingList }">
			<c:forEach items="${followingList }" var="fvo" >
				<tr>
					<td>${fvo.getFollower_id() }</td>				
					<td><img src="<%=request.getContextPath() %>/fileUpload/${fvo.getUser_pic() }"></td>				
					<td>${fvo.getUser_nickname() }</td>				
					<td>${fvo.getFollowers_count() }</td>				
				</tr>
			</c:forEach>
		</c:if>
		
	</table>
	
</body>
</html>