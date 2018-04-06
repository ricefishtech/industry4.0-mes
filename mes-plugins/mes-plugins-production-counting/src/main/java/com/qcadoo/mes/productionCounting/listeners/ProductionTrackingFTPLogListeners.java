package com.qcadoo.mes.productionCounting.listeners;


import com.google.common.collect.Maps;
import com.qcadoo.mes.productionCounting.ProductionTrackingService;

import com.qcadoo.mes.productionCounting.constants.ProductionCountingConstants;
import com.qcadoo.mes.productionCounting.constants.TrackingOperationProductOutComponentExtFields;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.NumberService;

import com.qcadoo.tenant.api.MultiTenantService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.*;


@Service
public class ProductionTrackingFTPLogListeners {

    private static final String L_FORM = "form";

    private static final String L_GRID = "grid";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductionTrackingFTPLogListeners.class);

    @Autowired
    private NumberService numberService;

    @Autowired
    private ProductionTrackingService productionTrackingService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private MultiTenantService multiTenantService;


// ricefishM
    public void associatedOrdersLog(final ViewDefinitionState view, final ComponentState state, final String[] args) {

        try {
           // FTPListAllFiles ftp = new FTPListAllFiles();
                if(login("146.196.52.181", 21, "szkingnet", "123456")) {
                    listFiles("/");
                }
            disConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public FTPClient ftp;
    public ArrayList<String> listFiles;

    /**
     * 登陆FTP服务器
     * @param host FTPServer IP地址
     * @param port FTPServer 端口
     * @param username FTPServer 登陆用户名
     * @param password FTPServer 登陆密码
     * @return 是否登录成功
     * @throws IOException
     */
    public boolean login(String host,int port,String username,String password) throws IOException{
        try {
            ftp = new FTPClient();
            ftp.connect(host, port);
        } catch (UnknownHostException ex) {
            throw new IOException("Can't find FTP server '" + host + "'");
        }
        if(FTPReply.isPositiveCompletion(ftp.getReplyCode())){
            if(ftp.login(username, password)){
                ftp.setControlEncoding("UTF-8");
                return true;
            }
        }
        if(ftp.isConnected()){
            ftp.disconnect();
        }
        return false;
    }

    /**
     * 关闭数据链接
     * @throws IOException
     */
    public void disConnection() throws IOException{
        if(ftp.isConnected()){
            ftp.disconnect();
        }
    }



    public void listFiles(String pathName) throws IOException{
        if(pathName.startsWith("/")&&pathName.endsWith("/")){
            String directory = pathName;
            //更换目录到当前目录
            ftp.changeWorkingDirectory(directory);
            FTPFile[] files = ftp.listFiles();
            for(FTPFile file:files){
                if(file.isFile()){
                    // listFiles.add(directory+file.getName());
                   // String orgiFileName = file.getName().substring(0, file.getName().lastIndexOf("."));//获取不包括后缀的文件名

                    String query = buildQuery();
                    List<Map<String, Object>> trackingList = jdbcTemplate.queryForList(query, Maps.newHashMap());

                    if(trackingList.size()>0){
                        for(Map trackingMap :trackingList) {

                            if (trackingMap.get("ordernumbercode") != null && trackingMap.get("id") != null) {

                                String ordernumbercode = trackingMap.get("ordernumbercode").toString();
                                String trackingId = trackingMap.get("id").toString();

                                if (ordernumbercode != null && trackingId != null) {
                                    if (directory.equals(ordernumbercode)) {
                                        String CONFIRM_SQL = "UPDATE productioncounting_trackingoperationproductoutcomponentext "
                                                + "SET logfile=:logfile WHERE id = :id";
                                        Map<String, Object> parameters = new HashMap<String, Object>();
                                        parameters.put("id", Integer.parseInt(trackingId));
                                        parameters.put("logfile", directory + file.getName());
                                        SqlParameterSource namedParameters = new MapSqlParameterSource(parameters);
                                        jdbcTemplate.update(CONFIRM_SQL, namedParameters);
                                    }
                                }
                            }
                        }


                    }



                }else if(file.isDirectory()){
                    listFiles(directory+file.getName()+"/");
                }
            }
        }
    }

    private String buildQuery() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ptotext.id AS id, '/'||orders.number||'/'||barcode.code||'/' AS ordernumbercode FROM productioncounting_productiontracking pt ");
        query.append("LEFT JOIN orders_order orders ON pt.order_id = orders.id ");
        query.append("LEFT JOIN technologies_barcodeoperationcomponent barcode ON pt.order_id = barcode.order_id ");
        query.append("LEFT JOIN productioncounting_trackingoperationproductoutcomponent ptot ON pt.id = ptot.productiontracking_id ");
        query.append("LEFT JOIN productioncounting_trackingoperationproductoutcomponentext ptotext ON ptot.id = ptotext.trackingoperationproductoutcomponent_id ");
        query.append("WHERE pt.tenantid = " + multiTenantService.getCurrentTenantId());

        return query.toString();
    }

}