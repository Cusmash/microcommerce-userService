package com.bread.userservice.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import java.util.Map;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import graphql.schema.DataFetchingEnvironment;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof CustomGraphQLException customEx) {
            return GraphqlErrorBuilder.newError(env)
                    .message(customEx.getMessage())
                    .errorType(graphql.ErrorType.ValidationError)
                    .extensions(Map.of("code", customEx.getCode()))
                    .build();
        }

        return null;
    }
}