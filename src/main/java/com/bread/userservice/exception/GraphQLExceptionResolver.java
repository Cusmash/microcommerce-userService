package com.bread.userservice.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable exception, DataFetchingEnvironment env) {
        if (exception instanceof CustomGraphQLException e) {
            return GraphqlErrorBuilder.newError(env)
                    .message(e.getMessage())
                    .errorType(graphql.ErrorType.ValidationError)
                    .extensions(Map.of("code", e.getCode()))
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .message("Internal server error")
                .errorType(graphql.ErrorType.DataFetchingException)
                .build();
    }
}