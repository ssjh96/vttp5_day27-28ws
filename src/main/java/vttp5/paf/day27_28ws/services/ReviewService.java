package vttp5.paf.day27_28ws.services;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp5.paf.day27_28ws.repositories.GamesRepo;
import vttp5.paf.day27_28ws.repositories.ReviewsRepo;

@Service
public class ReviewService 
{
    @Autowired
    private GamesRepo gamesRepo;
    
    @Autowired
    private ReviewsRepo reviewsRepo;

    public JsonObject createReview(MultiValueMap<String, String> reviewData) // gid, user, rating, comment (optional)
    {
        // Check if game exist 
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
        String user = reviewData.getFirst("user");
        String comment = reviewData.getFirst("comment"); // for checking if comment is posted in from payload

        // Validate the rating
        Double rating;
        try {
            rating = Double.parseDouble(reviewData.getFirst("rating"));

            if (rating < 0 || rating > 10) 
            {
                JsonObject jError = Json.createObjectBuilder()
                                        .add("error", "rating must be between 0 to 10.")
                                        .build();
                
                return jError;
            }
        }
        catch (NumberFormatException e)
        {
            return Json.createObjectBuilder().add("error", "invalid rating value").build();
        }
        
        // Create review JSON then parse to Document
        JsonObject jReviewToInsert = Json.createObjectBuilder()
                                .add("user", user)
                                .add("rating", rating)
                                .add("comment", comment != null ? comment : "") // check if comment is null, ensures optional commenting
                                // .add("comment", Optional.ofNullable(comment).orElse(""))
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



    public JsonObject updateReview(String reviewId, String jUpdateStr) // jsonString contains: comment & rating, requires additional timestamp
    {
        // Search for review by given review oid
        Optional<Document> optReviewDoc = reviewsRepo.findReviewById(reviewId);
        if (optReviewDoc.isEmpty())
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No review found for oid: %s".formatted(reviewId))
                                    .build();
            
            return jError;
        }

        // parse the jsonString payload as a doc
        Document updateDoc = Document.parse(jUpdateStr); 

        // Validate 0-10 rating
        Double newRating = updateDoc.getDouble("rating");
        if (newRating < 0 || newRating > 10) 
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "New rating must be between 0 to 10.")
                                    .build();
            
            return jError;
        }
        
        // Append the timestamp to the document
        updateDoc.append("posted", LocalDateTime.now().toString()); // append the timestamp in

        // update the existing
        long modifiedCount = reviewsRepo.updateDocument(reviewId, updateDoc);
        
        if (modifiedCount == 0)
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No modification done")
                                    .build();
            
            return jError;
        }
        
        JsonObject jSuccess = Json.createObjectBuilder()
                                    .add("success", "Update completed")
                                    .build();
            
        return jSuccess;
    }



    public JsonObject getLatestComment (String reviewId)
    {
        // Search for review by given review oid
        Optional<Document> optReviewDoc = reviewsRepo.findReviewById(reviewId);
        if (optReviewDoc.isEmpty())
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No review found for oid: %s".formatted(reviewId))
                                    .build();
            
            return jError;
        }

        Document reviewDoc = optReviewDoc.get();
        System.out.println(">>>>> Latest: " + reviewDoc);

        // {
        //     user: <name form field>,
        //     rating: <latest rating>,
        //     comment: <latest comment>,
        //     ID: <game id form field>,
        //     posted: <date>,
        //     name: <The board game’s name as per ID>,
        //     edited: <true or false depending on edits>,
        //     timestamp: <result timestamp>
        // }

        // Extract attributes from review Doc
        String user = reviewDoc.getString("user");
        Double rating = reviewDoc.getDouble("rating");
        String comment = reviewDoc.getString("comment");
        int ID = reviewDoc.getInteger("ID");
        String posted = reviewDoc.getString("posted");
        String name = reviewDoc.getString("name");
        // List<Document> editedList = reviewDoc.getList("edited", Document.class); // this is to get List<Documents since edited is an array of bson objects
        // it allows iteration over the list or get specific elements, e.g. Document lastestEdit = editedList.get(editedList.size() - 1); // last element
        Boolean edited = reviewDoc.containsKey("edited");
        String timestamp = LocalDateTime.now().toString();

        // Create jsonResult
        JsonObject jResult = Json.createObjectBuilder()
                                .add("user", user)
                                .add("rating", rating)
                                .add("comment", comment)
                                .add("ID", ID)
                                .add("posted", posted)
                                .add("name", name)
                                .add("edited", edited)
                                .add("timestamp", timestamp)
                                .build();

        return jResult;
    }


    public JsonObject getReviewHistory1 (String reviewId)
    {
        // Search for review by given review oid
        Optional<Document> optReviewDoc = reviewsRepo.findReviewById(reviewId);
        if (optReviewDoc.isEmpty())
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No review found for oid: %s".formatted(reviewId))
                                    .build();
            
            return jError;
        }


        Document reviewDoc = optReviewDoc.get();
        System.out.println(">>>>> Latest: " + reviewDoc);

        // {
        //     user: <name form field>,
        //     rating: <latest rating>,
        //     comment: <latest comment>,
        //     ID: <game id form field>,
        //     posted: <date>,
        //     name: <The board game’s name as per ID>,
        //     edited: [
        //     { comment: ..., rating: ..., posted: ... },
        //     { comment: ..., rating: ..., posted: ... },
        //     { comment: ..., rating: ..., posted: ... }
        //     ],
        //     timestamp: <result timestamp>
        // }

        // Extract attributes from review Doc
        String user = reviewDoc.getString("user");
        Double rating = reviewDoc.getDouble("rating");
        String comment = reviewDoc.getString("comment");
        int ID = reviewDoc.getInteger("ID");
        String posted = reviewDoc.getString("posted");
        String name = reviewDoc.getString("name");
        // List<Document> editedList = reviewDoc.getList("edited", Document.class); // this is to get List<Documents since edited is an array of bson objects
        // it allows iteration over the list or get specific elements, e.g. Document lastestEdit = editedList.get(editedList.size() - 1); // last element
        String timestamp = LocalDateTime.now().toString();

        // Check if the review was edited
        Boolean bEdited = reviewDoc.containsKey("edited");

        // if edit history is present
        if(bEdited)
        {
            JsonArrayBuilder jab = Json.createArrayBuilder();
            
            List<Document> editedList = reviewDoc.getList("edited", Document.class);
            for (Document edit : editedList)
            {
                JsonObject jEdit = Json.createObjectBuilder()
                                        .add("rating", edit.getDouble("rating"))
                                        .add("comment", edit.getString("comment"))
                                        .add("posted", edit.getString("posted"))
                                        .build();
                
                jab.add(jEdit);
            }
            
            // Create jsonResult for edited
            JsonObject jResult = Json.createObjectBuilder()
                    .add("user", user)
                    .add("rating", rating)
                    .add("comment", comment)
                    .add("ID", ID)
                    .add("posted", posted)
                    .add("name", name)
                    .add("edited", jab.build())
                    .add("timestamp", timestamp)
                    .build();

            return jResult;
        }

        // Create jsonResult for unedited
        JsonObject jResult = Json.createObjectBuilder()
                .add("user", user)
                .add("rating", rating)
                .add("comment", comment)
                .add("ID", ID)
                .add("posted", posted)
                .add("name", name)
                .add("edited", "no edits")
                .add("timestamp", timestamp)
                .build();

        return jResult;
    }

    

    public JsonObject getReviewHistory2 (String reviewId)
    {
        // Search for review by given review oid
        Optional<Document> optReviewDoc = reviewsRepo.findReviewById(reviewId);
        if (optReviewDoc.isEmpty())
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No review found for oid: %s".formatted(reviewId))
                                    .build();
            
            return jError;
        }

        Document reviewDoc = optReviewDoc.get();
        System.out.println(">>>>> Latest: " + reviewDoc);

        // Remove "_id" to match expected JSON format
        reviewDoc.remove("_id"); // .remove() returns an object, {"_id" : 123}, which is the removedValue object and reviewDoc returns without "_id"
        System.out.println(">>>>> After reviewDoc remove _id: " + reviewDoc);

        // Check if the review was edited then return the result
        Boolean bEdited = reviewDoc.containsKey("edited");
        String jResultStr; 

        if(!bEdited)
        {
            // Append necessary info and return jResultStr
            jResultStr = reviewDoc
                .append("edited", "no edits")
                .append("timestamp", LocalDateTime.now().toString())
                .toJson();
        }

        jResultStr = reviewDoc
                    .append("timestamp", LocalDateTime.now().toString())
                    .toJson();

        JsonReader jReader = Json.createReader(new StringReader(jResultStr));
        JsonObject jResult = jReader.readObject();

        return jResult;
    }

    // Extra info
    // If you want _id to appear as a plain string, manually convert it before calling .toJson():
    // Convert ObjectId to String before converting to JSON
    // reviewDoc.put("_id", reviewDoc.getObjectId("_id").toHexString());



    

}
