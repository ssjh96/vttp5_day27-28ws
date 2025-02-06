package vttp5.paf.day27_28ws.repositories;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators.Concat;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

@Repository
public class GamesRepo {
    
    @Autowired
    private MongoTemplate template;

    private static final String C_GAMES = "games";
    private static final String C_REVIEWS = "reviews2";
    private static final String F_GID = "gid";  

    // Check if game exist

    // db.games.find(
    //     {gid: 1}
    // )
    public Optional<Document> findGameById (int gid)
    {
        Criteria criteria = Criteria.where(F_GID).is(gid);

        Query query = Query.query(criteria);

        Document result = template.findOne(query, Document.class, C_GAMES); // findOne returns a single doc | find returns a List<Doc>

        return Optional.ofNullable(result);
    }

    // WS28
    // part a
    // db.getCollection("games").aggregate([
    //     // match the game gid -> finds game with gid: 1
    //     { $match: { gid: 1 } }, 
    //     // join 'reviews2' with 'games' collection to get all related reviews
    //     { $lookup: { from: 'reviews2', localField: 'gid', foreignField: 'ID', as:'reviews'} },
    //     // flatten the review array, converts reviews array into individual docs for averaging
    //     { $unwind: '$reviews' },
    //     // compute average rating using $group, group by gid
    //     { $group: {
    //         _id: 'gid', 
    //         name: { $first: '$name' }, 
    //         year: { $first: '$year' }, 
    //         rank: { $first: '$ranking' }, 
    //         average: { $avg: '$reviews.rating'},
    //         users_rated: { $first: '$users_rated'},
    //         url: { $first: '$url' },
    //         thumbnail: { $first: '$image' },
    //         // use toString to convert review objectId to hexstring id for $concat
    //         reviews: {$push: { $concat:['/review/', { $toString: '$reviews._id' } ] } },
    //         } 
    //     },
    //     // reconstruct the data with $project
    //     { $project: {
    //         _id: 0,
    //         // if all below omit, and just exclude _id with _id: 0, all other fields will actually show
    //         name: 1,
    //         year: 1,
    //         rank: 1,
    //         average: 1,
    //         users_rated: 1,
    //         url: 1,
    //         thumbnail: 1,
    //         reviews: 1,
    //         // Added timestamp (maybe add with java code)
    //         timestamp: {$dateToString: { format: "%Y-%m-%dT%H:%M:%S", date: new Date() } }
    //         }
    //     }   
    // ])
    public Document getGameReviewsByGid(int gid)
    {
        // Match given gid
        Criteria criteria = Criteria.where(F_GID).is(gid);
        MatchOperation matchGid = Aggregation.match(criteria);

        // lookup (join 'games' & 'reviews2')
        LookupOperation joinReviewsCollection = Aggregation.lookup(C_REVIEWS, "gid", "ID", "reviews");

        // unwind 'reviews' array (Flatten reviews for averaging)
        UnwindOperation unwindReviews = Aggregation.unwind("reviews");

        // Group by gid, compute avg rating, collect review links
        GroupOperation groupByGid = Aggregation.group(F_GID)
                                            .first("name").as("name")
                                            .first("year").as("year")
                                            .first("ranking").as("rank")
                                            .avg("reviews.rating").as("average")
                                            .first("users_rated").as("users_rated")
                                            .first("url").as("url")
                                            .first("image").as("thumbnail")

                                            .push(
                                                StringOperators.Concat.stringValue("/review/").concatValueOf(ConvertOperators.ToString.toString("$reviews._id"))
                                                // ("reviews._id")
                                            ).as("reviews");
        
        ProjectionOperation excludeOid = Aggregation.project().andExclude("_id");

        Aggregation pipieline = Aggregation.newAggregation(matchGid, joinReviewsCollection, unwindReviews, groupByGid, excludeOid);

        AggregationResults<Document> aggResults = template.aggregate(pipieline, C_GAMES, Document.class);

        Document docResults = aggResults.getUniqueMappedResult();

        return docResults.append("timestamp", LocalDateTime.now().toString());
    }
}
