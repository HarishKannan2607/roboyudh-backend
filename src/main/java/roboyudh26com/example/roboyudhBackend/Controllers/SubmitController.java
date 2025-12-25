package roboyudh26com.example.roboyudhBackend.Controllers;

import org.springframework.web.bind.annotation.*;
import roboyudh26com.example.roboyudhBackend.Models.TeamDetails;
import roboyudh26com.example.roboyudhBackend.Repositories.teamDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/roboyudh26")
@CrossOrigin(
        origins = {"http://127.0.0.1:5500", "http://localhost:5500"}
)
public class SubmitController {
    @Autowired
    teamDetailsRepo teamDetailsRepo;

    @PostMapping("/submitDetails")
    public ResponseEntity<?> submitDetails(@RequestBody TeamDetails teamDetails) {
        String phoneNumber = teamDetails.getPhoneNumber();
        if(teamDetailsRepo.existsById(phoneNumber)){
            return  ResponseEntity.status(409).body("Number already exists");
        }
        TeamDetails savedData = teamDetailsRepo.save(teamDetails);
        return new ResponseEntity<>(savedData, HttpStatus.OK);
    }

}
