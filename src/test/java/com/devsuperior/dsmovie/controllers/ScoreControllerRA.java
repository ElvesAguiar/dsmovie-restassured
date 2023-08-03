package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;


public class ScoreControllerRA {

    private Map<String, Object> putScoreObject;

    private Long existingId, nonExistingId;
    private String clientUsername, adminUsername;
    private String clientPassword, adminPassword;
    private String clientToken, adminToken, invalidToken;

    @BeforeEach
    void setUp() throws Exception {
        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
        putScoreObject = new HashMap<>();

        putScoreObject.put("movieId", 1);
        putScoreObject.put("score", 5);

        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        invalidToken = "#$" + adminToken + "@%";

        baseURI = "http://localhost:8080";
    }

    @Test
    public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {

        nonExistingId = 100L;
        putScoreObject.put("movieId", 100L);
        JSONObject putBody = new JSONObject(putScoreObject);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(putBody)
                .when()
                .put("/scores")
                .then()
                .statusCode(404);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {

        putScoreObject.put("movieId", null);
        JSONObject putBody = new JSONObject(putScoreObject);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(putBody)
                .when()
                .put("/scores")
                .then()
                .statusCode(422);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {

        putScoreObject.put("score", -5);
        JSONObject putBody = new JSONObject(putScoreObject);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(putBody)
                .when()
                .put("/scores")
                .then()
                .statusCode(422);
    }
}
