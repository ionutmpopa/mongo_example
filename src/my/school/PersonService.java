import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void updateAgeInBatches(List<Person> allPersons, int batchSize, int newAge) {
        // Split your documents into batches
        List<List<Person>> batches = splitIntoBatches(allPersons, batchSize);

        // Process batches in parallel
        batches.parallelStream().forEach(batch -> {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, Person.class);

            // Build a query to update the 'age' field in the current batch
            Query query = new Query(Criteria.where("age").exists(true).in(batch));

            // Update the 'age' field with the new value
            Update update = new Update().set("age", newAge);

            // Add the update operation to the bulk operations
            bulkOps.updateMulti(query, update);

            // Execute the bulk operations
            bulkOps.execute();
        });
    }

    private List<List<Person>> splitIntoBatches(List<Person> allPersons, int batchSize) {
        // Split the list into batches of the specified size
        int totalSize = allPersons.size();
        int fromIndex = 0;

        while (fromIndex < totalSize) {
            int toIndex = Math.min(fromIndex + batchSize, totalSize);
            yield allPersons.subList(fromIndex, toIndex);
            fromIndex = toIndex;
        }
    }
}
