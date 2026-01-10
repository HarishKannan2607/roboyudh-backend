package roboyudh26com.example.roboyudhBackend.Controllers;

import com.razorpay.RazorpayException;
import org.springframework.web.bind.annotation.*;

import roboyudh26com.example.roboyudhBackend.Models.TeamDetails;
import roboyudh26com.example.roboyudhBackend.Repositories.teamDetailsRepo;
import roboyudh26com.example.roboyudhBackend.Services.RazorpayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import roboyudh26com.example.roboyudhBackend.Services.RazorpayService;

import java.util.Map;

@RestController
@RequestMapping("/roboyudh26")
@CrossOrigin(
        origins = {
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "https://roboyudh2026.vercel.app",
                "https://roboyudh2026-epc1.vercel.app"

        }
)
public class SubmitController {

    @Autowired
    private teamDetailsRepo teamDetailsRepo;

    @Autowired
    private RazorpayService razorpayService;

    /* ===============================
       1️⃣ CREATE PAYMENT ORDER
       =============================== */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestBody Map<String, Object> data)
            throws RazorpayException {

        int teamSize = Integer.parseInt(data.get("teamSize").toString());
        String event = (String) data.get("event");

        int amount = razorpayService.calculateAmount(event, teamSize);

        return ResponseEntity.ok(
                razorpayService.createOrder(amount).toString()
        );
    }


    /* ==========================================
       2️⃣ VERIFY PAYMENT + SAVE REGISTRATION
       ========================================== */
    @PostMapping("/verify-and-save")
    public ResponseEntity<?> verifyAndSave(
            @RequestBody Map<String, Object> requestData) {

        // Payment details
        Map<String, String> payment =
                (Map<String, String>) requestData.get("payment");

        // Registration details
        Map<String, String> reg =
                (Map<String, String>) requestData.get("registration");

        boolean isValid = razorpayService.verifyPayment(
                payment.get("razorpay_order_id"),
                payment.get("razorpay_payment_id"),
                payment.get("razorpay_signature")
        );

        if (!isValid) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Payment verification failed");
        }

        String phoneNumber = reg.get("phoneNumber");

        // ❌ Prevent duplicate AFTER payment
        if (teamDetailsRepo.existsById(phoneNumber)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Phone number already exists");
        }

        // ✅ Recalculate amount safely
        int teamSize = Integer.parseInt(reg.get("teamSize"));
        String event = reg.get("event");

        int amount = razorpayService.calculateAmount(event, teamSize);


        // ✅ Save registration
        TeamDetails teamDetails = new TeamDetails();
        teamDetails.setFullName(reg.get("fullName"));
        teamDetails.setTeamSize(teamSize);
        teamDetails.setEmail(reg.get("email"));
        teamDetails.setCollegeName(reg.get("collegeName"));
        teamDetails.setTeamName(reg.get("teamName"));
        teamDetails.setPhoneNumber(phoneNumber);
        teamDetails.setEvent(reg.get("event"));
        teamDetails.setPaymentId(payment.get("razorpay_payment_id"));
        teamDetails.setAmount(amount);

        TeamDetails saved = teamDetailsRepo.save(teamDetails);

        return new ResponseEntity<>(saved, HttpStatus.OK);
    }
}
