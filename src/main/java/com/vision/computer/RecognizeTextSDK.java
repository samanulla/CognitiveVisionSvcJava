package com.vision.computer;

import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;

import java.io.File;
import java.nio.file.Files;


class RecognizeTextSDK {

      public static void main(String[] args) { 

        String localTextImagePath = "src/main/resources/printed_text.jpg";
        // Use a remote image for recognizing text with OCR
        String remoteTextImageURL = "https://moderatorsampleimages.blob.core.windows.net/samples/sample2.jpg";
        String remoteTextImageURL2 = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/" +
                        "Atomist_quote_from_Democritus.png/338px-Atomist_quote_from_Democritus.png";

        /**
         * AUTHENTICATE
         * Create a client that authorizes your Computer Vision subscription key and region.
         */	
        String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
        if (subscriptionKey == null) {
        System.out.println("\n\nPlease set the COMPUTER_VISION_SUBSCRIPTION_KEY environment variable." +
        "\n**You might need to restart your shell or IDE after setting it.**\n");
        System.exit(0);
        }

        String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");
        if (endpoint == null) {
        System.out.println("\n\nPlease set the COMPUTER_VISION_ENDPOINT environment variable." +
        "\n**You might need to restart your shell or IDE after setting it.**\n");
        System.exit(0);
        }

        ComputerVisionClient computerVisionClient = 
                ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
        /**
         * END - Authenticate
         */

        // Analyze local and remote text-image for recognizing text with OCR
        RecognizeTextOCR(computerVisionClient, localTextImagePath, remoteTextImageURL2);
      }



    /** 
     * RECOGNIZE PRINTED TEXT:
     * Displays text found in image with angle and orientation of the block of text.
     */
    private static void RecognizeTextOCR(ComputerVisionClient client, String localTextImagePath, String remoteTextImageURL){
        System.out.println("-----------------------------------------------");
        System.out.println("RECOGNIZE PRINTED TEXT");
        try {
        File rawImage = new File(localTextImagePath);
        byte[] localImageBytes = Files.readAllBytes(rawImage.toPath());

        // Recognize text in local image
        OcrResult ocrResultLocal = client.computerVision().recognizePrintedTextInStream()
            .withDetectOrientation(true)
            .withImage(localImageBytes)
            .withLanguage(OcrLanguages.EN)
            .execute();

        // Recognize text in remote image
        OcrResult ocrResultRemote = client.computerVision().recognizePrintedText()
            .withDetectOrientation(true)
            .withUrl(remoteTextImageURL)
            .withLanguage(OcrLanguages.EN)
            .execute();    

        OcrResult[] results = { ocrResultLocal , ocrResultRemote };

        // Print results of local and remote images
        for (OcrResult result : results){
            String location = null;
            OcrResult ocrResult = null;
            if (result == ocrResultLocal) { ocrResult = ocrResultLocal; location = "local"; }
            else { ocrResult = ocrResultRemote; location = "remote"; } 
            System.out.println();
            System.out.println("Recognizing text from " + location + " image with OCR ...");
            System.out.println("\nLanguage: " + ocrResult.language());
            System.out.printf("Text angle: %1.3f\n", ocrResult.textAngle());
            System.out.println("Orientation: " + ocrResult.orientation());

            boolean firstWord = true; 
            // Gets entire region of text block
            for (OcrRegion reg : ocrResult.regions()) {
            // Get one line in the text block
            for (OcrLine line : reg.lines()) {
                for (OcrWord word : line.words()) {
                // get bounding box of first word recognized (just to demo)
                if (firstWord) {
                    System.out.println("\nFirst word in first line is \"" + word.text() 
                        + "\" with  bounding box: " + word.boundingBox());
                    firstWord = false;
                    System.out.println();
                }
                System.out.print(word.text() + " ");
                }
                System.out.println();
            }
            }
        }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * END - Recognize Text
     */
}