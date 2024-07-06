package my.school;

import java.util.Arrays;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import com.mongodb.client.AggregateIterable;

public class MongoCleanUp {

    /*
     * Requires the MongoDB Java Driver.
     * https://mongodb.github.io/mongo-java-driver
     */

    MongoClient mongoClient = new MongoClient(
        new MongoClientURI(
            "mongodb+srv://imp:00000000000.fcyi7hd.mongodb.net/"
        )
    );
    MongoDatabase database = mongoClient.getDatabase("geospatial");
    MongoCollection<Document> collection = database.getCollection("centers");

    AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
            new Document("timestamp",
                new Document("$lte", "2024-01-01T00:00:00.00Z"))),
        new Document("$sort",
            new Document("timestamp", 1L)),
        new Document("$limit", 1000L),
        new Document("$set",
            new Document("eventSavedAt",
                new java.util.Date(1704067200000L))),
        new Document("$merge",
            new Document("into", "centers")
                .append("on", "_id")
                .append("whenMatched", "merge"))));

}

//db.centers.aggregate(
//    [
//    {
//    $match:
//    {
//    timestamp: {
//    $lte: "2024-01-01T00:00:00.00Z"
//    }
//    }
//    },
//    {
//    $sort:
//    {
//    timestamp: 1
//    }
//    },
//    {
//    $limit:
//    1000000
//    },
//    {
//    $set:
//    {
//    eventSavedAt: ISODate(
//    "2024-01-01T00:00:00.000+00:00"
//    )
//    }
//    },
//    {
//    $merge:
//    {
//    into: "centers",
//    on: "_id",
//    whenMatched: "merge"
//    }
//    }
//    ]
//    )
