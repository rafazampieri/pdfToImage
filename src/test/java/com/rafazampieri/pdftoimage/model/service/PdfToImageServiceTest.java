package com.rafazampieri.pdftoimage.model.service;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.rafazampieri.pdftoimage.model.enums.ImageTypeEnum;
import com.rafazampieri.pdftoimage.model.service.PdfToImageService.ReturnPdfToImage;

// Java 8: http://devblog.virtage.com/2013/07/how-to-get-file-resource-from-maven-srctestresources-folder-in-junit-test/
public class PdfToImageServiceTest {
	
	private PdfToImageService service = new PdfToImageService();
	
	@After
	public void deleteImagesOutputFolder(){
		File directory = new File( getClass().getResource( "/"+PdfToImageService.DEFAULT_OUTPUT_DIRECTORY_NAME ).getFile() );
		if(directory.exists()){
			for(File file: directory.listFiles()){
				file.delete();
			}
		}
	}
	
	@Test
	public void createImagesFromPDfTest() throws Exception{
		String pdfFilePath = getClass().getResource("/5-pages.pdf").getFile();
		
		ReturnPdfToImage returnPdfToImage = service.createImagesFromPdf(new File( pdfFilePath ));
		
		Assert.assertTrue(returnPdfToImage.pageCount == 5);
		Assert.assertEquals(returnPdfToImage.imageTypeOut, ImageTypeEnum.JPG);
		
		File directoryOutExpected = new File( getClass().getResource("/images").getFile() );
		Assert.assertEquals(returnPdfToImage.imagesDirectoryOut, directoryOutExpected);
		Assert.assertTrue( directoryOutExpected.exists() );
		Assert.assertTrue( directoryOutExpected.listFiles().length == 5 );
	}
	
	@Test
	public void createImagesFromPdfWithExtensionTxtTest() throws Exception{
		String filePdfWithExtensionTxtPath = getClass().getResource("/5-pages-pdfWithOtherExtension.txt").getFile();
		try{
			service.createImagesFromPdf(new File( filePdfWithExtensionTxtPath ));
		} catch (Exception e) {
			Assert.fail();
		}
	}
	
}
