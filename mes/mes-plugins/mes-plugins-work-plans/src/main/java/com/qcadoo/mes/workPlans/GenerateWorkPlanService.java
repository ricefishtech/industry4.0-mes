
package com.qcadoo.mes.workPlans;

import com.google.common.collect.Lists;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.localization.api.utils.DateUtils;
import com.qcadoo.mes.basic.ParameterService;
import com.qcadoo.mes.orders.constants.OrderFields;
import com.qcadoo.mes.orders.util.OrderHelperService;
import com.qcadoo.mes.technologies.BarcodeOperationComponentService;
import com.qcadoo.mes.technologies.constants.TechnologyFields;
import com.qcadoo.mes.workPlans.constants.ParameterFieldsWP;
import com.qcadoo.mes.workPlans.constants.WorkPlanFields;
import com.qcadoo.mes.workPlans.constants.WorkPlanType;
import com.qcadoo.mes.workPlans.constants.WorkPlansConstants;
import com.qcadoo.mes.workPlans.print.WorkPlanForDivisionPdfService;
import com.qcadoo.mes.workPlans.print.WorkPlanPdfService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.file.FileService;
import com.qcadoo.report.api.ReportService;
import com.qcadoo.security.api.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class GenerateWorkPlanService {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateWorkPlanService.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private WorkPlansService workPlanService;

    @Autowired
    private WorkPlanPdfService workPlanPdfService;

    @Autowired
    private WorkPlanForDivisionPdfService workPlanForDivisionPdfService;

    @Autowired
    private OrderHelperService orderHelperService;

    @Autowired
    private BarcodeOperationComponentService barcodeOperationComponentService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private TranslationService translationService;

    public void generateWorkPlan(final List<Entity> ordersorg) {

        Entity workPlan = getWorkPlanDD().create();

        workPlan.setField(WorkPlanFields.NAME, generateNameForWorkPlan());
        workPlan.setField(WorkPlanFields.TYPE, WorkPlanType.NO_DISTINCTION.getStringValue());
        workPlan.setField(WorkPlanFields.ORDERS, ordersorg);
        workPlan.setField(WorkPlanFields.GENERATED, false);

        workPlan.setField(WorkPlanFields.DONT_PRINT_ORDERS_IN_WORK_PLANS,
                parameterService.getParameter().getField(ParameterFieldsWP.DONT_PRINT_ORDERS_IN_WORK_PLANS));
        workPlan = workPlan.getDataDefinition().save(workPlan);


        if (workPlan == null) {
            LOG.warn("没有相关工作计划");
            return;
        } else if (StringUtils.isNotBlank(workPlan.getStringField(WorkPlanFields.FILE_NAME))) {
            LOG.warn("工作计划已经生成过了");
            return;
        }

        List<Entity> orders = workPlan.getManyToManyField(WorkPlanFields.ORDERS);

            if (orders == null) {
                LOG.warn("没有相关联订单");
                return;
            }
            if(!validateOrders(orders)){
                return;
            }
            createBarcodeOCForOrders(orders);

            List<String> numbersOfOrdersWithoutTechnology = orderHelperService.getOrdersWithoutTechnology(orders);

            if (!numbersOfOrdersWithoutTechnology.isEmpty()) {
                LOG.warn("订单缺少工艺");
                return;
            }

           workPlan.setField(WorkPlanFields.WORKER,securityService.getCurrentUserName());
           workPlan.setField(WorkPlanFields.GENERATED,"1");
           workPlan.setField(WorkPlanFields.DATE,new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, LocaleContextHolder.getLocale()).format(new Date()));

           workPlan = workPlan.getDataDefinition().save(workPlan);

           workPlan = workPlanService.getWorkPlan(workPlan.getId());

            try {
                generateWorkPlanDocuments(workPlan);
                checkIfInactiveOrders(orders);
            } catch (IOException e) {
                LOG.warn(e.getMessage());
            } catch (DocumentException e) {
                LOG.warn(e.getMessage());
            }
    }

    private boolean validateOrders(final List<Entity> orders) {
        List<String> numbers = Lists.newArrayList();
        for (Entity order : orders){
            if(order.getBelongsToField(OrderFields.TECHNOLOGY) == null){
                numbers.add(order.getStringField(OrderFields.NUMBER));
            }
        }

        if(!numbers.isEmpty()){
            String commaSeparatedNumbers = numbers.stream()
                    .map(i -> i.toString())
                    .collect(Collectors.joining(", "));
            LOG.warn("订单缺少工艺");
            return false;
        }
        return true;
    }

    private boolean checkIfInactiveOrders(final List<Entity> orders) {
        List<String> numbers = Lists.newArrayList();
        for (Entity order : orders){
            if(!order.isActive()){
                numbers.add(order.getStringField(OrderFields.NUMBER));
            }
        }

        if(!numbers.isEmpty()){
            String commaSeparatedNumbers = numbers.stream()
                    .map(i -> i.toString())
                    .collect(Collectors.joining(", "));
            LOG.warn("工作计划包含无效订单");
            return false;
        }
        return true;
    }

    private void generateWorkPlanDocuments(final Entity workPlan) throws IOException,
            DocumentException {
        Entity workPlanWithFilename = fileService.updateReportFileName(workPlan, WorkPlanFields.DATE, "workPlans.workPlan.report.fileName");
        workPlanPdfService.generateDocument(workPlanWithFilename, LocaleContextHolder.getLocale());
        if (workPlan.getStringField(WorkPlanFields.TYPE).compareTo(WorkPlanType.BY_DIVISION.getStringValue()) == 0) {
            String fileNameForDivision = "Division_Workplan";
            Entity workPlanForDivision = fileService.updateReportFileName(workPlanWithFilename, WorkPlanFields.DATE,
                    fileNameForDivision);
            workPlanForDivisionPdfService.generateDocument(workPlanForDivision, LocaleContextHolder.getLocale(), fileNameForDivision,
                    PageSize.A4.rotate());
        }
    }

    private void createBarcodeOCForOrders(final List<Entity> orders) {
        for (Entity order : orders) {
            createBarcodeOCForOrder(order);
        }

    }

    private void createBarcodeOCForOrder(final Entity order) {
        Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);
        if(technology != null) {
            List<Entity> tocs = order.getBelongsToField(OrderFields.TECHNOLOGY)
                    .getHasManyField(TechnologyFields.OPERATION_COMPONENTS);
            for (Entity toc : tocs) {
                barcodeOperationComponentService.createBarcodeOperationComponent(order, toc);
            }
        }
    }

    public String generateNameForWorkPlan() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, LocaleContextHolder.getLocale());

        return translationService.translate("workPlans.workPlan.defaults.name", LocaleContextHolder.getLocale(),
                dateFormat.format(currentDate));
    }


    public DataDefinition getWorkPlanDD() {
        return dataDefinitionService.get(WorkPlansConstants.PLUGIN_IDENTIFIER, WorkPlansConstants.MODEL_WORK_PLAN);
    }

}
