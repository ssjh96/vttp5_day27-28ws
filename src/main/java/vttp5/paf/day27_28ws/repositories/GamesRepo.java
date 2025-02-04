package vttp5.paf.day27_28ws.repositories;

import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class GamesRepo {
    
    @Autowired
    private MongoTemplate template;

    private static final String C_GAMES = "games";
    private static final String F_GID = "gid";  

    // Check if game exist

    // db.games.find(
    //     {gid: 1}
    // )
    public Optional<Document> findGameById (int gid)
    {
        Criteria criteria = Criteria.where(F_GID).is(gid);

        Query query = Query.query(criteria);

        Document result = template.findOne(query, Document.class, C_GAMES); // findOne returns a single doc | find returns a List<Doc>

        return Optional.ofNullable(result);
    }
}
