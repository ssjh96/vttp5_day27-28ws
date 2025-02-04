package vttp5.paf.day27_28ws.bootstrap;

import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import vttp5.paf.day27_28ws.repositories.GamesRepo;

@Component
public class TestQuery implements CommandLineRunner 
{
    @Autowired 
    private GamesRepo gamesRepo;

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println(">>> Test searching game by gid...");
        Optional<Document> optGame = gamesRepo.findGameById(1);

        if (optGame.isEmpty())
        {
            System.out.println(">>> Error no game found..");
        }

        Document game = optGame.get();

        System.out.printf(">>> Game with gid: %s found.", 1);
        System.out.println(">>>>> " + game.toJson());

    }
    
}
