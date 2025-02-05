package vttp5.paf.day27_28ws.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import vttp5.paf.day27_28ws.services.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api")
public class ReviewsController 
{
    @Autowired
    private ReviewService reviewService;

    // localhost:8080/api/review
    // gid: 1
    // user: Shaun
    // rating: 10.0
    // comment: "blah blah" (optional)
    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postReview(@RequestBody MultiValueMap<String, String> reviewData) // name, rating, comment (optional), game id
    {
        System.out.println(">>>>> reviewData: " + reviewData);
        JsonObject jResult = reviewService.createReview(reviewData);
        System.out.println(">>>>> jResult: " + jResult);

        if (jResult.containsKey("error"))
        {
            return ResponseEntity.badRequest().body(jResult.toString());
        }

        return ResponseEntity.ok(jResult.toString());
    }


    // localhost:8080/api/review/67a234b82d93e0521476f327
    // {
    //     "comment": "not bad lah",
    //     "rating": 8.8
    // }    
    @PutMapping(path = "review/{review_id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateReview(@PathVariable ("review_id") String reviewId, @RequestBody String jUpdateStr) 
    {
        System.out.println(">>>>> jUpdateStr: " + jUpdateStr);
        JsonObject jResult = reviewService.updateReview(reviewId, jUpdateStr);
        System.out.println(">>>>> jResult: " + jResult);

        if (jResult.containsKey("error"))
        {
            return ResponseEntity.badRequest().body(jResult.toString());
        }

        return ResponseEntity.ok(jResult.toString());
    }

    
    @GetMapping(path = "/review/{review_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLatestReview(@PathVariable ("review_id") String reviewId) {
        
        JsonObject jResult = reviewService.getLatestComment(reviewId);
        System.out.println(">>>>> jResult: " + jResult);

        if (jResult.containsKey("error"))
        {
            return ResponseEntity.badRequest().body(jResult.toString());
        }

        return ResponseEntity.ok(jResult.toString());
    }
    

    @GetMapping(path = "/review/{review_id}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviewHistory(@PathVariable ("review_id") String reviewId) {
        
        JsonObject jResult = reviewService.getReviewHistory2(reviewId);
        System.out.println(">>>>> jResult: " + jResult);

        if (jResult.containsKey("error"))
        {
            return ResponseEntity.badRequest().body(jResult.toString());
        }

        return ResponseEntity.ok(jResult.toString());
    }
}
