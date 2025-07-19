/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.repository;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import se.digitaltolk.translation.repository.entity.TranslationDocument;

public interface TranslationRepository extends ReactiveElasticsearchRepository<TranslationDocument, String> {

}
