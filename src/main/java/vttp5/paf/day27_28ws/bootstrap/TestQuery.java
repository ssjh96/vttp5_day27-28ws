package vttp5.paf.day27_28ws.bootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import vttp5.paf.day27_28ws.repositories.GamesRepo;
import vttp5.paf.day27_28ws.repositories.ReviewsRepo;

@Component
public class TestQuery implements CommandLineRunner 
{
    @Autowired 
    private GamesRepo gamesRepo;

    @Autowired 
    private ReviewsRepo reviewsRepo;

    @Override
    public void run(String... args) throws Exception {
        
        // Testing findGameById
        System.out.println(">>> Test searching game by gid...");
        Optional<Document> optGame = gamesRepo.findGameById(1);

        if (optGame.isEmpty())
        {
            System.out.println(">>> Error no game found..");
            // handle empty with exit or smth
        }

        Document game = optGame.get();

        System.out.printf(">>> Game with gid: %s found.\n", 1);
        System.out.println(">>>>> " + game.toJson());

        // Testing findReviewById
        System.out.println(">>> Test searching review by ObjectId...");
        Optional<Document> optReview = reviewsRepo.findReviewById("67a234802d93e0521476f326");

        if (optReview.isEmpty())
        {
            System.out.println(">>> Error no review found..");
            // handle empty with exit or smth
        }

        Document review = optReview.get();

        System.out.printf(">>> Review with ObjectId: %s found.\n", "67a234802d93e0521476f326");
        System.out.println(">>>>> " + review.toJson());


        //
        List<String> test = Arrays.asList("hello" + "kan wo", "ni zai" + "hai pai semo");
        System.out.println(">>> test: \n\n" + test);

        Document testGameReviews = gamesRepo.getGameReviewsByGid(1);
        System.out.println(">>> Test game reviews when gid=1: \n\n" + testGameReviews);
        System.out.println(">>> Test game reviews (JSON) when gid=1: \n\n" + testGameReviews.toJson());
    }
    
}
