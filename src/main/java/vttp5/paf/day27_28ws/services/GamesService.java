package vttp5.paf.day27_28ws.services;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp5.paf.day27_28ws.repositories.GamesRepo;

@Service
public class GamesService {

    @Autowired
    private GamesRepo gamesRepo;

    // WS28 part a
    public JsonObject getReviewsForGid (int gid)
    {
        Optional<Document> optDoc = gamesRepo.findGameById(gid);
        if (optDoc.isEmpty())
        {
            return Json.createObjectBuilder()
                        .add("error", "No game found for given gid: %d.".formatted(gid))
                        .build();
        }
        
        Document allReviewsDoc = gamesRepo.getGameReviewsByGid(gid);

        String jAllReviewsStr = allReviewsDoc.toJson();
        JsonObject jAllReview = Json.createReader(new StringReader(jAllReviewsStr)).readObject();

        return jAllReview;
    }


    // part b-1
    public JsonObject getHighestRatingInfos()
    {
        List<Document> allHighestRatedDoc = gamesRepo.getHighestRatingInEachGame();

        JsonArrayBuilder jab = Json.createArrayBuilder();


        for (Document d : allHighestRatedDoc)
        {
            jab.add(Json.createReader(new StringReader(d.toJson())).readObject()); // jab add jsonObj
        }

        JsonObject jResult = Json.createObjectBuilder()
                                .add("rating", "highest")
                                .add("games", jab)
                                .add("timestamp", LocalDateTime.now().toString())
                                .build();

        return jResult;
    }


    // part b-2
    public JsonObject getLowestRatingInfos()
    {
        List<Document> allLowestRatedDoc = gamesRepo.getLowestRatingInEachGame();

        JsonArrayBuilder jab = Json.createArrayBuilder();


        for (Document d : allLowestRatedDoc)
        {
            jab.add(Json.createReader(new StringReader(d.toJson())).readObject()); // jab add jsonObj
        }

        JsonObject jResult = Json.createObjectBuilder()
                                .add("rating", "lowest")
                                .add("games", jab)
                                .add("timestamp", LocalDateTime.now().toString())
                                .build();

        return jResult;
    }
}
