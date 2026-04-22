package com.example.demo.odata;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericEdmProvider extends CsdlAbstractEdmProvider {

    public static final String NAMESPACE = "com.example.demo";
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    private final ODataEntityRegistry registry;

    public GenericEdmProvider(ODataEntityRegistry registry) {
        this.registry = registry;
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        return registry.getAll().stream()
                .filter(d -> new FullQualifiedName(NAMESPACE, d.getEntityName()).equals(entityTypeName))
                .findFirst()
                .map(d -> new CsdlEntityType()
                        .setName(d.getEntityName())
                        .setProperties(d.getODataProperties())
                        .setKey(Collections.singletonList(new CsdlPropertyRef().setName(d.getKeyPropertyName()))))
                .orElse(null);
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        if (!entityContainer.equals(CONTAINER)) return null;
        return registry.getAll().stream()
                .filter(d -> d.getEntitySetName().equals(entitySetName))
                .findFirst()
                .map(d -> new CsdlEntitySet().setName(d.getEntitySetName())
                        .setType(new FullQualifiedName(NAMESPACE, d.getEntityName())))
                .orElse(null);
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        List<CsdlEntitySet> entitySets = new ArrayList<>();
        for (ODataEntityDescriptor<?> d : registry.getAll()) {
            entitySets.add(new CsdlEntitySet()
                    .setName(d.getEntitySetName())
                    .setType(new FullQualifiedName(NAMESPACE, d.getEntityName())));
        }
        return new CsdlEntityContainer().setName(CONTAINER_NAME).setEntitySets(entitySets);
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        List<CsdlEntityType> entityTypes = new ArrayList<>();
        for (ODataEntityDescriptor<?> d : registry.getAll()) {
            entityTypes.add(getEntityType(new FullQualifiedName(NAMESPACE, d.getEntityName())));
        }
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);
        schema.setEntityTypes(entityTypes);
        schema.setEntityContainer(getEntityContainer());
        return Collections.singletonList(schema);
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            return new CsdlEntityContainerInfo().setContainerName(CONTAINER);
        }
        return null;
    }
}
