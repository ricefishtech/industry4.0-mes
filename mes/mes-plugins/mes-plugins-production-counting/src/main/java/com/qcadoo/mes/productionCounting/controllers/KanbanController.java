
package com.qcadoo.mes.productionCounting.controllers;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.mes.productionCounting.KanbanService;
import com.qcadoo.mes.productionCounting.response.ListResponse;
import com.qcadoo.security.api.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public final class KanbanController {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private SecurityService securityService;

    @Value("${useCompressedStaticResources}")
    private boolean useCompressedStaticResources;

    @Autowired
    private KanbanService kanbanService;


    @RequestMapping(value = "dataCountKanban", method = RequestMethod.GET)
    public ModelAndView getDataCountKanbanbanView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("userLogin", securityService.getCurrentUserName());

        mav.addObject("translationsMap", translationService.getMessagesGroup("dashboard", locale));

        mav.addObject("totalProduct", kanbanService.totalProduct());
        mav.addObject("totalTechnology", kanbanService.totalTechnology());
        mav.addObject("totalStaff", kanbanService.totalStaff());
        mav.addObject("totalProductionLine", kanbanService.totalProductionLine());
        mav.addObject("totalWarehouse", kanbanService.totalWarehouse());
        mav.addObject("totalFinalProduct", kanbanService.totalFinalProduct());
        mav.addObject("totalMaterial", kanbanService.totalMaterial());
        mav.addObject("totalMiddleware", kanbanService.totalMiddleware());
        mav.addObject("totalMasterOrder", kanbanService.totalMasterOrder());
        mav.addObject("totalProductionOrder", kanbanService.totalProductionOrder());
        mav.addObject("totalMachine", kanbanService.totalMachine());
        mav.addObject("totalRepairTime", kanbanService.totalRepairTime());

        mav.setViewName("productionCounting/dataCountKanban");
        mav.addObject("useCompressedStaticResources", useCompressedStaticResources);

        return mav;
    }

    @RequestMapping(value = "productionKanban", method = RequestMethod.GET)
    public ModelAndView getProductionKanbanView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("userLogin", securityService.getCurrentUserName());

        mav.addObject("translationsMap", translationService.getMessagesGroup("dashboard", locale));

        mav.addObject("totalPlannedProductionOrder", kanbanService.totalPlannedProductionOrder());
        mav.addObject("producedOrder", kanbanService.producedOrder());
        mav.addObject("shippedOrder", kanbanService.shippedOrder());
        mav.addObject("productionOrder", kanbanService.productionOrder());

        mav.setViewName("productionCounting/productionKanban");
        mav.addObject("useCompressedStaticResources", useCompressedStaticResources);

        return mav;
    }


    @RequestMapping(value = "getProductionOrder", method = RequestMethod.GET)
    public ModelAndView getProductionOrderView(@RequestParam final Map<String, String> arguments, final Locale locale) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("userLogin", securityService.getCurrentUserName());

        mav.addObject("translationsMap", translationService.getMessagesGroup("dashboard", locale));

        mav.addObject("totalPlannedProductionOrder", kanbanService.totalPlannedProductionOrder());
        mav.addObject("producedOrder", kanbanService.producedOrder());
        mav.addObject("shippedOrder", kanbanService.shippedOrder());
        mav.addObject("productionOrder", kanbanService.productionOrder());

        mav.setViewName("productionCounting/productionKanban");
        mav.addObject("useCompressedStaticResources", useCompressedStaticResources);

        return mav;
    }

    @ResponseBody
    @RequestMapping(value = "/getProductionOrderList", method = RequestMethod.GET)
    public ListResponse getProductionOrderList() {

        List list = kanbanService.getProductionOrderList();

        return new ListResponse(list);
    }

}
