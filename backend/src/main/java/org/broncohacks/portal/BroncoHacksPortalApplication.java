package org.broncohacks.portal;

import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


// TODO:
// Implement DTOs for standardized JSON response for team requests 
//
@SpringBootApplication
public class BroncoHacksPortalApplication {
    //Some placeholder database structure. Replace with PostgreSQL when necessary.
    public static ArrayList<User> databaseUsers = new ArrayList<User>();
    public static ArrayList<Team> databaseTeams = new ArrayList<Team>();


    public static void main(String[] args) {
        //adds testUser to the database

        ApplicationContext context = SpringApplication.run(BroncoHacksPortalApplication.class, args);
    }
        
}