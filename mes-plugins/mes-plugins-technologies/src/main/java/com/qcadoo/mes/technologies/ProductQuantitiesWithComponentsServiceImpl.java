
package com.qcadoo.mes.technologies;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.qcadoo.mes.technologies.constants.MrpAlgorithm;
import com.qcadoo.mes.technologies.constants.TechnologiesConstants;
import com.qcadoo.mes.technologies.constants.TechnologyFields;
import com.qcadoo.mes.technologies.dto.OperationProductComponentHolder;
import com.qcadoo.mes.technologies.dto.OperationProductComponentWithQuantityContainer;
import com.qcadoo.mes.technologies.dto.ProductQuantitiesHolder;
import com.qcadoo.mes.technologies.tree.ProductStructureTreeService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityTree;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.utils.EntityTreeUtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ProductQuantitiesWithComponentsServiceImpl implements ProductQuantitiesWithComponentsService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductQuantitiesWithComponentsServiceImpl.class);

    @Autowired
    ProductQuantitiesService productQuantitiesService;

    @Autowired
    ProductStructureTreeService productStructureTreeService;

    @Autowired
    DataDefinitionService dataDefinitionService;

    @Override
    public Map<OperationProductComponentHolder, BigDecimal> getNeededProductQuantitiesByOPC(Entity technology,
            BigDecimal givenQuantity, MrpAlgorithm mrpAlgorithm) {
        Map<Long, BigDecimal> operationRuns = Maps.newHashMap();
        Set<OperationProductComponentHolder> nonComponents = Sets.newHashSet();

        OperationProductComponentWithQuantityContainer productComponentWithQuantities = getProductComponentWithQuantitiesForTechnology(
                technology, givenQuantity, operationRuns, nonComponents);

        OperationProductComponentWithQuantityContainer allWithSameEntityType = productComponentWithQuantities
                .getAllWithSameEntityType(TechnologiesConstants.MODEL_OPERATION_PRODUCT_IN_COMPONENT);

        if (mrpAlgorithm.equals(MrpAlgorithm.ALL_PRODUCTS_IN)) {
            return getOperationProductComponentWithQuantities(allWithSameEntityType, nonComponents, false);
        } else if (mrpAlgorithm.equals(MrpAlgorithm.ONLY_COMPONENTS)) {
            return getOperationProductComponentWithQuantities(allWithSameEntityType, nonComponents, true);
        } else if (mrpAlgorithm.equals(MrpAlgorithm.COMPONENTS_AND_SUBCONTRACTORS_PRODUCTS)) {
            return getOperationProductComponentWithQuantities(allWithSameEntityType, nonComponents, false);
        } else {
            return getOperationProductComponentWithQuantities(allWithSameEntityType, nonComponents, true, true);
        }

    }

    private Map<OperationProductComponentHolder, BigDecimal> getOperationProductComponentWithQuantities(
            final OperationProductComponentWithQuantityContainer productComponentWithQuantities,
            final Set<OperationProductComponentHolder> nonComponents, final boolean onlyComponents) {
        return getOperationProductComponentWithQuantities(productComponentWithQuantities, nonComponents, onlyComponents, false);
    }

    private Map<OperationProductComponentHolder, BigDecimal> getOperationProductComponentWithQuantities(
            final OperationProductComponentWithQuantityContainer productComponentWithQuantities,
            final Set<OperationProductComponentHolder> nonComponents, final boolean onlyComponents, final boolean onlyMaterials) {
        Map<OperationProductComponentHolder, BigDecimal> productWithQuantities = Maps.newHashMap();

        for (Map.Entry<OperationProductComponentHolder, BigDecimal> productComponentWithQuantity : productComponentWithQuantities
                .asMap().entrySet()) {
            OperationProductComponentHolder operationProductComponentHolder = productComponentWithQuantity.getKey();

            if (onlyComponents && nonComponents.contains(operationProductComponentHolder)) {
                continue;
            }
            if (onlyMaterials) {
                Entity product = operationProductComponentHolder.getProduct();
                if (hasAcceptedMasterTechnology(product)) {
                    continue;
                }
            }

            addOPCQuantitiesToList(productComponentWithQuantity, productWithQuantities);
        }

        return productWithQuantities;
    }

    private boolean hasAcceptedMasterTechnology(final Entity product) {
        DataDefinition technologyDD = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODEL_TECHNOLOGY);
        Entity masterTechnology = technologyDD
                .find()
                .add(SearchRestrictions.and(SearchRestrictions.belongsTo(TechnologyFields.PRODUCT, product),
                        (SearchRestrictions.eq("state", "02accepted"))))
                .add(SearchRestrictions.eq(TechnologyFields.MASTER, true)).setMaxResults(1).uniqueResult();

        return masterTechnology != null;

    }

    public void addOPCQuantitiesToList(final Map.Entry<OperationProductComponentHolder, BigDecimal> productComponentWithQuantity,
            final Map<OperationProductComponentHolder, BigDecimal> productWithQuantities) {
        OperationProductComponentHolder operationProductComponentHolder = productComponentWithQuantity.getKey();
        BigDecimal quantity = productComponentWithQuantity.getValue();
        productWithQuantities.put(operationProductComponentHolder, quantity);
    }

    @Override
    public ProductQuantitiesHolder getProductComponentQuantities(final Entity technology, final BigDecimal givenQuantity) {
        Map<Long, BigDecimal> operationRuns = Maps.newHashMap();
        OperationProductComponentWithQuantityContainer productQuantities = getProductComponentQuantities(technology,
                givenQuantity, operationRuns);

        return new ProductQuantitiesHolder(productQuantities, operationRuns);
    }

    @Override
    public OperationProductComponentWithQuantityContainer getProductComponentQuantities(final Entity technology,
            final BigDecimal givenQuantity, final Map<Long, BigDecimal> operationRuns) {
        Set<OperationProductComponentHolder> nonComponents = Sets.newHashSet();

        return getProductComponentWithQuantitiesForTechnology(technology, givenQuantity, operationRuns, nonComponents);
    }

    @Override
    public OperationProductComponentWithQuantityContainer getProductComponentWithQuantitiesForTechnology(final Entity technology,
            final BigDecimal givenQuantity, final Map<Long, BigDecimal> operationRuns,
            final Set<OperationProductComponentHolder> nonComponents) {
        OperationProductComponentWithQuantityContainer operationProductComponentWithQuantityContainer = new OperationProductComponentWithQuantityContainer();

        EntityTree operationComponents = productStructureTreeService.getOperationComponentsFromTechnology(technology);
        technology.setField(TechnologyFields.OPERATION_COMPONENTS,
                EntityTreeUtilsService.getDetachedEntityTree(operationComponents));

        Entity root = technology.getTreeField(TechnologyFields.OPERATION_COMPONENTS).getRoot();
        Map<Long, Entity> entitiesById = new LinkedHashMap<Long, Entity>();

        for (Entity entity : operationComponents) {
            entitiesById.put(entity.getId(), entity);
        }
        if (root != null) {
            productQuantitiesService.preloadProductQuantitiesAndOperationRuns(operationComponents,
                    operationProductComponentWithQuantityContainer, operationRuns);
            productQuantitiesService.traverseProductQuantitiesAndOperationRuns(technology, entitiesById, givenQuantity, root,
                    null, operationProductComponentWithQuantityContainer, nonComponents, operationRuns);
        }

        return operationProductComponentWithQuantityContainer;
    }

    @Override
    public Map<Long, BigDecimal> getNeededProductQuantities(final Entity technology, final BigDecimal givenQuantity,
            final MrpAlgorithm mrpAlgorithm) {
        Map<Long, BigDecimal> operationRuns = Maps.newHashMap();
        Set<OperationProductComponentHolder> nonComponents = Sets.newHashSet();

        OperationProductComponentWithQuantityContainer productComponentWithQuantities = getProductComponentWithQuantitiesForTechnology(
                technology, givenQuantity, operationRuns, nonComponents);

        return productQuantitiesService.getProductWithQuantities(productComponentWithQuantities, nonComponents, mrpAlgorithm,
                TechnologiesConstants.MODEL_OPERATION_PRODUCT_IN_COMPONENT);
    }

}
