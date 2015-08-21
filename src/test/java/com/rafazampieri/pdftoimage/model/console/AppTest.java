package com.rafazampieri.pdftoimage.model.console;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import com.rafazampieri.pdftoimage.console.App;
import com.rafazampieri.pdftoimage.model.enums.ImageTypeEnum;
import com.rafazampieri.pdftoimage.model.service.PdfToImageService;

public class AppTest extends Assert{
	
	@Rule
    public final ExpectedSystemExit systemExit = ExpectedSystemExit.none();
	
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
	public void createOnePageImageFromPdf(){
		File rootDirectory = new File( getClass().getResource("/").getFile() );
		
		String page = "3";
		try {
			File pdfFilePath = new File(rootDirectory, "5-pages.pdf");
			App.main( new String[]{ 
				"-pdf", pdfFilePath.getAbsolutePath(),
				"-page", page
			} );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File imagesDirectory = new File( rootDirectory, PdfToImageService.DEFAULT_OUTPUT_DIRECTORY_NAME);
		Assert.assertTrue( imagesDirectory.listFiles().length == 1 );
		
		String fileName = imagesDirectory.listFiles()[0].getName();
		String expectedFileName = "page_" + page + "_" + PdfToImageService.DEFAULT_DPI + "dpi" + PdfToImageService.DEFAULT_IMAGE_TYPE.getExtensionName();
		System.out.println( "teste: " + expectedFileName);
		System.out.println( fileName);
		Assert.assertTrue( expectedFileName, fileName.equals( expectedFileName ) );
	}
	
	@Test
	public void createImagesFromPdfToDefaultImagesFolder(){
		File rootDirectory = new File( getClass().getResource("/").getFile() );
		
		File pdfFilePath = new File(rootDirectory, "5-pages.pdf");
		try {
			App.main( new String[]{ "-pdf", pdfFilePath.getAbsolutePath() } );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File imagesDirectory = new File( rootDirectory, PdfToImageService.DEFAULT_OUTPUT_DIRECTORY_NAME);
		assertOutputImages(imagesDirectory, PdfToImageService.DEFAULT_IMAGE_TYPE, PdfToImageService.DEFAULT_DPI);
	}
	
	@Test
	public void createImagesFromPdfWithDpiAndImagetype(){
		File rootDirectory = new File( getClass().getResource("/").getFile() );
		
		Integer dpiOutput = 100;
		ImageTypeEnum imageTypeGif = ImageTypeEnum.GIF;
		try {
			App.main( new String[]{ 
				"-pdf", new File(rootDirectory, "5-pages.pdf").getAbsolutePath(),
				"-dpi", dpiOutput.toString(),
				"-imageType", imageTypeGif.toLowerCase()
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File imagesDirectory = new File( rootDirectory, PdfToImageService.DEFAULT_OUTPUT_DIRECTORY_NAME);
		assertOutputImages(imagesDirectory, imageTypeGif, dpiOutput);
	}
	
	@Test
	public void callWithNoArguments() throws Exception{
		final PrintStream defaultSystemOut = System.out;
		
		final ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(systemOut));
		
		systemExit.expectSystemExit();
		systemExit.checkAssertionAfterwards(new Assertion() { // Call when "System.exit(int)" is expected
			public void checkAssertion() {
				String standardOutput = systemOut.toString();
				Assert.assertTrue( standardOutput.trim().equals("Help") );
				System.setOut( defaultSystemOut );
			}
		}); 
		App.main(new String[] {});
	}
	
	private void assertOutputImages(File imagesDirectory, ImageTypeEnum imageType, Integer dpiOutput){
		assertTrue( imagesDirectory.exists() );
		assertTrue( imagesDirectory.listFiles().length == 5 );
		for(File image: imagesDirectory.listFiles()){
			String imageExtension = image.getName().split("\\.")[1];
			assertTrue( imageExtension.equals( imageType.toLowerCase() ) );
			
			String imageName = image.getName().split("\\.")[0];
			String[] imageNameSplit = imageName.split("_");
			
			String imageNamePageNumber = imageNameSplit[1];
			assertTrue( imageNamePageNumber.matches("[0-9]*") );
			
			String imageNameDpi = imageNameSplit[2];
			imageNameDpi = imageNameDpi.substring(0, imageNameDpi.length()-3);
			Assert.assertTrue( Integer.parseInt(imageNameDpi) == dpiOutput );
		}
	}
}
