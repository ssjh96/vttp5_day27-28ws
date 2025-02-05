package vttp5.paf.day27_28ws.repositories;

import java.util.Optional;

import org.bson.Document;
import org.bson.json.JsonObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
public class ReviewsRepo {

    @Autowired
    private MongoTemplate template;

    public static final String C_Reviews = "reviews2";
    public static final String F_OID_Reviews = "_id";
    
    // db.reviews2.insert({
    //     user: "shamus",
    //     rating: 2.0,
    //     comment: "good stuff",
    //     ID: 123,     // from games collection
    //     posted: '2025-02-03',
    //     name: "samurai jack"    //from game collection
    // })
    public Document insertReview(Document doc)
    {
        Document result = template.insert(doc, C_Reviews);
        System.out.println(">>> result inserted is: " + result);

        return result;
    }

    // db.reviews2.find({_id: ObjectId(<reviewId>)})                also used for getting latestComment Json (part c, getmapping)
    public Optional<Document> findReviewById (String reviewId)
    {
        ObjectId reviewObjectId = new ObjectId(reviewId);

        Optional<Document> optReviewDoc = Optional.ofNullable(
                    template.findById(reviewObjectId, Document.class, C_Reviews));
        
        return optReviewDoc;
    }

    // db.reviews2.update(
    //     {_id: ObjectId("67a234b82d93e0521476f327")},
    //     { 
    //         $set: { rating: 9.9, comment: "better now", posted: "2025-02-05" },
    //         $push: { edited: {rating: 9.9, comment: "better now", posted: "2025-02-05"} } 
    //     } 
    // )
    public Long updateDocument(String reviewId, Document updateDoc) // updateDoc contains: comment, rating, posted
    {
        ObjectId reviewObjectId = new ObjectId(reviewId);

        Criteria criteria = Criteria.where(F_OID_Reviews).is(reviewObjectId);
        Query query = Query.query(criteria);

        Update updateOps = new Update()
                        .set("comment", updateDoc.getString("comment"))
                        .set("rating", updateDoc.getDouble("rating"))
                        .set("posted", updateDoc.getString("posted"))
                        .push("edited", updateDoc); // Push the whole document in directly so that it can be cast back to Doc later when taking out
                        // push into "edited" array
                        // BsonArray of Bson Objects when working w MongoDB
                        // If JsonObj constructed, need to convert to document by parsing 
        
        UpdateResult updateResult = template.updateFirst(query, updateOps, Document.class, C_Reviews); // find the objectId, send the 
        // updateFirst > Updates only the first matching document (best for updating single records by _id).
        // updateMulti > Updates all matching documents (useful for batch updates, but not needed here since _id is unique).

        return updateResult.getModifiedCount();
    }

    
}
