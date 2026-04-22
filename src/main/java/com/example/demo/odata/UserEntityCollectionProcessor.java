package com.example.demo.odata;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.*;
import org.apache.olingo.server.api.uri.*;
import org.apache.olingo.server.api.uri.queryoption.*;
import org.apache.olingo.server.api.uri.queryoption.expression.*;

import java.util.List;
import java.util.stream.Collectors;

public class UserEntityCollectionProcessor implements EntityCollectionProcessor {

    private OData odata;
    private ServiceMetadata serviceMetadata;
    private final UserRepository userRepository;

    public UserEntityCollectionProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public void readEntityCollection(ODataRequest request, ODataResponse response,
                                     UriInfo uriInfo, ContentType responseFormat)
            throws ODataApplicationException, ODataLibraryException {

        EdmEntitySet edmEntitySet = ((UriResourceEntitySet) uriInfo.getUriResourceParts().get(0)).getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        List<User> users = userRepository.findAll();

        // $filter
        FilterOption filterOption = uriInfo.getFilterOption();
        if (filterOption != null) {
            Expression expression = filterOption.getExpression();
            UserExpressionVisitor visitor = new UserExpressionVisitor();
            users = users.stream().filter(user -> {
                try {
                    visitor.setCurrentUser(user);
                    return Boolean.TRUE.equals(expression.accept(visitor));
                } catch (ExpressionVisitException | ODataApplicationException e) {
                    return false;
                }
            }).collect(Collectors.toList());
        }

        // $orderby
        OrderByOption orderByOption = uriInfo.getOrderByOption();
        if (orderByOption != null) {
            UserExpressionVisitor visitor = new UserExpressionVisitor();
            for (int i = orderByOption.getOrders().size() - 1; i >= 0; i--) {
                OrderByItem item = orderByOption.getOrders().get(i);
                boolean desc = item.isDescending();
                users.sort((u1, u2) -> {
                    try {
                        visitor.setCurrentUser(u1);
                        Object val1 = item.getExpression().accept(visitor);
                        visitor.setCurrentUser(u2);
                        Object val2 = item.getExpression().accept(visitor);
                        if (val1 instanceof Comparable && val2 instanceof Comparable) {
                            @SuppressWarnings("unchecked")
                            int cmp = ((Comparable<Object>) val1).compareTo(val2);
                            return desc ? -cmp : cmp;
                        }
                        return 0;
                    } catch (Exception e) {
                        return 0;
                    }
                });
            }
        }

        // $skip
        SkipOption skipOption = uriInfo.getSkipOption();
        if (skipOption != null) {
            int skip = skipOption.getValue();
            users = users.subList(Math.min(skip, users.size()), users.size());
        }

        // $top
        TopOption topOption = uriInfo.getTopOption();
        if (topOption != null) {
            int top = topOption.getValue();
            users = users.subList(0, Math.min(top, users.size()));
        }

        // Build entity collection
        EntityCollection entityCollection = new EntityCollection();
        for (User user : users) {
            entityCollection.getEntities().add(toEntity(user));
        }

        // Serialize
        SelectOption selectOption = uriInfo.getSelectOption();
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet)
                .selectList(odata.createUriHelper().buildContextURLSelectList(edmEntityType, null, selectOption))
                .build();

        EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
                .contextURL(contextUrl)
                .select(selectOption)
                .build();

        SerializerResult result = odata.createSerializer(responseFormat)
                .entityCollection(serviceMetadata, edmEntityType, entityCollection, opts);

        response.setContent(result.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    public static Entity toEntity(User user) {
        Entity entity = new Entity();
        entity.addProperty(new Property(null, "id", ValueType.PRIMITIVE, user.getId()));
        entity.addProperty(new Property(null, "name", ValueType.PRIMITIVE, user.getName()));
        entity.addProperty(new Property(null, "surname", ValueType.PRIMITIVE, user.getSurname()));
        entity.addProperty(new Property(null, "role", ValueType.PRIMITIVE,
                user.getRole() != null ? user.getRole().name() : null));
        return entity;
    }
}
