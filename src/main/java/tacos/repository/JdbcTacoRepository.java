package tacos.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import tacos.domain.Ingredient;
import tacos.domain.Taco;

//import java.util.*;
import java.sql.Date;
import java.util.Map;

@Repository
public class JdbcTacoRepository implements TacoRepository {


  private JdbcTemplate jdbc;

//  @Autowired
  private ObjectMapper objectMapper;

//  @Autowired
  private SimpleJdbcInsert tacoInserter;

  private SimpleJdbcInsert tacoOrderInserter;

  @Autowired
  public JdbcTacoRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
    this.tacoInserter = new SimpleJdbcInsert(jdbc)
            .withTableName("taco")
            .usingGeneratedKeyColumns("id");

    this.objectMapper = new ObjectMapper();
    this.tacoOrderInserter = new SimpleJdbcInsert(jdbc).withTableName("taco_ingredients");
  }

  @Override
  public Taco save(Taco taco) {
    taco.setCreatedAt(new Date(System.currentTimeMillis()));
    long tacoId = saveTacoInfo(taco);
    taco.setId(tacoId);
    for (Ingredient ingredient : taco.getIngredients()) {
      saveIngredientToTaco(ingredient, tacoId);
    }

    return taco;
  }

  private long saveTacoInfo(Taco taco) {
    Map<String, Object> values = objectMapper.convertValue(taco, Map.class);
    values.put("createdAt", taco.getCreatedAt());

    long tacoId = tacoInserter.executeAndReturnKey(values).longValue();

    return tacoId;
//    taco.setCreatedAt(new Date());
//    PreparedStatementCreator psc =
//        new PreparedStatementCreatorFactory(
//            "insert into Taco (name, createdAt) values (?, ?)",
//            Types.VARCHAR, Types.TIMESTAMP
//        ).newPreparedStatementCreator(
//            Arrays.asList(
//                taco.getName(),
//                new Timestamp(taco.getCreatedAt().getTime())));
//
//    KeyHolder keyHolder = new GeneratedKeyHolder();
//    jdbc.update(psc, keyHolder);
//
//    return keyHolder.getKey().longValue();
  }

  private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
//
//    Map<String, Object> values = new HashMap<>();
//    values.put("taco", tacoId);
//    values.put("ingredient", ingredient.getId());
//    tacoOrderInserter.execute(values);
    jdbc.update(
            "insert into Taco_Ingredients (taco, ingredient) " +
                    "values (?, ?)",
            tacoId, ingredient.getId());
  }

}
