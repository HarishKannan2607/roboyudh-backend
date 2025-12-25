package roboyudh26com.example.roboyudhBackend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TeamDetails {
    @Id
    private String phoneNumber;
    private String teamName;
    private String teamSize;
    private String fullName;
    private String email;
    private String event;
    private String collegeName;
}
