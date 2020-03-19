package io.vertx.workshop.portfolio;

import io.vertx.core.json.JsonObject;

/**
 * Converter for {@link com.pluralsight.dockerproductionaws.portfolio.Portfolio}.
 *
 * NOTE: This class has been automatically generated from the {@link com.pluralsight.dockerproductionaws.portfolio.Portfolio} original class using Vert.x codegen.
 */
public class PortfolioConverter {

  public static void fromJson(JsonObject json, Portfolio obj) {
    if (json.getValue("cash") instanceof Number) {
      obj.setCash(((Number)json.getValue("cash")).doubleValue());
    }
    if (json.getValue("shares") instanceof JsonObject) {
      java.util.Map<String, java.lang.Integer> map = new java.util.LinkedHashMap<>();
      json.getJsonObject("shares").forEach(entry -> {
        if (entry.getValue() instanceof Number)
          map.put(entry.getKey(), ((Number)entry.getValue()).intValue());
      });
      obj.setShares(map);
    }
  }

  public static void toJson(Portfolio obj, JsonObject json) {
    json.put("cash", obj.getCash());
    if (obj.getShares() != null) {
      JsonObject map = new JsonObject();
      obj.getShares().forEach((key,value) -> map.put(key, value));
      json.put("shares", map);
    }
  }
}
