package api.client;

import constants.Url;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseClient {
    /**
     * Метод-билдер создает общую реквест-спецификацию
     */
    public RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(Url.URL)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }
}
