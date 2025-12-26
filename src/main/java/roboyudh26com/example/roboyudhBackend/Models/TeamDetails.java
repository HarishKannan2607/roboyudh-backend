package roboyudh26com.example.roboyudhBackend.Models;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TeamDetails {

    @Id
    private String phoneNumber;   // Primary key (unique)
    private String fullName;
    private String email;
    private String collegeName;
    private String teamName;
    private int teamSize;         // Changed to int (IMPORTANT)
    private String event;
    private int amount;           // teamSize × 100
    private String paymentId;     // Razorpay payment ID
}
