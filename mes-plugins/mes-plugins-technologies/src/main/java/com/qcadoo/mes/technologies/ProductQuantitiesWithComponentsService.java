
package com.qcadoo.mes.technologies;

import com.qcadoo.mes.technologies.constants.MrpAlgorithm;
import com.qcadoo.mes.technologies.dto.OperationProductComponentHolder;
import com.qcadoo.mes.technologies.dto.OperationProductComponentWithQuantityContainer;
import com.qcadoo.mes.technologies.dto.ProductQuantitiesHolder;
import com.qcadoo.model.api.Entity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface ProductQuantitiesWithComponentsService {

    ProductQuantitiesHolder getProductComponentQuantities(Entity technology, BigDecimal givenQuantity);

    OperationProductComponentWithQuantityContainer getProductComponentQuantities(Entity technology, BigDecimal givenQuantity,
                                                                                 Map<Long, BigDecimal> operationRuns);

    OperationProductComponentWithQuantityContainer getProductComponentWithQuantitiesForTechnology(Entity technology,
                                                                                                  BigDecimal givenQuantity, Map<Long, BigDecimal> operationRuns, Set<OperationProductComponentHolder> nonComponents);

    Map<Long, BigDecimal> getNeededProductQuantities(Entity technology, BigDecimal givenQuantity, MrpAlgorithm mrpAlgorithm);


    /**
     * @param technology    Given technology
     * @param givenQuantity How many products, that are outcomes of this technology, we want.
     * @param mrpAlgorithm  MRP Algorithm
     */
    Map<OperationProductComponentHolder, BigDecimal> getNeededProductQuantitiesByOPC(final Entity technology, final BigDecimal givenQuantity,
                                                                                     final MrpAlgorithm mrpAlgorithm);

}