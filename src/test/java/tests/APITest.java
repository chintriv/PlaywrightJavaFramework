package tests;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class APITest {

    @Test
    public void e2eApiTest(){
        // Login
        HashMap<Object,Object> loginPayload = new HashMap<>();
        loginPayload.put("email","rahulshetty1@yahoo.com");
        loginPayload.put("password","Magiclife1!");


        Playwright playwright =  Playwright.create();
        APIRequestContext apiRequest =  playwright.request().newContext();
        APIResponse loginResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/auth/login", RequestOptions.create().setData(loginPayload));
        Assert.assertTrue(loginResponse.ok());
        System.out.println(loginResponse.text());

        String token = JsonPath.read(loginResponse.text(), "$.token");
        System.out.println("Login success = "+token);

        // Create event
        String eventTitle = "Playwright API Testing";
        HashMap<Object,Object> createEventPayload = new HashMap<>();
        createEventPayload.put("title",eventTitle);
        createEventPayload.put("description","api");
        createEventPayload.put("category","Conference");
        createEventPayload.put("venue","Main Road");
        createEventPayload.put("city","Bangalore");
        createEventPayload.put("eventDate","2026-10-15T09:00:00.000Z");
        createEventPayload.put("price",100);
        createEventPayload.put("totalSeats",501);

        APIResponse eventResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/events", RequestOptions.create().setHeader("Authorization", "Bearer "+token).setData(createEventPayload));
        Assert.assertTrue(eventResponse.ok(), "Create event API success");
        int eventId = JsonPath.read(eventResponse.text(),"$.data.id");
        System.out.println("Event created with ID "+eventId);

        //get event
        APIResponse retrieveEvents = apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events", RequestOptions.create().setQueryParam("pages", "1").setQueryParam("limit","12").setHeader("Authorization", "Bearer "+token).setData(createEventPayload));
        Assert.assertTrue(retrieveEvents.ok(), "event retrieve success");
        //System.out.println(retrieveEvents.text());

        List<Integer> allEventIds = JsonPath.read(retrieveEvents.text(),"$.data[*].id");
        Assert.assertTrue(allEventIds.contains(eventId),"created event appears in the list");

        //Delete event
        APIResponse deleteResponse = apiRequest.delete("https://api.eventhub.rahulshettyacademy.com/api/events/"+eventId, RequestOptions.create().setHeader("Authorization", "Bearer "+token));
        Assert.assertTrue(deleteResponse.ok());

        //verify delete worked
        APIResponse verifyRes = apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + token)
                        .setQueryParam("pages", "1")
                        .setQueryParam("limit","12"));
        Assert.assertTrue(verifyRes.ok(), "post delete events list call success");

        List<String> idsAfterDelete = JsonPath.read(verifyRes.text(), "$.data[*].id");
        Assert.assertFalse(idsAfterDelete.contains(eventId),"deleted event no longer available");
        System.out.println("Deletion verified");

    }
}
