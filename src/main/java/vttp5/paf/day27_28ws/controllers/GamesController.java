package vttp5.paf.day27_28ws.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import vttp5.paf.day27_28ws.services.GamesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/game")
public class GamesController {

    @Autowired
    private GamesService gameService;

    // localhost:8080/game/1
    @GetMapping(path = "/{gid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviewsForGid (@PathVariable int gid) 
    {
        JsonObject jResult = gameService.getReviewsForGid(gid);

        if (jResult.containsKey("error"))
        {
            return ResponseEntity.badRequest().body(jResult.toString());
        }

        return ResponseEntity.ok(jResult.toString());
    }

    // localhost:8080/game/highest
    @GetMapping(path = "/highest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHighestRatings() 
    {
        JsonObject jResult = gameService.getHighestRatingInfos();

        return ResponseEntity.ok(jResult.toString());
    }

    // localhost:8080/game/lowest
    @GetMapping(path = "/lowest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLowestRating() 
    {
        JsonObject jResult = gameService.getLowestRatingInfos();

        return ResponseEntity.ok(jResult.toString());
    }
    
    
}
