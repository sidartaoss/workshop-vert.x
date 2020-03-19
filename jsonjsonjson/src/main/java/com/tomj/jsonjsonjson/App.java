package com.tomj.jsonjsonjson;

import io.vertx.core.json.JsonObject;

public class App {

    public static void main(String[] args) {
        String jsonString = "{\"name\": \"Tom\"}";

//        JsonObject jsonObject = new JsonObject(jsonString);
        JsonObject jsonObject = new JsonObject();

        jsonObject.put("name", "Fred")
                .put("location", "San Francisco")
                .put("address", "100 A Street");

        System.out.println("Json = " + jsonObject.toString());
        System.out.println("name = " + jsonObject.getString("name"));

        MyItem myItem = new MyItem();
        myItem.setName("Tom");
        myItem.setDescription("Programmer");

        JsonObject jsonObject1 = JsonObject.mapFrom(myItem);

//        jsonObject1.put("myItem", myItem);

        System.out.println("myItem = " + jsonObject1.toString());

        MyItem myItem1 = jsonObject1.mapTo(MyItem.class);

        System.out.println("myItem1.name = " + myItem1.getName());
    }
}
