package com.qcadoo.mes.productionCounting.constants;

/**
 * Created by alex on 2017/6/4.
 */
public final class AnomalyFields {

    private AnomalyFields() {

    }

    public static final String NUMBER = "number";

    public static final String PRODUCTION_TRACKING = "productionTracking";

    public static final String LOCATION = "location";

    public static final String MASTER_PRODUCT = "masterProduct";

    public static final String PRODUCT = "product";

    public static final String USED_QUANTITY = "usedQuantity";

    public static final String ANAOMALY_REASONS = "anomalyReasons";

    public static final String STATE = "state";

    public static final String ISSUED = "issued";

    public static final class State {

        public static final String DRAFT = "01draft";

        public static final String EXPLAINED = "02explained";

        public static final String COMPLETED = "03completed";

    }

}

