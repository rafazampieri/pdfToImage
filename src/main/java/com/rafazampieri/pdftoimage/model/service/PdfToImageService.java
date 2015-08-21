package com.rafazampieri.pdftoimage.model.service;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;

import com.rafazampieri.pdftoimage.model.enums.ImageTypeEnum;
import com.rafazampieri.pdftoimage.model.exception.PdfToImageException;

public class PdfToImageService {
	
	private static final String MSG_ERRO_INESPERADO = "Ocorreu algum erro inesperado ao renderizar a página.";
	
	public static final ImageTypeEnum DEFAULT_IMAGE_TYPE = ImageTypeEnum.JPG;
	public static final Integer DEFAULT_DPI = 72;
	public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "images";
	
	public class ReturnPdfToImage{
		private ReturnPdfToImage(){}
		public Integer pageCount;
		public Integer pdfSize;
		public File pdfDirectory;
		public File imagesDirectoryOut;
		public ImageTypeEnum imageTypeOut;
		public Integer imageDpiOut;
	}
	
	public ReturnPdfToImage createImagesFromPdf(File pdfFile) throws Exception{
		return createImagesFromPdf(pdfFile, null, null, null, null);
	}
	public ReturnPdfToImage createImagesFromPdf(File pdfFile, File directoryOut) throws Exception{
		return createImagesFromPdf(pdfFile, null, null, directoryOut, null);
	}
	public ReturnPdfToImage createImagesFromPdf(File pdfFile, Integer dpiImageOut) throws Exception{
		return createImagesFromPdf(pdfFile, null, dpiImageOut, null, null);
	}
	public ReturnPdfToImage createImagesFromPdf(File pdfFile, ImageTypeEnum outImageType) throws Exception{
		return createImagesFromPdf(pdfFile, outImageType, null, null, null);
	}
	public ReturnPdfToImage createImagesFromPdf(File pdfFile, ImageTypeEnum imageTypeOut, Integer imageDpiOut, File directoryOut) throws Exception{
		return createImagesFromPdf(pdfFile, imageTypeOut, imageDpiOut, directoryOut, null);
	}
	
	public ReturnPdfToImage createImagesFromPdf(File pdfFile, ImageTypeEnum imageTypeOut, Integer imageDpiOut, File directoryOut, Integer page) throws PdfToImageException{
		PDFDocument documentPdf;
		try{
			documentPdf = new PDFDocument();
			documentPdf.load(pdfFile);
		} catch (Exception e) {
			throw new PdfToImageException("O arquivo informado não é um PDF.");
		}
		
		imageTypeOut = imageTypeOut != null ? imageTypeOut : DEFAULT_IMAGE_TYPE;
		imageDpiOut = imageDpiOut != null ? imageDpiOut : DEFAULT_DPI;
		
		directoryOut = createDirectoryOutIfNotExist(pdfFile, directoryOut);
		
		List<Image> imageList = renderDocumentPdf(documentPdf, imageDpiOut, page);
	    for (int i = 0; i < imageList.size(); i++) {
	    	String fileNameOut = "page_" + (page != null ? page : i+1) + "_" + imageDpiOut + "dpi" + imageTypeOut.getExtensionName();
    		try {
				ImageIO.write((RenderedImage) imageList.get(i), imageTypeOut.toLowerCase(), new File(directoryOut, fileNameOut));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	    
	    ReturnPdfToImage returnPdfToImage = new ReturnPdfToImage();
	    returnPdfToImage.pdfSize = documentPdf.getSize();
	    returnPdfToImage.pdfDirectory = pdfFile.getParentFile();
	    returnPdfToImage.imagesDirectoryOut = directoryOut;
	    returnPdfToImage.imageTypeOut = imageTypeOut;
	    returnPdfToImage.imageDpiOut = imageDpiOut;
	    try {
	    	returnPdfToImage.pageCount = documentPdf.getPageCount();
	    } catch (DocumentException e) {
	    	throw new PdfToImageException(MSG_ERRO_INESPERADO);
	    }
	    
	    System.gc();  
	    
	    return returnPdfToImage;
	}
	private File createDirectoryOutIfNotExist(File pdfFile, File directoryOut) throws PdfToImageException {
		directoryOut = directoryOut != null ? directoryOut : new File(pdfFile.getParentFile(), DEFAULT_OUTPUT_DIRECTORY_NAME);
		if(directoryOut.exists() == false){
			try{
				directoryOut.mkdir();
			} catch (Exception e) {
				throw new PdfToImageException("");
			}
		}
		return directoryOut;
	}
	private List<Image> renderDocumentPdf(PDFDocument documentPdf, Integer imageDpiOut, Integer page) throws PdfToImageException {
		boolean isThrowException = false;
		String messageWhenThrowException = null;
		
		SimpleRenderer renderer = new SimpleRenderer();
		renderer.setAntialiasing( SimpleRenderer.OPTION_ANTIALIASING_HIGH ); // https://en.wikipedia.org/wiki/Anti-aliasing
	    renderer.setResolution( imageDpiOut );
	    
	    List<Image> imageList = null;
	    try {
	    	boolean isRenderOnePage = false;
	    	if(page != null){
	    		boolean isValidPage = page > 0 && page <= documentPdf.getPageCount();
				if( isValidPage ){
					if(documentPdf.getPageCount() == 1){
						page = 1;
						System.out.println("Esse documento só contém uma página, não há necessidade de informar o parametro '-page'.");
					}
					isRenderOnePage = true;
	    		} else {
	    			throw new PdfToImageException("Selecione uma página válida do documento entre 1 e " + documentPdf.getPageCount() + ".");
	    		}
	    	}
	    	
	    	boolean isRenderAllPages = !isRenderOnePage; 
			if(isRenderOnePage){
				Integer pageRenderIndex = page-1;
				imageList = renderer.render(documentPdf, pageRenderIndex, pageRenderIndex);
		    } else if (isRenderAllPages) {
		    	imageList = renderer.render(documentPdf);
		    }
		} catch (Exception e) {
			if(e instanceof PdfToImageException){
				messageWhenThrowException = e.getMessage();
			} else {
				messageWhenThrowException = MSG_ERRO_INESPERADO;
			}
		}
		
		if( isThrowException ){
			throw new PdfToImageException( messageWhenThrowException );
		}
		
		return imageList;
	}

}
