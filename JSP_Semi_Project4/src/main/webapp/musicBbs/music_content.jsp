<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>

	<div align="center">
		<c:set var="vo" value="${musicCont }" />
		<hr width="50%" color="tomato">
		<h3>${vo.getMusic_title() }ALBUM상세페이지</h3>
		<hr width="50%" color="tomato">
		<br>

		<table border="1" cellspacing="0" width="500">
			<tr>
				<th>ALBUM TITLE</th>
				<td>${vo.getMusic_title() }</td>
			</tr>

			<tr>
				<th>ALBUM COVER IMG</th>
				<td align="center"><img
					src="<%=request.getContextPath() %>/fileupload/${vo.getMusic_pic() }"
					width="60" height="60"></td>
			</tr>

			<tr>
				<th>ABOUT ALBUM</th>
				<td>${vo.getMusic_contents() }</td>
			</tr>

			<tr>
				<th>조회수</th>
				<td>${vo.getMusic_playcnt() }</td>
			</tr>

			<tr>
				<th>좋아요수</th>
				<td>${vo.getMusic_likecnt() }</td>
			</tr>

			<tr>
				<th>첨부파일</th>
				<c:if test="${!empty vo.getMusic_mp3() }">
					<td><a
						href="<%=request.getContextPath()%>/fileupload/${vo.getMusic_mp3() }"
						download="${vo.getMusic_mp3() }">다운로드</a></td>
				</c:if>
			</tr>




			<%-- 데이터가 없는 경우 --%>
			<c:if test="${empty vo }">
				<tr>
					<td colspan="2" align="center">
						<h3>게시물 번호에 해당하는 게시물이 없습니다.....</h3>
					</td>
				</tr>
			</c:if>
		</table>
		<br> <input type="button" value="글수정"
			onclick="location.href='upload_modify.do?uno=${dto.getUpload_no() }'">
		&nbsp;&nbsp; <input type="button" value="글삭제"
			onclick="if(confirm('게시글을 정말 삭제하시겠습니까?')) {
                     location.href='upload_delete.do?uno=${dto.getUpload_no() }'
                  }else { return; }">
		&nbsp;&nbsp; <input type="button" value="전체목록"
			onclick="location.href='upload_list.do'">

	</div>
	<!-- 댓글 입력 -->
	<div id="commentForm">
		<label for="comment">댓글:</label> <input type="text" id="comment"
			name="comment">
		<button type="button" id="submitComment">댓글 작성</button>
	</div>
	<br>
	<!-- 댓글 목록 -->
	<div id="commentList">
		<h3>댓글 목록</h3>
		<ul id="commentListContainer">
		</ul>
	</div>
</body>
<script>
	$(document)
			.ready(
					function() {
						// 댓글 불러오기
						function loadComments() {
							$
									.ajax({
										url : 'comment_list.do?album_id=${vo.getMusic_id()}',
										dataType : 'json',
										success : function(data) {
											let commentListContainer = $('#commentListContainer');
											commentListContainer.empty();

											$
													.each(
															data,
															function(index,
																	comment) {
																let commentListItem = $(
																		'<li></li>')
																		.text(
																				comment.content);
																commentListContainer
																		.append(commentListItem);
															});
										},
										error : function(request, status, error) {
											console
													.log("Error loading comments: "
															+ error);
										}
									});
						}

						// 페이지 로드 시 댓글 불러오기
						loadComments();

						// 댓글 작성
						$('#submitComment')
								.on(
										'click',
										function() {
											let commentContent = $('#comment')
													.val();
											$
													.ajax({
														url : 'comment_write.do',
														type : 'POST',
														data : {
															album_id : '${vo.getMusic_id()}',
															content : commentContent
														},
														success : function() {
															// 댓글 작성 후 입력창 초기화 및 댓글 목록 새로고침
															$('#comment').val(
																	'');
															loadComments();
														},
														error : function(
																request,
																status, error) {
															console
																	.log("Error submitting comment: "
																			+ error);
														}
													});
										});
					});
</script>

</html>