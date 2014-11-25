package com.bin.fileupload.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/x-msdownload") ;
		String fileName = "文件下载.pptx" ;
		resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8")) ;
		
		// 默认response输出本类的out.print部分的html串作为流.如果你有实际的文件,自己set实际的流输出既可以.
		OutputStream out = resp.getOutputStream() ;
		String pptFileName = "D:\\BaiduYunDownload\\javaweb\\javaweb课件\\13. 尚硅谷_佟刚_JavaWEB_国际化.pptx" ;
		
		InputStream in = new FileInputStream(pptFileName) ;
		
		byte[] buffer = new byte[1024] ;
		int len = 0 ;
		
		while ((len = in.read()) != -1) {
			out.write(buffer, 0, len); ;
		}
		in.close();
	}
	
}
