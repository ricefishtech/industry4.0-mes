package com.qcadoo.report.api.xls.abstractview;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public abstract class AbstractXLSXView extends AbstractView {


    /** The content type for an Excel response */
    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /** The extension to look for existing templates */
    protected static final String EXTENSION = ".xlsx";

    private static final String CREATOR = "Ricefish MES";

    private String url;

    /**	 * Default Constructor.	 * Sets the content type of the view to "application/vnd.ms-excel".	 */
    public AbstractXLSXView() {
        setContentType(CONTENT_TYPE);
    }
    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    /**
     * Renders the Excel view, given the specified model.
     */
    @Override
    protected final void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        XSSFWorkbook workbook;
        ByteArrayOutputStream baos = createTemporaryOutputStream();

        workbook = new XSSFWorkbook();
        POIXMLProperties xmlProps = workbook.getProperties();
        POIXMLProperties.CoreProperties coreProps =  xmlProps.getCoreProperties();
        coreProps.setCreator(CREATOR);
        buildExcelDocument(model, workbook, request, response);
        setupResponse(response);
        workbook.write(baos);
        writeToResponse(response, baos);
    }


    protected abstract void buildExcelDocument(
            Map<String, Object> model, XSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    protected void setupResponse(HttpServletResponse response){
        // empty for compatibility with other XLSX reports
    }

}