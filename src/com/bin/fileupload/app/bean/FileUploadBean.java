package com.bin.fileupload.app.bean;

public class FileUploadBean {
	private Integer id ;
	private String fileName ;
	private String filePath ;
	private String fileDes ;
	public FileUploadBean(String fileName, String filePath, String fileDes) {
		super();
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileDes = fileDes;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileDes() {
		return fileDes;
	}
	public void setFileDes(String fileDes) {
		this.fileDes = fileDes;
	}
	@Override
	public String toString() {
		return "FileUploadBean [id=" + id + ", fileName=" + fileName + ", filePath=" + filePath + ", fileDes=" + fileDes + "]";
	} ;
	
	
}
