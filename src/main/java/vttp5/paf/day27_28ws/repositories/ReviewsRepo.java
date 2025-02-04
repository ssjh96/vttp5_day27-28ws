package vttp5.paf.day27_28ws.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewsRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    public static final String C_Reviews = "reviews2";
    


    
}
