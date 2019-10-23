package com.vision.computer;

import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 *  Example program for calling ComputerVision - ReadAPI for locally stored Image and Remotely stored image
 *  Requires you to have the Azure Computer Vision Subscription
 */

public class OCRTextRecognizerRest {
    // **********************************************
    // *** Update or verify the following values. ***
    // **********************************************

    public static void main(String[] args) {

        // Add your Computer Vision subscription key and endpoint to your environment variables.
        // After setting, close and then re-open your command shell or project for the changes to take effect.

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

        String uriBase = endpoint +
                "/vision/v2.1/ocr";

        String imageToAnalyze =
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/" +
                        "Atomist_quote_from_Democritus.png/338px-Atomist_quote_from_Democritus.png";

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder uriBuilder = new URIBuilder(uriBase);

            uriBuilder.setParameter("language", "unk");
            uriBuilder.setParameter("detectOrientation", "true");

            // Request parameters.
            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body.
            StringEntity requestEntity =
                    new StringEntity("{\"url\":\"" + imageToAnalyze + "\"}");
            request.setEntity(requestEntity);

            // Call the REST API method and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                System.out.println("REST Response:\n");
                System.out.println(json.toString(2));
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
    }
}