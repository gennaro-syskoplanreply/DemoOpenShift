package com.example.demo.odata;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserEdmProvider extends CsdlAbstractEdmProvider {

    public static final String NAMESPACE = "com.example.demo";
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);
    public static final String ET_USER_NAME = "User";
    public static final FullQualifiedName ET_USER_FQN = new FullQualifiedName(NAMESPACE, ET_USER_NAME);
    public static final String ES_USERS_NAME = "Users";

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        if (!entityTypeName.equals(ET_USER_FQN)) return null;

        return new CsdlEntityType()
                .setName(ET_USER_NAME)
                .setProperties(Arrays.asList(
                        new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName()).setNullable(false),
                        new CsdlProperty().setName("name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false),
                        new CsdlProperty().setName("surname").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false),
                        new CsdlProperty().setName("role").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()).setNullable(false)
                ))
                .setKey(Collections.singletonList(new CsdlPropertyRef().setName("id")));
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        if (!entityContainer.equals(CONTAINER) || !entitySetName.equals(ES_USERS_NAME)) return null;
        return new CsdlEntitySet().setName(ES_USERS_NAME).setType(ET_USER_FQN);
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        return new CsdlEntityContainer()
                .setName(CONTAINER_NAME)
                .setEntitySets(Collections.singletonList(new CsdlEntitySet().setName(ES_USERS_NAME).setType(ET_USER_FQN)));
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);
        schema.setEntityTypes(Collections.singletonList(getEntityType(ET_USER_FQN)));
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
