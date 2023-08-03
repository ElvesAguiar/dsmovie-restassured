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
import static org.hamcrest.CoreMatchers.*;

public class MovieControllerRA {

	private Map<String,Object> postMovieObject;

	private Long existingId,nonExistingId;

	private String clientUsername,adminUsername;
	private String clientPassword,adminPassword;

	private String clientToken,adminToken,invalidToken;

	@BeforeEach
	void setUp() throws Exception{
		clientUsername="alex@gmail.com";
		clientPassword="123456";
		adminUsername="maria@gmail.com";
		adminPassword="123456";
		postMovieObject=new HashMap<>();

		postMovieObject.put("title","Test Movie");
		postMovieObject.put("score", 0.0F);
		postMovieObject.put("count", 0);
		postMovieObject.put( "image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");

		adminToken= TokenUtil.obtainAccessToken(adminUsername,adminPassword);
		clientToken=TokenUtil.obtainAccessToken(clientUsername,clientPassword);
		invalidToken="#$"+adminToken+"@%";

		baseURI ="http://localhost:8080";
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
	given()
			.get("/movies")
			.then()
			.statusCode(200);
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {

		given()
				.get("/movies?title=Titanic")
				.then()
				.statusCode(200)
				.body("content.id[0]", is(7))
				.body("content.title",hasItems("Titanic"))
				.body("content.score[0]",is(0.0F))
				.body("content.count[0]",is(0))
				.body("content.image[0]",equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/yDI6D5ZQh67YU4r2ms8qcSbAviZ.jpg"))

		;
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		existingId=1L;
		given()
				.get("/movies/{id}",existingId)
				.then()
				.statusCode(200)
				.body("id", is(existingId.intValue()))
				.body("title",equalTo("The Witcher"))
				.body("score",is(4.5F))
				.body("count",is(2))
				.body("image",equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		nonExistingId=100L;
		given()
				.get("/movies/{id}",nonExistingId)
				.then()
				.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws Exception {
		postMovieObject.put("title","");
		JSONObject postBody= new JSONObject(postMovieObject);
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(postBody)
			.when()
				.post("/movies")
			.then()
				.statusCode(422);

	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

		JSONObject postBody= new JSONObject(postMovieObject);
		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(postBody)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject postBody= new JSONObject(postMovieObject);
		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(postBody)
				.when()
				.post("/movies")
				.then()
				.statusCode(401);
	}
}
