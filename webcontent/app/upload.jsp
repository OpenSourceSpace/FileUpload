<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath }/scripts/jquery-1.11.1.js"></script>
<script type="text/javascript">
	$(function(){
		// 1.获取#addFile,并为其添加click响应函数
		var i = 2 ;
		$("#addFile").click(function(){
			$(this).parent().parent().before("<tr class='file'><td>File"
					+ i + ":</td><td><input type='file' name='file"
					+ i + "' /></td></tr><tr class='desc'><td>Desc"
					+ i + ":</td><td><input type='text' name='desc"
					+ i + "' /><button id='delete"
					+ i + "'>删除</button></td></tr>") ;
			i++ ;
			//获取新添加的删除按钮,
			$("#delete" + (i -1)).click(function(){
				var $tr = $(this).parent().parent() ;
				$tr.prev("tr").remove() ;
				$tr.remove() ;
				// 对i 重写排序.
				$(".file").each(function(index){
					var n = index + 1 ;
					$(this).find("td:first").text("File" + n) ;
					$(this).find("td:last input").attr("name","file" + n) ;
				}) ;
				$(".desc").each(function(index){
					var n = index + 1 ;
					$(this).find("td:first").text("Desc" + n) ;
					$(this).find("td:last input").attr("name","desc" + n) ;
				}) ;
				i = i - 1 ;
			}) ;
			
			return false ;// button会自动提交,所以加上这个.
		}) ;
		// 2.利用jQuery生成一下节点,注意数字的变化,并把它们都加到#br的前面.其中有"删除"按钮可以删除当前的file和desc相关节点.
		/* 
		File1:<input type="file" name="file1" />
		<br>
		Desc1:<input type="text" name="desc1" /><button>删除</button>
		<br>
		 */
	}) ;
	
</script>
</head>
<body>
	<font color="red">${message }</font>
	<br><br>

	<form action="${pageContext.request.contextPath }/uploadServlet" method="post" enctype="multipart/form-data">
		<input type="hidden" id="fileNum" name="fileNum" value="1" />
		<table>
			<tr class="file">
				<td>File1:</td>
				<td><input type="file" name="file1" /></td>
			</tr>
			<tr class="desc">
				<td>Desc1:</td>
				<td><input type="text" name="desc1" /></td>
			</tr>
			
			<tr>
				<td><input type="submit" id="submit" value="Submit" /></td>
				<td><button id="addFile">新增一个附件</button></td>
			</tr>
		</table>
		<br>
	</form>
	
</body>
</html>