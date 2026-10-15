package org.broncohacks.portal.signupsigninendpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInController {
    //Sample username and password for debugging/testing
    String correctEmail = "jose@gmail.com";
    String correctPass = "1234koopa";

    @PostMapping("/signin")
    public String checkSignIn(@RequestBody SignInRequest request){

        //Checks for a valid email and password (as in: user's profile should already exist)
        //POST request done with Postman for testing
        if(request.getEmail() == null || request.getPassword() == null){
            return "Email or Password cannot be empty";
        }

        else if(request.getEmail().equals(correctEmail) && request.getPassword().equals(correctPass)){
            return "Login Success";
        }

        else{
            return "Invalid Email or Password";
        }
    }
}
