/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.repository.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Setter
@Getter
@Document(indexName = "translations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TranslationDocument {

    @Id
    String id;

    @Field(type = FieldType.Keyword)
    String key;

    @Field(type = FieldType.Text)
    String value;

    @Field(type = FieldType.Keyword)
    String locale;

    @Field(type = FieldType.Keyword)
    String tag;

    @Field(type = FieldType.Long)
    Long createdAt;

    @Field(type = FieldType.Long)
    Long updatedAt;

}
