package com.bin.fileupload.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.bin.fileupload.app.bean.FileUploadBean;
import com.bin.fileupload.app.exception.InvalidExtNameException;
import com.bin.fileupload.app.utils.FileUploadAppProperties;


public class FileUploadServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final String FILE_PATH = "/WEB-INF/files/" ;
	private static final String TEMP_DIR = "d:\\tempDirectory" ;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = null ;
		
		String fileMaxSize = FileUploadAppProperties.getInstance().getProperty("file.max.size") ;
		String totalFileMaxSize = FileUploadAppProperties.getInstance().getProperty("total.file.max.size") ;
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		// 设置内存中最多存储的文件的大小,若超出,写入硬盘.
		factory.setSizeThreshold(1024 * 5);// 5k
		File tempDirectory = new File(TEMP_DIR) ;
		factory.setRepository(tempDirectory);// 设置临时文件夹
		
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		// 设置总的文件最大值.
		upload.setSizeMax(Integer.parseInt(totalFileMaxSize));// 5M
		upload.setFileSizeMax(Integer.parseInt(fileMaxSize));
		try {
			// 把需要上传的FileItem都放入到该Map中.键:文件的待存放的名称 值:FileItem
			Map<String, FileItem> uploadFiles = new HashMap<String, FileItem>() ;
			// 解析请求,得到FileItem的集合.
			List<FileItem> items = upload.parseRequest(req) ;
			// 1.构建FileUploadBean的集合.同时填充uploadFiles
			List<FileUploadBean> beans = buildFileUploadBeans(items,uploadFiles) ;
			// 2.校验扩展名.
			validateExtName(beans) ;
			
			// 3.校验文件的大小:在解析时,已经校验了.我们只需要通过异常得到结果.
			// 4.进行文件的上传操作.
			upload(uploadFiles) ;
			// 5.保存上传的信息到数据库.
			saveBeans() ;
			// 6.删除临时文件夹.使用commons-io的类.
			//FileUtils.deleteDirectory(new File(TEMP_DIR));// 这种方法是存在问题的.有可能删掉别的用户正在使用的临时文件.
			path = "/app/success.jsp" ;
		} catch (Exception e) {
			e.printStackTrace();
			path= "/app/upload.jsp" ;
			req.setAttribute("message", e.getMessage());
		}
		
		req.getRequestDispatcher(path).forward(req, resp);
	}

	/**
	 * 文件上传前的准备工作.得到FilePath和inputStream.
	 */
	private void upload(Map<String, FileItem> uploadFiles) throws IOException {
		for(Map.Entry<String, FileItem> uploadFile:uploadFiles.entrySet()){
			String filePath = uploadFile.getKey() ;
			FileItem item= uploadFile.getValue() ;
			
			upload(filePath,item.getInputStream()) ;
		}
		
	}

	/**
	 * 文件上传的IO方法.
	 */
	private void upload(String filePath, InputStream inputStream) throws IOException {
		OutputStream out = new FileOutputStream(filePath) ;
		
		byte[] buffer = new byte[1024] ;
		int len = 0 ;
		
		while ((len= inputStream.read(buffer)) != -1) {
			out.write(buffer,0,len);
		}
		inputStream.close();
		out.close(); 
		System.out.println(filePath);
	}

	private void saveBeans() {
		System.out.println("保存到数据库!");
		
	}

	/**
	 * 校验文件的后缀名.
	 */
	private void validateExtName(List<FileUploadBean> beans) {
		String exts = FileUploadAppProperties.getInstance().getProperty("exts") ;
		List<String> extList = Arrays.asList(exts.split(",")) ;
		for (FileUploadBean bean: beans) {
			String fileName = bean.getFileName() ;
			String extName = fileName.substring(fileName.lastIndexOf(".") + 1) ;
			
			if (!extList.contains(extName)) {
				// 抛一个异常.
				throw new InvalidExtNameException(fileName + ":文件扩展名不合法!") ;
			}
		}
	}

	/**
	 * 利用传入的FileItem的集合,构建FileUploadBean的集合,同时填充uploadFiles
	 * FileUploadBean: id,fileName,filePath,fileDesc
	 * uploadFiles:Map<String,FileItem>类型,存放文件域类型的FileItem.键:保存的文件的真实路径名.值:FileItem
	 * 构建过程:
	 * 1. 对传入的FileItem的集合进行遍历,得到desc的按个Map.键:desc的fieldName(desc1,desc2...),值:desc的那个输入的文本值.
	 * 2. 对传入的FileItem集合遍历,得到文件域的fieldName,desc,fileName,filePath构建bean对象.填充uploadFiles.
	 */
	private List<FileUploadBean> buildFileUploadBeans(List<FileItem> items, Map<String, FileItem> uploadFiles) {
		List<FileUploadBean> beans = new ArrayList<FileUploadBean>() ;
		// 1. 遍历FileItem的集合,先得到desc的Map<String,String>,其中键:fieldName(desc1,desc2..),
		// 值:表单域对应字段的值.
		Map<String, String> descs = new HashMap<>() ;
		for(FileItem item:items){
			if (item.isFormField()) {
				descs.put(item.getFieldName(), item.getString()) ;
			}
		}
		// 2. 再遍历FileItem的集合,得到文件域的FileItem对象.
		// 每得到一个FileItem对象都创建一个FileUploadBean对象.
		//得到fileName,构建filePath,从1的Map中得到当前FileItem对应的desc.
		// 使用fileName后面的数字匹配.
		for (FileItem item: items) {
			if (!item.isFormField()) {
				String fieldName = item.getFieldName() ;
				String index = fieldName.substring(fieldName.length() - 1) ;
				
				String fileName = item.getName() ;
				String desc = descs.get("desc" + index) ;
				String filePath = getFilePath(fileName) ;
				
				FileUploadBean bean = new FileUploadBean(fileName, filePath, desc) ;
				beans.add(bean) ;
				
				uploadFiles.put(filePath, item) ;
			}
		}
		return beans;
	}

	/**
	 * 根据给定的文件名构建一个随机的文件名.
	 * 1.构建的文件名的扩展名和给定的文件的扩展名一致.
	 * 2.利用ServletContext的getRealPath方法得到真实路径.
	 */
	private String getFilePath(String fileName) {
		String extName = fileName.substring(fileName.lastIndexOf(".")) ;
		Random random = new Random();
		int a = random.nextInt(100000) ;//保证了唯一性.(后期可以使用MD5得到一个随机数.)
		String filePath = getServletContext().getRealPath(FILE_PATH) + "\\" + System.currentTimeMillis() + a + extName ;
		return filePath;
	}
	
}
