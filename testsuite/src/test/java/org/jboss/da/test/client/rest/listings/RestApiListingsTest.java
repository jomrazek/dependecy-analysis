package org.jboss.da.test.client.rest.listings;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.jboss.da.communication.model.GAV;
import org.jboss.da.test.client.AbstractRestApiTest;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestApiListingsTest extends AbstractRestApiTest {

    private enum ListType {
        BLACK, WHITE
    }

    private static String ENCODING = "utf-8";

    private static String PATH_FILES_LISTINGS_GAV = "/listings";

    private static String PATH_WHITE_LIST = "/listings/whitelist";

    private static String PATH_BLACK_LIST = "/listings/blacklist";

    private static String PATH_WHITE_LISTINGS_GAV = "/listings/whitelist/gav";

    private static String PATH_BLACK_LISTINGS_GAV = "/listings/blacklist/gav";
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Before
    public void dropTables() throws Exception{
        List<GAV> whitelistedArtifacts = getAllArtifactsFromList(PATH_WHITE_LIST);
        whitelistedArtifacts.forEach(gav -> removeGavFromList(PATH_WHITE_LIST, gav));
        
        List<GAV> blacklistedArtifacts = getAllArtifactsFromList(PATH_BLACK_LIST);
        blacklistedArtifacts.forEach(gav -> removeGavFromList(PATH_WHITE_LIST, gav));
        
    }
    
    private void removeGavFromList(String listUrl, GAV gav) {
        try {
            createClientRequest(listUrl, mapper.writeValueAsString(gav));
        } catch (JsonProcessingException e) {
            fail("Failed to remove GAV from the list using URL " + listUrl);
        }
    }
    

    private List<GAV> getAllArtifactsFromList(String listUrl) throws Exception {
        return processGetRequest(
                new GenericType<List<GAV>>(){}, restApiURL + listUrl);
    }
    
    private <T> T processGetRequest(GenericType<T> type, String url) throws Exception {
        ClientRequest request = new ClientRequest(url);
        request.accept(MediaType.APPLICATION_JSON);

        ClientResponse<T> response = request.get(type);

        if (response.getStatus() != 200)
            fail("Failed to get entity via REST API");

        return response.getEntity();
    }

    
    @Ignore
    @Test
    public void testAddWhiteArtifact() throws Exception {
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON, "succes")
                .getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testAddNonRHWhiteArtifact() throws Exception {
        String type = "gav";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);

        assertEquals(400, response.getStatus());
    }

    @Ignore
    @Test
    public void testAddBlackArtifact() throws Exception {
        String type = "gav";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON, "succes")
                .getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testAddBlackRHArtifact() throws Exception {
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON, "succes")
                .getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testAddBlackNonOSGiArtifact() throws Exception {
        String type = "gavNonOSGi";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON, "succes")
                .getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testAddBlacklistedArtifactToWhitelist() throws Exception {
        // Add artifact to blacklist
        String type = "gav";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);

        assertEquals(200, response.getStatus());
        // Try to add artifact to whitelist
        type = "gavRh";
        jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        response = request.post(String.class);

        assertEquals(409, response.getStatus());
    }

    @Ignore
    @Test
    public void testAddWhitelistedArtifactToBlacklist() throws Exception {
        // Add artifact to whitelist
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());
        // Add artifact to blacklist
        type = "gav";
        jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "succesmessage").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testAddMultipleTimeWhitelistedArtifactToBlacklist() throws Exception {
        // Add artifacts to whitelist
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());

        type = "gavRh2";
        jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        response = request.post(String.class);
        assertEquals(200, response.getStatus());
        // Add artifact to blacklist
        type = "gav";
        jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "succesmessage").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testAlreadyAddedWhiteArtifact() throws Exception {
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());

        response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "succesfalse").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testAlreadyAddedBlackArtifact() throws Exception {
        String type = "gav";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());

        response = request.post(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "succesfalse").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testDeleteWhiteArtifact() throws Exception {
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());

        response = request.delete(String.class);
        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON, "succes")
                .getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testDeleteBlackArtifact() throws Exception {
        String type = "gav";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());

        response = request.delete(String.class);
        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON, "succes")
                .getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testDeleteNonExistingWhiteArtifact() throws Exception {
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.delete(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "succesfalse").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testDeleteNonExistingBlackArtifact() throws Exception {
        String type = "gav";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.delete(String.class);

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "succesfalse").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testCheckRHWhiteArtifact() throws Exception {
        String type = "gavRh";
        addArtifact(ListType.WHITE, type);

        ClientResponse<String> response = new ClientRequest(restApiURL + PATH_WHITE_LIST
                + "?groupid=org.jboss.da&artifactid=dependency-analyzer&version=0.3.0.redhat-1")
                .get();

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "gavrhr<esponse").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    /**
     * Non RedHat but OSGi compliant white artifact test
     * @throws Exception 
     */
    @Test
    public void testCheckNonRHWhiteArtifact() throws Exception {
        String type = "gav";
        addArtifact(ListType.WHITE, type);

        ClientResponse<String> response = new ClientRequest(restApiURL + PATH_WHITE_LIST
                + "?groupid=org.jboss.da&artifactid=dependency-analyzer&version=0.3.0").get();

        System.out.println(restApiURL + PATH_WHITE_LIST
                + "?groupid=org.jboss.da&artifactid=dependency-analyzer&version=0.3.0");

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "gavrhresponse").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    /**
     * Non RedHat non OSGi compliant white artifact test
     */
    @Ignore
    @Test
    public void testCheckNonRHNonOSGiWhiteArtifact() {

    }

    @Ignore
    @Test
    public void testCheckRHBlackArtifact() {

    }

    /**
     * Non RedHat but OSGi compliant black artifact test
     */
    @Ignore
    @Test
    public void testCheckNonRHBlackArtifact() {

    }

    /**
     * Non RedHat non OSGi compliant black artifact test
     */
    @Ignore
    @Test
    public void testCheckNonRHNonOSGiBlackArtifact() {

    }

    @Ignore
    @Test
    public void testGetAllWhiteArtifacts() throws Exception {
        // Add artifacts to whitelist
        String type = "gavRh";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());

        type = "gavRh2";
        jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        request = createClientRequest(PATH_WHITE_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        response = request.post(String.class);
        assertEquals(200, response.getStatus());
        // Get list

        response = new ClientRequest(restApiURL + PATH_WHITE_LIST).get();

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "gavwhitelist").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    @Ignore
    @Test
    public void testGetAllBlackArtifacts() throws Exception {
        // Add artifacts to blacklist
        String type = "gav";
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        ClientRequest request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());

        type = "gav2";
        jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);

        request = createClientRequest(PATH_BLACK_LISTINGS_GAV,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        response = request.post(String.class);
        assertEquals(200, response.getStatus());
        // Get list

        response = new ClientRequest(restApiURL + PATH_BLACK_LIST).get();

        File expectedResponseFile = new ExpectedResponseFilenameBuilder(
                restApiExpectedResponseFolder, PATH_FILES_LISTINGS_GAV, APPLICATION_JSON,
                "gavblacklist").getFile();
        assertEquals(readFileToString(expectedResponseFile).trim(), response
                .getEntity(String.class).trim());
        assertEquals(200, response.getStatus());
    }

    private void addArtifact(ListType list, String file) throws Exception {
        String type = file;
        File jsonRequestFile = getJsonRequestFile(PATH_FILES_LISTINGS_GAV, type);
        String path = null;
        switch (list) {
            case WHITE:
                path = PATH_WHITE_LISTINGS_GAV;
                break;
            case BLACK:
                path = PATH_BLACK_LISTINGS_GAV;
                break;
        }
        ClientRequest request = createClientRequest(path,
                FileUtils.readFileToString(jsonRequestFile, ENCODING));

        ClientResponse<String> response = request.post(String.class);
        assertEquals(200, response.getStatus());
    }

    private File getJsonRequestFile(String path, String variant) {
        return new RequestFilenameBuilder(restApiRequestFolder, path, APPLICATION_JSON, variant)
                .getFile();
    }

    private ClientRequest createClientRequest(String relativePath, String jsonRequest) {
        ClientRequest request = new ClientRequest(restApiURL + relativePath);
        request.header("Content-Type", APPLICATION_JSON);
        request.body(MediaType.APPLICATION_JSON_TYPE, jsonRequest);
        return request;
    }
}
