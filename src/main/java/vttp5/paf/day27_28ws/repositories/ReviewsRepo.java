package vttp5.paf.day27_28ws.repositories;

import java.util.Optional;

import org.bson.Document;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewsRepo {

    @Autowired
    private MongoTemplate template;

    public static final String C_Reviews = "reviews2";
    
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

    
}
