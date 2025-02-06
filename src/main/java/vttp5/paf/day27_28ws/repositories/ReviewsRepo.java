package vttp5.paf.day27_28ws.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.json.JsonObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
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
    public static final String C_Games = "games";
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

    

    


    // // part b
    // db.getCollection("reviews2").aggregate([
    //     // Sort reviews by rating in dsc order (highest first)
    //     { $sort: {rating: -1} },
    //     // join with 'games' collection
    //     { $lookup: { from: 'games', localField: 'ID', foreignField: 'gid', as: 'games' } },
    //     // flatten the 'games' array (Since each review only got 1 matching game)
    //     { $unwind: '$games'},
    //     // group by 'ID' and keep only the highest rating (prevents duplicate highest ratings)
    //     { $group: { 
    //         _id: '$ID',
    //         name: { $first: '$name' }, 
    //         rating: { $first: '$rating' }, 
    //         user: { $first: '$user' }, 
    //         comment: { $first: '$comment' },
    //         review_id: { $first: '$_id'}
    //         } 
    //     }  
    // ])

    // db.getCollection("reviews2").aggregate([
    //     // Sort reviews by rating in dsc order (highest first)
    //     { $sort: {rating: -1} },
    //     // join with 'games' collection
    //     { $lookup: { from: 'games', localField: 'ID', foreignField: 'gid', as: 'games' } },
    //     // flatten the 'games' array (Since each review only got 1 matching game)
    //     { $unwind: '$games'},
    //     // group by 'ID' and keep only the highest rating (prevents duplicate highest ratings)
    //     { $group: { 
    //         _id: '$ID',
    //         name: { $first: '$name' }, 
    //         rating: { $first: '$rating' }, 
    //         user: { $first: '$user' }, 
    //         comment: { $first: '$comment' },
    //         review_id: { $first: '$_id'}
    //         } 
    //     },
    //     // Group all selected games into a single array
    //     // group requires _id to be set
    //     // _id: null since we dont need a group identifier
    //     { $group: {
    //         _id: null, // no grouping key, just collect all results
    //         games: {$push: '$$ROOT'} // '$$ROOT' means the entire document up to this stage
    //         }
    //     },
    //     // add new fields
    //     { $addFields: {
    //         rating: 'highest', 
    //         timestamp: {
    //             $dateToString: {
    //                 format: "%Y-%m-%dT%H:%M:%S", 
    //                 date: new Date() }
    //             }
    //         }
    //     },
    //     { $project: { _id:0 } } // exclude the null _id
    // ])
        
    // db.getCollection("reviews2").aggregate([
    //     // Sort reviews by rating in asc order (lowest first)
    //     { $sort: {rating: 1} },
    //     // join with 'games' collection
    //     { $lookup: { from: 'games', localField: 'ID', foreignField: 'gid', as: 'games' } },
    //     // flatten the 'games' array (Since each review only got 1 matching game)
    //     { $unwind: '$games'},
    //     // group by 'ID' and keep only the highest rating (prevents duplicate highest ratings)
    //     { $group: { 
    //         _id: '$ID',
    //         name: { $first: '$name' }, 
    //         rating: { $first: '$rating' }, 
    //         user: { $first: '$user' }, 
    //         comment: { $first: '$comment' },
    //         review_id: { $first: '$_id'},
    //         } 
    //     }
    // ])


    
}
