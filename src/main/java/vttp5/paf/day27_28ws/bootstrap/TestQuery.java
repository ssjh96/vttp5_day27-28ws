package vttp5.paf.day27_28ws.bootstrap;

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
        }

        Document review = optReview.get();

        System.out.printf(">>> Review with ObjectId: %s found.\n", "67a234802d93e0521476f326");
        System.out.println(">>>>> " + review.toJson());
    }
    
}
