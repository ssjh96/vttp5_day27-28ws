package vttp5.paf.day27_28ws.services;

import java.time.LocalDate;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp5.paf.day27_28ws.repositories.GamesRepo;
import vttp5.paf.day27_28ws.repositories.ReviewsRepo;

@Service
public class ReviewService 
{
    @Autowired
    private GamesRepo gamesRepo;
    
    @Autowired
    private ReviewsRepo reviewsRepo;

    public JsonObject createReview(MultiValueMap<String, String> reviewData)
    {
        int gid = Integer.parseInt(reviewData.getFirst("gid"));

        Optional<Document> optGameDoc = gamesRepo.findGameById(gid);

        if (optGameDoc.isEmpty())
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No game found for gid: %d".formatted(gid))
                                    .build();
            
            return jError;
        }

        // db.reviews2.insert({
        //     user: "shamus",
        //     rating: 2.0,
        //     comment: "good stuff",
        //     ID: 123,     // from games collection
        //     posted: '2025-02-03',
        //     name: "samurai jack"    //from game collection
        // })

        Document gameDoc = optGameDoc.get();
        
        JsonObject jReviewToInsert = Json.createObjectBuilder()
                                .add("user", reviewData.getFirst("user"))
                                .add("rating", Double.parseDouble(reviewData.getFirst("rating")))
                                .add("comment", reviewData.getFirst("comment"))
                                .add("ID", gid)
                                .add("posted", LocalDate.now().toString())
                                .add("name", gameDoc.getString("name"))
                                .build();
        
        Document docReviewToInsert = Document.parse(jReviewToInsert.toString());

        reviewsRepo.insertReview(docReviewToInsert);

        // Return success response
        JsonObject jSuccess = Json.createObjectBuilder()
                    .add("success", "Review Submmited")
                    .build();

        return jSuccess;
    }
}
