package com.vision.computer;

import java.io.File;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import org.json.JSONObject;

/**
 *  Example program for calling ComputerVision - ReadAPI for locally stored Image and Remotely stored image
 *  Requires you to have the Azure Computer Vision Subscription
 */

class RecognizeHandTextRest {

    public static void main(String[] args) {

        String localTextImage = "src/main/resources/handwritten.pdf";
        String urlImageText = "https://userresearch.blog.gov.uk/wp-content/uploads/sites/102/2014/10/the-perfect-sticky-note-620x417.jpg";
                //"https://raw.githubusercontent.com/Azure-Samples/cognitive-services-sample-data-files/master/ComputerVision/Images/handwritten_text.jpg";

        // Gets your Computer Vision subscription key from your environment variables
        String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
        if (subscriptionKey == null) {
            System.out.println("\n\nPlease set the COMPUTER_VISION_SUBSCRIPTION_KEY environment variable." +
            "\n**Restart your shell or IDE after setting it.**\n");
            System.exit(0);
        }
        // Gets your Computer Vision endpoint from your environment variables
        String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");
        if (endpoint == null) {
            System.out.println("\n\nPlease set the COMPUTER_VISION_ENDPOINT environment variable." +
            "\n**Restart your shell or IDE after setting it.**\n");
            System.exit(0);
        }

        // Analyze local handwritten text image
        RecognizeLocalText(localTextImage, subscriptionKey);

        // Analyze remote handwritten text image
        RecognizeURLText(urlImageText, subscriptionKey);
    }

    public static void RecognizeLocalText(String image, String key) {
        System.out.println("-----------------------------------------------");
        System.out.println("HANDWRITTEN - LOCAL IMAGE");

            // byte[] localImageBytes = Files.readAllBytes(rawImage.toPath());

            CloseableHttpClient httpTextClient = HttpClientBuilder.create().build();
            CloseableHttpClient httpResultClient = HttpClientBuilder.create().build();;
            try {
                // Create API URI
                URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/vision/v2.0/read/core/asyncBatchAnalyze");
                URI uri = builder.build();
                System.out.println("URI: " + uri.toString());
    
                // Make request with URI and set the headers
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Ocp-Apim-Subscription-Key", key);
    
                // Create request body
                File imageFile = new File(image);
                FileEntity reqEntity = new FileEntity(imageFile);
                request.setEntity(reqEntity);
    
                // Call API with the client
                HttpResponse response = httpTextClient.execute(request);
    
                // Check for success.
                if (response.getStatusLine().getStatusCode() != 202) {
                    // Format and display the JSON error message.
                    HttpEntity entity = response.getEntity();
                    String jsonString = EntityUtils.toString(entity);
                    JSONObject json = new JSONObject(jsonString);
                    System.out.println("Error:\n");
                    System.out.println(json.toString(2));
                    return;
                }
    
                // Prepare to get the operation location (it's a URI with the operation ID at the end)
                String operationLocation = null;
    
                // From the response, find the "Operation-Location" header
                Header[] responseHeaders = response.getAllHeaders();
                for (Header header : responseHeaders) {
                    if (header.getName().equals("Operation-Location")) {
                        operationLocation = header.getValue();
                        break;
                    }
                }
    
                if (operationLocation == null) {
                    System.out.println("\nError retrieving Operation-Location.\nExiting.");
                    System.exit(1);
                }
    
                System.out.println("\nText submitted.\n" +
                "Waiting 10 seconds to retrieve the recognized text...\n");
                Thread.sleep(10000);
    
                // Get the entity from the 1st response
                HttpEntity entity = response.getEntity();
                // Call the second REST API method with ID and get its response.
                HttpGet resultRequest = new HttpGet(operationLocation);
                resultRequest.setHeader("Ocp-Apim-Subscription-Key", key);
    
                // Execute and get the entity of the response
                HttpResponse resultResponse = httpResultClient.execute(resultRequest);
                HttpEntity responseEntity = resultResponse.getEntity();
    
                // Print results
                if (responseEntity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(responseEntity);
                    JSONObject json = new JSONObject(jsonString);
                    System.out.println("Text recognition result response: \n");
                    System.out.println(json.toString(2));
                }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void RecognizeURLText(String imageURL, String key) {
        System.out.println();
        System.out.println("-----------------------------------------------");
        System.out.println("HANDWRITTEN - REMOTE IMAGE");

        CloseableHttpClient httpTextClient = HttpClientBuilder.create().build();
        CloseableHttpClient httpResultClient = HttpClientBuilder.create().build();;
        try {
            // Create URI
            URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/vision/v2.0/read/core/asyncBatchAnalyze");
            URI uri = builder.build();
            System.out.println("URI: " + uri.toString());

            // Make request with URI and set the headers
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", key);

            // Create request body
            String body = "{\"url\":\"" + imageURL + "\"}";
            StringEntity reqEntity = new StringEntity(body);
            request.setEntity(reqEntity);

            // Call API with client
            HttpResponse response = httpTextClient.execute(request);

            // Check for success.
            if (response.getStatusLine().getStatusCode() != 202) {
                // Format and display the JSON error message.
                HttpEntity entity = response.getEntity();
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                System.out.println("Error:\n");
                System.out.println(json.toString(2));
                return;
            }

            // Prepare to get the operation location (it's a URI with the operation ID at the end)
            String operationLocation = null;

            // From the response, find the "Operation-Location" header
            Header[] responseHeaders = response.getAllHeaders();
            for (Header header : responseHeaders) {
                if (header.getName().equals("Operation-Location")) {
                    operationLocation = header.getValue();
                    break;
                }
            }

            if (operationLocation == null) {
                System.out.println("\nError retrieving Operation-Location.\nExiting.");
                System.exit(1);
            }

            System.out.println("\nText submitted.\n" +
            "Waiting 10 seconds to retrieve the recognized text...\n");
            Thread.sleep(10000);

            // Get the entity from the 1st response
            HttpEntity entity = response.getEntity();
            // Call the second REST API method with ID and get its response.
            HttpGet resultRequest = new HttpGet(operationLocation);
            resultRequest.setHeader("Ocp-Apim-Subscription-Key", key);

            // Execute and get the entity of the response
            HttpResponse resultResponse = httpResultClient.execute(resultRequest);
            HttpEntity responseEntity = resultResponse.getEntity();

            // Print results
            if (responseEntity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(responseEntity);
                JSONObject json = new JSONObject(jsonString);
                System.out.println("Text recognition result response: \n");
                System.out.println(json.toString(2));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}


