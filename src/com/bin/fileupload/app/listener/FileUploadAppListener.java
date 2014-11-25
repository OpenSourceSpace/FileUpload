package com.bin.fileupload.app.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bin.fileupload.app.utils.FileUploadAppProperties;

public class FileUploadAppListener implements ServletContextListener {
	

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/upload.properties") ;
		
		Properties properties = new Properties() ;
		try {
			properties.load(inputStream);
			for (Map.Entry<Object, Object> prop:properties.entrySet()){
				String propertyName = (String) prop.getKey() ;
				String propertyValue = (String) prop.getValue() ;
				FileUploadAppProperties.getInstance().addProperty(propertyName, propertyValue) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
