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
        List<List<Person>> batches = new ArrayList<>();

        int totalSize = allPersons.size();
        int fromIndex = 0;

        while (fromIndex < totalSize) {
            int toIndex = Math.min(fromIndex + batchSize, totalSize);
            batches.add(allPersons.subList(fromIndex, toIndex));
            fromIndex = toIndex;
        }

        return batches;
    }

    public void updateAgeInBatches(int batchSize, int newAge) {
        Query query = new Query(Criteria.where("age").exists(true));

        Iterator<Person> iterator = mongoTemplate.stream(query, Person.class)
            .iterator();

        while (iterator.hasNext()) {
            List<Person> batch = new ArrayList<>(batchSize);

            for (int i = 0; i < batchSize && iterator.hasNext(); i++) {
                batch.add(iterator.next());
            }

            updateBatch(batch, newAge);
        }
    }

    private void updateBatch(List<Person> batch, int newAge) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, Person.class);

        Query query = new Query(Criteria.where("id").in(batch.stream().map(Person::getId).collect(Collectors.toList())));
        Update update = new Update().set("age", newAge);

        bulkOps.updateMulti(query, update);
        bulkOps.execute();
    }

    public void updateAgeInBatchesParallel(int batchSize, int newAge) {
        Query query = new Query(Criteria.where("age").exists(true));

        Iterator<Person> iterator = mongoTemplate.stream(query, Person.class).iterator();

        StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                true) // Enable parallel processing
            .map(batch -> {
                List<Person> batchList = new ArrayList<>(batchSize);
                batch.forEach(batchList::add);
                return batchList;
            })
            .forEachOrdered(batch -> updateBatch(batch, newAge));
    }
}
