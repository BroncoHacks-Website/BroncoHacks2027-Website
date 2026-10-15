package org.broncohacks.portal.signupsigninendpoints;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignUpController {

    @PostMapping("/signup")
    public String checkSignUp(@RequestBody SignUpRequest request){

        //Checks first if email and password request are empty
        if(request.getEmail() == null || request.getPassword() == null){
            return "Email or Password cannot be empty";
        }

        //regex that checks for valid email format
        else if(request.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            return "Valid Email format";
        }
        else{
            return "Invalid Email format";
        }
    }

}
